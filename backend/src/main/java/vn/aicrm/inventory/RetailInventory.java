package vn.aicrm.inventory;
import vn.aicrm.shared.*;

import java.util.*;
import java.math.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import static vn.aicrm.shared.RetailAccess.*;

/** The only writer of balances; immutable movements and valuation are recorded together. */
@Service
public class RetailInventory implements InventoryPort {
  private final RetailAccess r;
  public RetailInventory(RetailAccess r){this.r=r;}
  public Map<String,Object> balance(UUID t,UUID wh,UUID sku){
    r.db.update("insert into retail.balance(tenant_id,warehouse_id,sku_id) values(?,?,?) on conflict do nothing",t,wh,sku);
    return r.db.queryForList("select * from retail.balance where tenant_id=? and warehouse_id=? and sku_id=? for update",t,wh,sku).getFirst();
  }
  public void unlocked(UUID t,UUID wh,UUID sku,UUID current){
    if(r.db.queryForObject("select count(*) from retail.document d join retail.line l on l.tenant_id=d.tenant_id and l.document_id=d.id where d.tenant_id=? and d.warehouse_id=? and d.type='COUNT' and d.status in ('COUNTING','PENDING') and l.sku_id=? and d.id<>?",Integer.class,t,wh,sku,current)>0)throw problem("COUNT_LOCKED","SKU tại kho đang kiểm kê; chưa thể nhập hoặc xuất");
  }
  public BigDecimal available(UUID t,UUID wh,UUID sku){var rows=r.db.queryForList("select qty-quarantine available from retail.balance where tenant_id=? and warehouse_id=? and sku_id=?",t,wh,sku);return rows.isEmpty()?BigDecimal.ZERO:decimal(rows.getFirst().get("available"));}
  public BigDecimal average(UUID t,UUID wh,UUID sku){var rows=r.db.queryForList("select case when qty>0 then value/qty else 0 end cost from retail.balance where tenant_id=? and warehouse_id=? and sku_id=?",t,wh,sku);return rows.isEmpty()?BigDecimal.ZERO:decimal(rows.getFirst().get("cost"));}
  public void inbound(Authentication a,Map<String,Object> doc,Map<String,Object> line,UUID wh,BigDecimal qty,BigDecimal value,boolean quarantine,String kind,List<Map<String,Object>> transferSegments){r.modules.require(BusinessModules.STOCK);
    var t=r.tenant(a);var sku=(UUID)line.get("sku_id");var id=(UUID)doc.get("id");unlocked(t,wh,sku,id);var b=balance(t,wh,sku);
    var snap=r.node(line.get("sku_snapshot"));var track=snap.path("tracking").asText();var segments=new ArrayList<Map<String,Object>>();
    if(track.equals("LOT")){
      if(transferSegments!=null){for(var seg:transferSegments)lotIn(t,wh,sku,seg.get("lot").toString(),seg.get("expiry").toString(),decimal(seg.get("qty")),quarantine);segments.addAll(transferSegments);}
      else{String lot=String.valueOf(line.get("lot"));if(lot.isBlank()||line.get("expiry")==null)throw problem("LOT_REQUIRED","Hàng theo lô cần mã lô và hạn sử dụng");lotIn(t,wh,sku,lot,line.get("expiry").toString(),qty,quarantine);segments.add(Map.of("lot",lot,"expiry",line.get("expiry").toString(),"qty",qty));}
    }
    if(track.equals("SERIAL")){
      var serials=serials(line,qty);
      for(var sn:serials){var existing=r.db.queryForList("select * from retail.serial where tenant_id=? and serial=?",t,sn);
        boolean returning=kind.equals("SALE_RETURN"),transit=kind.equals("TRANSFER_RECEIVE");
        if(existing.isEmpty()){
          if(returning||transit)throw problem("SERIAL_NOT_FOUND","Serial không có giao dịch nguồn");
          r.db.update("insert into retail.serial(tenant_id,serial,sku_id,warehouse_id,location,source_line_id) values(?,?,?,?,?,?)",t,sn,sku,wh,quarantine?"QUARANTINE":"AVAILABLE",line.get("id"));
        }else{
          var old=existing.getFirst();var expected=returning?"SOLD":transit?"TRANSIT":kind.equals("COUNT_IN")?"MISSING":"SUPPLIER";
          if(!sku.equals(old.get("sku_id"))||!expected.equals(old.get("location"))||((returning||transit)&&!Objects.equals(line.get("source_line_id"),old.get("source_line_id"))))throw problem("SERIAL_CONFLICT","Serial sai giao dịch nguồn hoặc đang ở vị trí khác");
          r.db.update("update retail.serial set warehouse_id=?,location=?,source_line_id=? where tenant_id=? and serial=?",wh,quarantine?"QUARANTINE":"AVAILABLE",line.get("id"),t,sn);
        }
      }
    }
    BigDecimal q=decimal(b.get("qty")).add(qty),v=decimal(b.get("value")).add(value),iso=decimal(b.get("quarantine")).add(quarantine?qty:BigDecimal.ZERO);
    r.db.update("update retail.balance set qty=?,value=?,quarantine=? where tenant_id=? and warehouse_id=? and sku_id=?",q,v,iso,t,wh,sku);
    movement(t,wh,sku,id,(UUID)line.get("id"),kind,qty,value,quarantine?qty:BigDecimal.ZERO,segments);
  }
  private void lotIn(UUID t,UUID wh,UUID sku,String lot,String expiry,BigDecimal qty,boolean quarantine){
    var old=r.db.queryForList("select expiry from retail.lot where tenant_id=? and warehouse_id=? and sku_id=? and code=?",t,wh,sku,lot);
    if(!old.isEmpty()&&!old.getFirst().get("expiry").toString().equals(expiry))throw problem("LOT_EXPIRY_CONFLICT","Mã lô đã có hạn sử dụng khác");
    r.db.update("insert into retail.lot(tenant_id,warehouse_id,sku_id,code,expiry,qty,quarantine) values(?,?,?,?,?::date,?,?) on conflict(tenant_id,warehouse_id,sku_id,code) do update set qty=retail.lot.qty+excluded.qty,quarantine=retail.lot.quarantine+excluded.quarantine",t,wh,sku,lot,expiry,qty,quarantine?qty:BigDecimal.ZERO);
  }
  public BigDecimal outbound(Authentication a,Map<String,Object> doc,Map<String,Object> line,UUID wh,BigDecimal qty,String kind){r.modules.require(BusinessModules.STOCK);
    var t=r.tenant(a);var sku=(UUID)line.get("sku_id");var id=(UUID)doc.get("id");unlocked(t,wh,sku,id);var b=balance(t,wh,sku);
    BigDecimal oldQty=decimal(b.get("qty"));if(oldQty.subtract(decimal(b.get("quarantine"))).compareTo(qty)<0)throw problem("INSUFFICIENT_STOCK","Tồn khả dụng không đủ; phần thiếu phải đặt trước");
    var snap=r.node(line.get("sku_snapshot"));var track=snap.path("tracking").asText();var segments=new ArrayList<Map<String,Object>>();
    if(track.equals("LOT")){
      String lot=String.valueOf(line.get("lot"));
      boolean allowExpired=Set.of("COUNT_OUT","PURCHASE_RETURN","OTHER_ISSUE").contains(kind);
      var lots=r.db.queryForList("select * from retail.lot where tenant_id=? and warehouse_id=? and sku_id=? and (?='' or code=?) and (? or expiry>=?) and qty>quarantine order by expiry,code for update",t,wh,sku,lot,lot,allowExpired,today());
      BigDecimal remain=qty;
      for(var l:lots){var take=decimal(l.get("qty")).subtract(decimal(l.get("quarantine"))).min(remain);if(take.signum()==0)continue;
        r.db.update("update retail.lot set qty=qty-? where tenant_id=? and warehouse_id=? and sku_id=? and code=?",take,t,wh,sku,l.get("code"));
        segments.add(Map.of("lot",l.get("code"),"expiry",l.get("expiry").toString(),"qty",take));remain=remain.subtract(take);if(remain.signum()==0)break;
      }
      if(remain.signum()>0)throw problem("EXPIRED_OR_UNAVAILABLE_LOT","Lô thiếu hàng khả dụng hoặc đã hết hạn; không được xuất");
    }
    if(track.equals("SERIAL"))for(var sn:serials(line,qty)){
      var rows=r.db.queryForList("select * from retail.serial where tenant_id=? and serial=?",t,sn);
      if(rows.isEmpty()||!sku.equals(rows.getFirst().get("sku_id"))||!wh.equals(rows.getFirst().get("warehouse_id"))||!"AVAILABLE".equals(rows.getFirst().get("location")))throw problem("INVALID_SERIAL","Serial không khả dụng tại kho này");
      String loc=kind.equals("TRANSFER_DISPATCH")?"TRANSIT":kind.equals("PURCHASE_RETURN")?"SUPPLIER":kind.equals("COUNT_OUT")?"MISSING":"SOLD";
      r.db.update("update retail.serial set warehouse_id=null,location=?,source_line_id=? where tenant_id=? and serial=?",loc,line.get("id"),t,sn);
    }
    BigDecimal value=oldQty.compareTo(qty)==0?decimal(b.get("value")):decimal(b.get("value")).multiply(qty).divide(oldQty,8,RoundingMode.HALF_UP);
    r.db.update("update retail.balance set qty=qty-?,value=value-? where tenant_id=? and warehouse_id=? and sku_id=?",qty,value,t,wh,sku);
    r.db.update("update retail.line set cost=?,segments=?::jsonb where tenant_id=? and id=?",value,r.json.valueToTree(segments).toString(),t,line.get("id"));
    movement(t,wh,sku,id,(UUID)line.get("id"),kind,qty.negate(),value.negate(),BigDecimal.ZERO,segments);return value;
  }
  public List<String> serials(Map<String,Object> line,BigDecimal qty){var n=r.node(line.get("serials"));var result=new ArrayList<String>();n.forEach(s->{String sn=s.asText().trim();if(sn.isBlank()||sn.length()>100||result.contains(sn))throw problem("INVALID_SERIAL","Serial trùng hoặc không hợp lệ");result.add(sn);});if(qty.stripTrailingZeros().scale()>0||qty.compareTo(BigDecimal.valueOf(result.size()))!=0)throw problem("SERIAL_QUANTITY","Số serial phải bằng số lượng cơ sở nguyên");return result;}
  private void movement(UUID t,UUID wh,UUID sku,UUID doc,UUID line,String kind,BigDecimal qty,BigDecimal value,BigDecimal quarantine,Object details){r.db.update("insert into retail.movement(tenant_id,warehouse_id,sku_id,document_id,line_id,kind,qty,value,quarantine,details) values(?,?,?,?,?,?,?,?,?,?::jsonb)",t,wh,sku,doc,line,kind,qty,value,quarantine,r.json.valueToTree(details).toString());}
  public Object stock(Authentication a){r.modules.require(BusinessModules.STOCK);r.require(a,"READ");if(!r.can(a,"STOCK")&&!r.granular(a,"pos","VIEW")&&!r.granular(a,"orders","VIEW")&&!r.granular(a,"purchase-receipt","VIEW")&&!r.granular(a,"overview-stock","VIEW"))return List.of();var rows=r.db.queryForList("select b.*,s.code,s.name,s.variant,s.unit,s.tracking,w.name warehouse,s.active from retail.balance b join retail.sku s on s.tenant_id=b.tenant_id and s.id=b.sku_id join retail.warehouse w on w.tenant_id=b.tenant_id and w.id=b.warehouse_id where b.tenant_id=? and "+r.scope(a,"b")+" order by w.code,s.code",r.tenant(a));for(var x:rows){x.put("available",decimal(x.get("qty")).subtract(decimal(x.get("quarantine"))));if(!r.can(a,"COST")){x.remove("value");x.remove("details");}}return rows;}
  public Object movements(Authentication a){r.gate(a,"stock","VIEW");var rows=r.db.queryForList("select m.*,d.number,s.code,s.name,w.name warehouse from retail.movement m join retail.document d on d.tenant_id=m.tenant_id and d.id=m.document_id join retail.sku s on s.tenant_id=m.tenant_id and s.id=m.sku_id join retail.warehouse w on w.tenant_id=m.tenant_id and w.id=m.warehouse_id where m.tenant_id=? and "+r.scope(a,"m")+" order by m.created_at desc limit 1000",r.tenant(a));for(var x:rows){x.put("details",r.node(x.get("details")));if(!r.can(a,"COST"))x.remove("value");}return rows;}
}
