package vn.aicrm.accounting;
import vn.aicrm.shared.*;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import static vn.aicrm.shared.RetailAccess.*;

@Service
public class RetailCommissions implements CommissionsPort {
  private final RetailAccess r;
  public RetailCommissions(RetailAccess r){this.r=r;}
  public static LocalDate period(JsonNode x){var period=date(x,"period");if(period.getDayOfMonth()!=1)throw problem("COMMISSION_PERIOD","Kỳ hoa hồng phải là ngày đầu tháng");return period;}
  public Object create(Authentication a,JsonNode x){
    r.require(a,"FINANCE");var t=r.tenant(a);var branch=uuid(x,"branchId");r.branch(a,branch);var partner=uuid(x,"partnerId");
    if(!PartnersPort.hasRole(r,r.one("partner",t,partner),"COLLABORATOR"))throw problem("COMMISSION_PARTNER","Chọn đối tác cộng tác viên");
    var kind=required(x,"kind",30);if(!Set.of("ORDER","MONTHLY_ADDITION","ADJUSTMENT").contains(kind))throw problem("COMMISSION_KIND","Loại hoa hồng không hợp lệ");
    var amount=num(x,"amount",BigDecimal.ZERO);if(amount.signum()==0||amount.stripTrailingZeros().scale()>0||!kind.equals("ADJUSTMENT")&&amount.signum()<0)throw problem("COMMISSION_AMOUNT","Khoản theo đơn/bổ sung phải dương; điều chỉnh dùng số có dấu, đơn vị VND");
    var document=optional(x,"documentId");if(kind.equals("ORDER")&&document==null)throw problem("COMMISSION_DOCUMENT","Hoa hồng theo đơn cần phiếu bán đã ghi");
    if(document!=null){var d=r.one("document",t,document);r.warehouse(a,(UUID)d.get("warehouse_id"));if(!branch.equals(d.get("branch_id"))||!"POSTED".equals(d.get("status"))||!Set.of("POS","ISSUE","SALE_RETURN").contains(d.get("type"))||kind.equals("ORDER")&&d.get("type").equals("SALE_RETURN"))throw problem("COMMISSION_DOCUMENT","Chọn phiếu bán/điều chỉnh trả đã ghi cùng chi nhánh");}
    var id=UUID.randomUUID();r.db.update("insert into retail.commission(id,tenant_id,branch_id,partner_id,document_id,kind,period,amount,reason,creator_id) values(?,?,?,?,?,?,?,?,?,?)",id,t,branch,partner,document,kind,period(x),amount,required(x,"reason",1000),r.actor(a));return Map.of("id",id);
  }
  public BigDecimal remaining(Authentication a,UUID branch,UUID partner,LocalDate period){
    var t=r.tenant(a);var earned=r.db.queryForObject("select coalesce(sum(amount),0) from retail.commission where tenant_id=? and branch_id=? and partner_id=? and period=?",BigDecimal.class,t,branch,partner,period);
    var paid=r.db.queryForObject("select coalesce(sum(amount),0) from retail.payment where tenant_id=? and branch_id=? and partner_id=? and kind='COMMISSION' and commission_period=?",BigDecimal.class,t,branch,partner,period);return earned.subtract(paid);
  }
  public Object list(Authentication a){r.require(a,"FINANCE");var t=r.tenant(a);
    var entries=r.db.queryForList("select c.*,p.name partner,d.number from retail.commission c join retail.partner p on p.tenant_id=c.tenant_id and p.id=c.partner_id left join retail.document d on d.tenant_id=c.tenant_id and d.id=c.document_id where c.tenant_id=? and "+r.branchScope(a,"c")+" order by c.created_at desc limit 1000",t);
    var balances=r.db.queryForList("select c.branch_id,c.partner_id,c.period,p.name partner,b.name branch,sum(c.amount) earned,coalesce((select sum(pay.amount) from retail.payment pay where pay.tenant_id=c.tenant_id and pay.branch_id=c.branch_id and pay.partner_id=c.partner_id and pay.commission_period=c.period and pay.kind='COMMISSION'),0) paid from retail.commission c join retail.partner p on p.tenant_id=c.tenant_id and p.id=c.partner_id join retail.branch b on b.tenant_id=c.tenant_id and b.id=c.branch_id where c.tenant_id=? and "+r.branchScope(a,"c")+" group by c.tenant_id,c.branch_id,c.partner_id,c.period,p.name,b.name order by c.period desc,p.name",t);
    balances.forEach(row->row.put("remaining",decimal(row.get("earned")).subtract(decimal(row.get("paid")))));return Map.of("entries",entries,"balances",balances);
  }
}
