package vn.aicrm.retail;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import static vn.aicrm.retail.RetailAccess.*;

/** Location and warehouse master data; writes run inside RetailCommands' tenant lock. */
@Service
public class RetailLocations {
  private final RetailAccess r; private final RetailDefaults defaults;
  public RetailLocations(RetailAccess r,RetailDefaults defaults){this.r=r;this.defaults=defaults;}
  public Object list(Authentication a,String kind){
    r.gate(a,kind.equals("branches")?"locations":"warehouses","VIEW");
    return rows(a,kind);
  }
  private List<Map<String,Object>> rows(Authentication a,String kind){
    var t=r.tenant(a);
    if(kind.equals("branches"))return r.db.queryForList("select b.*,(select count(*) from retail.warehouse w where w.tenant_id=b.tenant_id and w.branch_id=b.id) as warehouse_count from retail.branch b where b.tenant_id=? and (? or exists(select 1 from retail.access_scope s where s.tenant_id=b.tenant_id and s.account_id=? and s.branch_id=b.id)) order by b.is_default desc,b.code",t,r.all(a),r.actor(a));
    return r.warehouses(a);
  }
  public Object onboarding(Authentication a){
    r.gate(a,"setup","VIEW");defaults.ensure(r.tenant(a));
    var company=r.db.queryForList("select * from retail.company_profile where tenant_id=?",r.tenant(a));
    return Map.of("company",company.isEmpty()?Map.of():company.getFirst(),"branches",rows(a,"branches"),"warehouses",rows(a,"warehouses"));
  }
  private void textLimit(JsonNode x,String k,int max){if(text(x,k,"").length()>max)throw problem("INVALID_FIELD","Trường "+k+" tối đa "+max+" ký tự");}
  public Object save(Authentication a,String kind,UUID id,JsonNode x){
    var t=r.tenant(a);boolean branch=kind.equals("branches");if(!branch&&!kind.equals("warehouses"))throw problem("INVALID_CATALOG","Danh mục không hợp lệ");
    var old=id==null?null:r.one(branch?"branch":"warehouse",t,id);
    if(branch){if(id==null&&!r.all(a))throw problem("SCOPE_DENIED","Thêm địa điểm cần phạm vi toàn công ty");if(id!=null)r.branch(a,id);}else if(id!=null)r.warehouse(a,id);
    UUID branchId=branch?null:uuid(x,"branchId");
    if(!branch){r.branch(a,branchId);var parent=r.one("branch",t,branchId);if(!Boolean.TRUE.equals(parent.get("active")))throw problem("INACTIVE_BRANCH","Địa điểm đã ngừng hoạt động");
      if(old!=null&&!branchId.equals(old.get("branch_id")))throw problem("WAREHOUSE_BRANCH_FIXED","Kho giữ nguyên địa điểm; tạo kho mới và chuyển hàng nếu cần thay đổi");}
    var code=required(x,"code",60);var name=required(x,"name",160);
    for(var k:List.of("phone","email","address","note"))textLimit(x,k,k.equals("phone")?60:k.equals("email")?160:2000);
    var manager=optional(x,"managerId");if(manager!=null&&r.db.queryForObject("select count(*) from identity.account where tenant_id=? and id=? and enabled",Integer.class,t,manager)==0)throw problem("INVALID_MANAGER","Người quản lý không thuộc công ty hoặc đã bị khóa");
    String table=branch?"branch":"warehouse";
    if(r.db.queryForObject("select count(*) from retail."+table+" where tenant_id=? and code=? and (?::uuid is null or id<>?::uuid)",Integer.class,t,code,id,id)>0)throw problem("DUPLICATE_CODE","Mã đã được sử dụng trong công ty");
    if(old!=null&&!Boolean.TRUE.equals(old.get("active")))throw problem("INACTIVE_RECORD","Không thể sửa hồ sơ đã ngừng hoạt động");
    boolean isDefault=x.path("isDefault").asBoolean(false);
    if(old!=null&&Boolean.TRUE.equals(old.get("is_default"))&&!isDefault)throw problem("DEFAULT_REQUIRED","Hãy chọn một hồ sơ khác làm mặc định trước");
    if(branch&&isDefault&&!r.all(a))throw problem("SCOPE_DENIED","Đổi địa điểm mặc định cần phạm vi toàn công ty");
    if(!branch&&isDefault&&!r.all(a)&&r.db.queryForObject("select count(*) from retail.warehouse w where w.tenant_id=? and w.branch_id=? and w.active and not exists(select 1 from retail.access_scope s where s.tenant_id=w.tenant_id and s.warehouse_id=w.id and s.account_id=?)",Integer.class,t,branchId,r.actor(a))>0)throw problem("SCOPE_DENIED","Cần phạm vi tất cả kho tại địa điểm để đổi kho mặc định");
    if(r.db.queryForObject("select count(*) from retail."+table+" where tenant_id=? and active and is_default"+(branch?"":" and branch_id=?"),Integer.class,branch?new Object[]{t}:new Object[]{t,branchId})==0)isDefault=true;
    if(isDefault)r.db.update("update retail."+table+" set is_default=false where tenant_id=?"+(branch?"":" and branch_id=?"),branch?new Object[]{t}:new Object[]{t,branchId});
    var target=id==null?UUID.randomUUID():id;
    if(id==null){if(branch)r.db.update("insert into retail.branch(id,tenant_id,code,name) values(?,?,?,?)",target,t,code,name);else r.db.update("insert into retail.warehouse(id,tenant_id,branch_id,code,name) values(?,?,?,?,?)",target,t,branchId,code,name);}
    r.db.update("update retail."+table+" set code=?,name=?,phone=?,email=?,address=?,manager_id=?,note=?,is_default=? where tenant_id=? and id=?",code,name,text(x,"phone",""),text(x,"email",""),text(x,"address",""),manager,text(x,"note",""),isDefault,t,target);
    if(!branch){var type=text(x,"kind","SALEABLE");if(!Set.of("SALEABLE","QUARANTINE").contains(type))throw problem("INVALID_KIND","Loại kho không hợp lệ");r.db.update("update retail.warehouse set kind=? where tenant_id=? and id=?",type,t,target);}
    if(branch&&id==null&&x.path("createWarehouse").asBoolean(true)){r.gate(a,"warehouses","CREATE");UUID wh=UUID.randomUUID();r.db.update("insert into retail.warehouse(id,tenant_id,branch_id,code,name,address,is_default) values(?,?,?,?,?,?,true)",wh,t,target,"WH-"+wh.toString().substring(0,8),"Kho "+name.substring(0,Math.min(name.length(),156)),text(x,"address",""));}
    return Map.of("id",target);
  }
  public Object progress(Authentication a,JsonNode x){
    if(!r.all(a))throw problem("SCOPE_DENIED","Thiết lập doanh nghiệp cần phạm vi toàn công ty");
    var t=r.tenant(a);int step=x.path("step").asInt();if(step<2||step>5)throw problem("INVALID_STEP","Bước thiết lập không hợp lệ");
    var prior=r.db.queryForList("select * from retail.company_profile where tenant_id=?",t);int current=prior.isEmpty()?1:((Number)prior.getFirst().get("onboarding_step")).intValue();if(step>current+1)throw problem("INVALID_STEP","Cần hoàn tất lần lượt các bước thiết lập");
    if(step==2){var name=required(x,"companyName",160);for(var k:List.of("phone","email","address","taxCode"))textLimit(x,k,k.equals("email")?160:k.equals("address")?2000:60);
      var branchId=uuid(x,"branchId");save(a,"branches",branchId,x);
      r.db.update("insert into retail.company_profile(tenant_id,name,phone,email,address,tax_code,onboarding_step) values(?,?,?,?,?,?,2) on conflict(tenant_id) do update set name=excluded.name,phone=excluded.phone,email=excluded.email,address=excluded.address,tax_code=excluded.tax_code,onboarding_step=greatest(retail.company_profile.onboarding_step,2)",t,name,text(x,"phone",""),text(x,"email",""),text(x,"address",""),text(x,"taxCode",""));
    }else {if(step==3)save(a,"warehouses",uuid(x,"warehouseId"),x);r.db.update("update retail.company_profile set onboarding_step=greatest(onboarding_step,?) where tenant_id=?",step,t);}
    return Map.of("ok",true);
  }
  public void checkDeactivate(Authentication a,String kind,UUID id){
    if(kind.equals("warehouses"))r.warehouse(a,id);else r.branch(a,id);
    var t=r.tenant(a);boolean warehouse=kind.equals("warehouses");var row=r.one(warehouse?"warehouse":"branch",t,id);
    if(!Boolean.TRUE.equals(row.get("active")))return;
    if(Boolean.TRUE.equals(row.get("is_default")))throw problem("DEFAULT_REQUIRED","Chọn địa điểm/kho khác làm mặc định trước khi ngừng hoạt động");
    if(warehouse){
      if(r.db.queryForObject("select count(*) from retail.balance where tenant_id=? and warehouse_id=? and (qty<>0 or value<>0)",Integer.class,t,id)>0)throw problem("WAREHOUSE_HAS_STOCK","Kho còn hàng hoặc giá trị tồn; cần xử lý trước khi ngừng");
      if(r.db.queryForObject("select count(*) from retail.document where tenant_id=? and (warehouse_id=? or destination_id=?) and status not in ('POSTED','CANCELLED','CLOSED','REJECTED','COMPLETE')",Integer.class,t,id,id)>0)throw problem("WAREHOUSE_HAS_DOCUMENTS","Kho còn chứng từ chưa hoàn tất hoặc hàng đang chuyển");
      if(r.db.queryForObject("select count(*) from retail.serial where tenant_id=? and warehouse_id=? and location in ('AVAILABLE','QUARANTINE','TRANSIT')",Integer.class,t,id)>0)throw problem("WAREHOUSE_HAS_SERIALS","Kho còn serial chưa xử lý");
      if(r.db.queryForObject("select count(*) from retail.delivery dl join retail.document d on d.tenant_id=dl.tenant_id and d.id=dl.issue_id where d.tenant_id=? and d.warehouse_id=? and dl.status in ('PREPARING','SHIPPING','PARTIAL')",Integer.class,t,id)>0)throw problem("WAREHOUSE_HAS_DELIVERIES","Kho còn đợt giao hàng chưa hoàn tất");
      if(r.db.queryForObject("select count(*) from retail.warranty where tenant_id=? and warehouse_id=? and status in ('RECEIVED','PROCESSING','READY')",Integer.class,t,id)>0)throw problem("WAREHOUSE_HAS_WARRANTIES","Kho còn hàng bảo hành chưa bàn giao");
    }
  }
}
