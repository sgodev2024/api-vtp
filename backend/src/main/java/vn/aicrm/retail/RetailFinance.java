package vn.aicrm.retail;

import java.util.*;
import java.math.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import static vn.aicrm.retail.RetailAccess.*;

@Service
public class RetailFinance {
  private final RetailAccess r;private final RetailCommissions commissions;
  public RetailFinance(RetailAccess r,RetailCommissions commissions){this.r=r;this.commissions=commissions;}
  public UUID open(Authentication a,Map<String,Object> doc,String kind,BigDecimal amount){
    var snapshot=r.node(doc.get("metadata")).path("partnerSnapshot");String term=kind.equals("AR")?"sale_days":"purchase_days";java.time.LocalDate dueDate=snapshot.has(term)?snapshot.hasNonNull(term)?today().plusDays(snapshot.path(term).asInt()):null:today().plusDays(30);
    UUID id=UUID.randomUUID();r.db.update("insert into retail.open_item(id,tenant_id,branch_id,partner_id,document_id,kind,amount,due_date) values(?,?,?,?,?,?,?,?)",id,r.tenant(a),doc.get("branch_id"),doc.get("partner_id"),doc.get("id"),kind,amount,dueDate);return id;
  }
  public UUID payment(Authentication a,UUID branch,UUID partner,UUID doc,String kind,BigDecimal amount,String method,String reference){
    if(amount.signum()<=0||amount.scale()>0)throw problem("INVALID_AMOUNT","Số tiền phải là số nguyên VND lớn hơn 0");
    UUID id=UUID.randomUUID();r.db.update("insert into retail.payment(id,tenant_id,branch_id,partner_id,document_id,kind,amount,method,reference) values(?,?,?,?,?,?,?,?,?)",id,r.tenant(a),branch,partner,doc,kind,amount,method,reference);return id;
  }
  public Object createPayment(Authentication a,JsonNode x){r.require(a,"FINANCE");UUID branch=uuid(x,"branchId");r.branch(a,branch);r.one("branch",r.tenant(a),branch);UUID partner=optional(x,"partnerId"),doc=optional(x,"documentId");if(partner!=null)r.one("partner",r.tenant(a),partner);if(doc!=null){var d=r.one("document",r.tenant(a),doc);r.warehouse(a,(UUID)d.get("warehouse_id"));if(!branch.equals(d.get("branch_id"))||!Objects.equals(partner,d.get("partner_id")))throw problem("PAYMENT_PARTNER","Khoản tiền phải khớp chi nhánh/đối tác của chứng từ");}
    var kind=text(x,"kind","RECEIPT");if(!Set.of("RECEIPT","PAYMENT","COMMISSION","SUPPLIER_REFUND").contains(kind))throw problem("INVALID_PAYMENT","Hoàn khách cần lập phiếu hoàn và duyệt");
    if(kind.equals("COMMISSION")&&(partner==null||!RetailPartners.hasRole(r,r.one("partner",r.tenant(a),partner),"COLLABORATOR")))throw problem("COMMISSION_PARTNER","Hoa hồng cần cộng tác viên");
    var amount=num(x,"amount",BigDecimal.ZERO);java.time.LocalDate commissionPeriod=null;
    if(kind.equals("COMMISSION")){commissionPeriod=RetailCommissions.period(x);if(amount.compareTo(commissions.remaining(a,branch,partner,commissionPeriod))>0)throw problem("COMMISSION_EXCEEDED","Chi vượt hoa hồng còn phải trả trong kỳ. Tổng tháng chỉ ghi phần bổ sung.");}
    var id=payment(a,branch,partner,doc,kind,amount,required(x,"method",40),text(x,"reference",""));
    if(commissionPeriod!=null)r.db.update("update retail.payment set commission_period=? where tenant_id=? and id=?",commissionPeriod,r.tenant(a),id);
    if(x.path("allocations").isArray())for(var item:x.path("allocations"))allocate(a,id,uuid(item,"openItemId"),num(item,"amount",BigDecimal.ZERO));return Map.of("id",id);
  }
  public Object allocate(Authentication a,UUID payment,UUID openItem,BigDecimal amount){
    var t=r.tenant(a);var p=r.one("payment",t,payment);var o=r.one("open_item",t,openItem);r.branch(a,(UUID)p.get("branch_id"));r.branch(a,(UUID)o.get("branch_id"));
    if(!Objects.equals(p.get("partner_id"),o.get("partner_id"))||!("AR".equals(o.get("kind"))?"RECEIPT":"PAYMENT").equals(p.get("kind")))throw problem("ALLOCATION_PARTNER","Đối tác hoặc chiều tiền không khớp công nợ");
    if(amount.signum()<=0||amount.scale()>0||amount.compareTo(decimal(p.get("amount")).subtract(decimal(p.get("allocated"))))>0||amount.compareTo(decimal(o.get("amount")).subtract(decimal(o.get("allocated"))).subtract(decimal(o.get("credit"))))>0)throw problem("ALLOCATION_EXCEEDED","Phân bổ vượt tiền chưa phân bổ hoặc công nợ còn mở");
    r.db.update("insert into retail.allocation(tenant_id,payment_id,open_item_id,amount) values(?,?,?,?)",t,payment,openItem,amount);
    r.db.update("update retail.payment set allocated=allocated+? where tenant_id=? and id=?",amount,t,payment);
    r.db.update("update retail.open_item set allocated=allocated+? where tenant_id=? and id=?",amount,t,openItem);return Map.of("ok",true);
  }
  public void settlePost(Authentication a,Map<String,Object> doc,UUID openItem,String kind){
    BigDecimal paid=decimal(doc.get("paid"));if(paid.signum()>0){var p=payment(a,(UUID)doc.get("branch_id"),(UUID)doc.get("partner_id"),(UUID)doc.get("id"),kind,paid,String.valueOf(doc.get("method")),"Thu/chi cùng phiếu "+doc.get("number"));var o=r.one("open_item",r.tenant(a),openItem);var amount=paid.min(decimal(o.get("amount")));if(amount.signum()>0)allocate(a,p,openItem,amount);}
    // Order-linked deposits are allocated only when the debt actually exists; surplus remains an advance.
    UUID source=(UUID)doc.get("source_id");if(source!=null){var payments=r.db.queryForList("select * from retail.payment where tenant_id=? and document_id=? and kind=? and amount>allocated order by created_at,id",r.tenant(a),source,kind);for(var p:payments){var o=r.one("open_item",r.tenant(a),openItem);var remaining=decimal(o.get("amount")).subtract(decimal(o.get("credit"))).subtract(decimal(o.get("allocated")));var amount=remaining.min(decimal(p.get("amount")).subtract(decimal(p.get("allocated"))));if(amount.signum()>0)allocate(a,(UUID)p.get("id"),openItem,amount);}}
  }
  public BigDecimal creditReturn(Authentication a,Map<String,Object> doc,BigDecimal amount){
    UUID source=(UUID)doc.get("source_id");var rows=r.db.queryForList("select * from retail.open_item where tenant_id=? and document_id=?",r.tenant(a),source);if(rows.isEmpty())throw problem("SOURCE_DEBT_MISSING","Chứng từ nguồn chưa có sổ công nợ");var o=rows.getFirst();var credit=amount.min(decimal(o.get("amount")).subtract(decimal(o.get("allocated"))).subtract(decimal(o.get("credit"))));r.db.update("update retail.open_item set credit=credit+? where tenant_id=? and id=?",credit,r.tenant(a),o.get("id"));return amount.subtract(credit);
  }
  public String debtFilter(Authentication a,String alias){return "("+(r.granular(a,"receivables","VIEW")?alias+".kind='AR'":"false")+" or "+(r.granular(a,"payables","VIEW")?alias+".kind='AP'":"false")+")";}
  public String paymentFilter(Authentication a,String alias){return "("+(r.granular(a,"receipt","VIEW")?alias+".kind in ('RECEIPT','SUPPLIER_REFUND')":"false")+" or "+(r.granular(a,"payment","VIEW")?alias+".kind in ('PAYMENT','COMMISSION','REFUND')":"false")+")";}
  public Object overview(Authentication a){r.require(a,"FINANCE");var t=r.tenant(a);return Map.of("openItems",r.db.queryForList("select o.*,d.number,p.name partner,o.amount-o.credit-o.allocated remaining from retail.open_item o join retail.document d on d.tenant_id=o.tenant_id and d.id=o.document_id left join retail.partner p on p.tenant_id=o.tenant_id and p.id=o.partner_id where o.tenant_id=? and "+r.branchScope(a,"o")+" and "+debtFilter(a,"o")+" order by d.created_at desc limit 1000",t),"payments",r.db.queryForList("select p.*,c.name partner,d.number from retail.payment p left join retail.partner c on c.tenant_id=p.tenant_id and c.id=p.partner_id left join retail.document d on d.tenant_id=p.tenant_id and d.id=p.document_id where p.tenant_id=? and "+r.branchScope(a,"p")+" and "+paymentFilter(a,"p")+" order by p.created_at desc limit 1000",t));}
}
