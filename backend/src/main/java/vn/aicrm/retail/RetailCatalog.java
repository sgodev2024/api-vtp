package vn.aicrm.retail;

import java.util.*;
import java.math.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import static vn.aicrm.retail.RetailAccess.*;

@Service
public class RetailCatalog {
  private final RetailAccess r; private final RetailPartners partners;
  public RetailCatalog(RetailAccess r,RetailPartners partners){this.r=r;this.partners=partners;}
  public Object catalog(Authentication a){
    r.require(a,"READ");var t=r.tenant(a);var warehouses=r.warehouses(a);
    var branches=r.db.queryForList("select * from retail.branch where tenant_id=? order by code",t).stream().filter(b->r.all(a)||warehouses.stream().anyMatch(w->w.get("branch_id").equals(b.get("id")))).toList();
    return Map.of("branches",branches,"warehouses",warehouses,
      "skus",r.db.queryForList("select * from retail.sku where tenant_id=? order by code",t).stream().map(r::clean).toList(),
      "partners",partners.catalog(a),
      "priceRules",r.db.queryForList("select p.* from retail.price_rule p where p.tenant_id=? and (p.branch_id is null or "+r.branchScope(a,"p")+") order by p.priority desc",t),
      "promotions",r.db.queryForList("select * from retail.promotion where tenant_id=? order by priority desc",t),
      "discountLimit",r.db.queryForObject("select coalesce((select discount_limit from retail.setting where tenant_id=?),0)",BigDecimal.class,t),
      "permissions",List.of("READ","SELL","PURCHASE","STOCK","CRM","FINANCE","REPORT","APPROVE","SETUP","COST","EXPORT").stream().filter(x->r.can(a,x)).toList(),"accountId",r.actor(a));
  }
  public Object create(Authentication a,String kind,JsonNode x){
    r.require(a,"SETUP");var t=r.tenant(a);var id=UUID.randomUUID();
    switch(kind){
      case "branches" -> { if(!r.all(a))throw problem("SCOPE_DENIED","Cần quyền quản lý toàn chi nhánh");r.db.update("insert into retail.branch(id,tenant_id,code,name) values(?,?,?,?)",id,t,required(x,"code",60),required(x,"name",160)); }
      case "warehouses" -> {var branch=uuid(x,"branchId");r.branch(a,branch);r.one("branch",t,branch);r.db.update("insert into retail.warehouse(id,tenant_id,branch_id,code,name) values(?,?,?,?,?)",id,t,branch,required(x,"code",60),required(x,"name",160));}
      case "skus" -> {
        var tracking=text(x,"tracking","NONE");if(!Set.of("NONE","LOT","SERIAL").contains(tracking))throw problem("INVALID_TRACKING","Kiểu theo dõi không hợp lệ");
        var price=num(x,"price",BigDecimal.ZERO); var vat=num(x,"vat",BigDecimal.ZERO); var weight=num(x,"weight",BigDecimal.ZERO);if(price.signum()<0||price.scale()>4||vat.signum()<0||vat.compareTo(new BigDecimal("100"))>0||weight.signum()<0)throw problem("INVALID_PRICE","Giá, thuế hoặc trọng lượng không hợp lệ");
        int returns=x.path("returnDays").asInt(0),warranty=x.path("warrantyDays").asInt(0);if(returns<0||warranty<0)throw problem("INVALID_POLICY","Thời hạn phải không âm");
        var units=x.path("units").isObject()?x.path("units"):r.json.createObjectNode();units.fields().forEachRemaining(e->{if(e.getKey().isBlank()||e.getKey().length()>40||quantity(decimal(e.getValue().asText())).signum()<=0)throw problem("INVALID_UOM","Tỷ lệ đơn vị phải dương");});
        r.db.update("insert into retail.sku(id,tenant_id,code,name,barcode,variant,unit,units,tracking,price,vat,return_days,warranty_days,allow_opened,weight) values(?,?,?,?,?,?,?,?::jsonb,?,?,?,?,?,?,?)",id,t,required(x,"code",60),required(x,"name",160),text(x,"barcode","").isBlank()?null:required(x,"barcode",100),text(x,"variant",""),required(x,"unit",40),units.toString(),tracking,price,vat,returns,warranty,x.path("allowOpened").asBoolean(false),weight);
      }
      case "partners" -> {return partners.save(a,null,x);}
      case "price-rules" -> {var sku=uuid(x,"skuId");r.one("sku",t,sku);var branch=optional(x,"branchId");if(branch!=null)r.branch(a,branch);var partner=optional(x,"partnerId");if(partner!=null)r.one("partner",t,partner);var price=num(x,"price",BigDecimal.ZERO);if(price.signum()<0)throw problem("INVALID_PRICE","Giá không âm");r.db.update("insert into retail.price_rule(id,tenant_id,sku_id,partner_id,customer_group,branch_id,channel,min_qty,price,priority,valid_from,valid_to) values(?,?,?,?,?,?,?,?,?,?,?,?)",id,t,sku,partner,text(x,"customerGroup",""),branch,text(x,"channel",""),positive(x,"minQty"),price,x.path("priority").asInt(0),date(x,"validFrom"),date(x,"validTo"));}
      case "promotions" -> {var sku=uuid(x,"skuId");r.one("sku",t,sku);var percent=num(x,"percent",BigDecimal.ZERO);if(percent.signum()<=0||percent.compareTo(new BigDecimal("100"))>0)throw problem("INVALID_DISCOUNT","Giảm giá trong khoảng (0,100]");r.db.update("insert into retail.promotion(id,tenant_id,sku_id,name,percent,priority,stackable,valid_from,valid_to) values(?,?,?,?,?,?,?,?,?)",id,t,sku,required(x,"name",160),percent,x.path("priority").asInt(0),x.path("stackable").asBoolean(false),date(x,"validFrom"),date(x,"validTo"));}
      case "scopes" -> {var account=uuid(x,"accountId");var wh=r.warehouse(a,uuid(x,"warehouseId"));if(r.db.queryForObject("select count(*) from identity.account where id=? and tenant_id=?",Integer.class,account,t)==0)throw problem("INVALID_ACCOUNT","Tài khoản không thuộc tổ chức");r.db.update("insert into retail.access_scope(tenant_id,account_id,branch_id,warehouse_id) values(?,?,?,?) on conflict do nothing",t,account,wh.get("branch_id"),wh.get("id"));return Map.of("ok",true);}
      case "settings" -> {var limit=num(x,"discountLimit",BigDecimal.ZERO);if(limit.signum()<0||limit.compareTo(new BigDecimal("100"))>0)throw problem("INVALID_DISCOUNT","Ngưỡng giảm giá từ 0–100%");r.db.update("insert into retail.setting(tenant_id,discount_limit) values(?,?) on conflict(tenant_id) do update set discount_limit=excluded.discount_limit",t,limit);return Map.of("ok",true);}
      default -> throw problem("INVALID_CATALOG","Danh mục không được hỗ trợ");
    }
    return Map.of("id",id);
  }
  public Object deactivate(Authentication a,String kind,UUID id){if(kind.equals("partners"))return partners.deactivate(a,id);r.require(a,"SETUP");String table=switch(kind){case "skus"->"sku";case "partners"->"partner";case "warehouses"->"warehouse";case "branches"->"branch";default->throw problem("INVALID_CATALOG","Không hỗ trợ ngừng danh mục này");};var row=r.one(table,r.tenant(a),id);if(table.equals("warehouse"))r.warehouse(a,id);if(table.equals("branch"))r.branch(a,id);r.db.update("update retail."+table+" set active=false where tenant_id=? and id=?",r.tenant(a),id);return Map.of("id",row.get("id"));}
  public Map<String,Object> price(Authentication a,UUID sku,UUID partner,UUID branch,String channel,BigDecimal qty){
    var t=r.tenant(a);var item=r.one("sku",t,sku);if(!Boolean.TRUE.equals(item.get("active")))throw problem("INACTIVE_SKU","Hàng hóa đã ngừng sử dụng");
    String group=partner==null?"":String.valueOf(r.one("partner",t,partner).get("customer_group"));
    var rules=r.db.queryForList("""
      select * from retail.price_rule where tenant_id=? and sku_id=? and ? between valid_from and valid_to and min_qty<=?
      and (partner_id is null or partner_id=?) and (customer_group='' or customer_group=?)
      and (branch_id is null or branch_id=?) and (channel='' or channel=?)
      order by case when partner_id is not null then 1 when customer_group<>'' then 2 when branch_id is not null or channel<>'' then 3 else 4 end,
      min_qty desc,priority desc,id limit 1
      """,t,sku,today(),qty,partner,group,branch,channel);
    BigDecimal price=rules.isEmpty()?decimal(item.get("price")):decimal(rules.getFirst().get("price"));var used=new ArrayList<Object>();
    var promos=r.db.queryForList("select * from retail.promotion where tenant_id=? and sku_id=? and ? between valid_from and valid_to order by priority desc,id",t,sku,today());
    var exclusive=promos.stream().filter(p->!Boolean.TRUE.equals(p.get("stackable"))).findFirst();
    var selected=exclusive.isPresent()?List.of(exclusive.get()):promos;
    BigDecimal effective=price;for(var p:selected){effective=effective.multiply(BigDecimal.ONE.subtract(decimal(p.get("percent")).divide(new BigDecimal("100"))));used.add(p.get("id"));}
    var response=new LinkedHashMap<String,Object>();response.put("price",price);response.put("priceAvailable",!rules.isEmpty()||price.signum()>0);response.put("effective",effective.setScale(4,RoundingMode.HALF_UP));response.put("vat",item.get("vat"));response.put("ruleId",rules.isEmpty()?"DEFAULT":rules.getFirst().get("id"));response.put("promotions",used);return response;
  }
}
