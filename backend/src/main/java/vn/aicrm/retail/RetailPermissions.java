package vn.aicrm.retail;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import static vn.aicrm.retail.RetailAccess.*;

/** Granular permissions. Legacy flags are derived capabilities, never authorization fallback. */
@Service
public class RetailPermissions implements vn.coreplatform.kernel.NavigationCapabilityProvider {
  public boolean supports(String resource){return resource.startsWith("aicrm-menu-");}
  public boolean canRender(Authentication a,String resource,String action){return screen(a,resource.substring(11));}
  public record Feature(String code,String module,String name,List<String> actions) {}
  private static Feature f(String code,String module,String name,String actions){return new Feature(code,module,name,List.of(actions.split(",")));}
  public static final List<Feature> FEATURES=List.of(
    f("pos","Bán hàng","Bán tại quầy","VIEW,CREATE,SUBMIT,COLLECT_POS,REQUEST_CANCEL,APPROVE_CANCEL,PRINT,EXPORT"),
    f("orders","Bán hàng","Báo giá / Đơn bán","VIEW,CREATE,SUBMIT,APPROVE,REJECT,REQUEST_CANCEL,APPROVE_CANCEL,PRINT,EXPORT"),
    f("issue","Kho","Xuất bán","VIEW,CREATE,SUBMIT,APPROVE,REJECT,REQUEST_CANCEL,APPROVE_CANCEL,PRINT,EXPORT"),
    f("delivery","Bán hàng","Giao hàng","VIEW,CREATE,UPDATE,SETTLE,EXPORT"),
    f("purchase-order","Mua hàng","Đề nghị / Đơn mua","VIEW,CREATE,SUBMIT,APPROVE,REJECT,REQUEST_CANCEL,APPROVE_CANCEL,PRINT,EXPORT"),
    f("purchase-receipt","Mua hàng","Nhập mua","VIEW,CREATE,SUBMIT,APPROVE,REJECT,CONFIRM_DISCREPANCY,REQUEST_CANCEL,APPROVE_CANCEL,PRINT,EXPORT"),
    f("purchase-return","Mua hàng","Trả mua","VIEW,CREATE,SUBMIT,APPROVE,REJECT,REQUEST_CANCEL,APPROVE_CANCEL,PRINT,EXPORT"),
    f("stock","Kho","Kho & Tồn / Nhập xuất khác","VIEW,CREATE,SUBMIT,APPROVE,REJECT,RECEIVE,DISPATCH,REQUEST_CANCEL,APPROVE_CANCEL,PRINT,EXPORT"),
    f("count","Kho","Kiểm kê","VIEW,CREATE,UPDATE,SUBMIT,APPROVE,REJECT,REQUEST_CANCEL,APPROVE_CANCEL,EXPORT"),
    f("transfer","Kho","Chuyển kho","VIEW,CREATE,SUBMIT,APPROVE,REJECT,DISPATCH,RECEIVE,CONFIRM_DISCREPANCY,REQUEST_CANCEL,APPROVE_CANCEL,PRINT,EXPORT"),
    f("warehouse-config","Kho","Cấu hình kho","VIEW,UPDATE"),
    f("products","Sản phẩm","Danh sách sản phẩm","VIEW,CREATE,UPDATE,DEACTIVATE,VIEW_PRICE,UPDATE_PRICE,IMPORT,EXPORT"),
    f("product-groups","Sản phẩm","Nhóm sản phẩm","VIEW,CREATE,UPDATE,DEACTIVATE"),
    f("product-brands","Sản phẩm","Thương hiệu","VIEW,CREATE,UPDATE,DEACTIVATE"),
    f("product-units","Sản phẩm","Đơn vị tính","VIEW,CREATE,UPDATE,DEACTIVATE"),
    f("uom-conversions","Sản phẩm","Quy đổi đơn vị tính","VIEW,CREATE,UPDATE,DEACTIVATE"),
    f("product-attributes","Sản phẩm","Thuộc tính / Biến thể","VIEW,CREATE,UPDATE,DEACTIVATE"),
    f("customer","Khách hàng / Đối tác","Khách hàng dùng chung","VIEW,CREATE,UPDATE,DEACTIVATE,IMPORT,EXPORT"),
    f("supplier","Mua hàng","Nhà cung cấp dùng chung","VIEW,CREATE,UPDATE,DEACTIVATE,IMPORT,EXPORT"),
    f("partners","Khách hàng / Đối tác","Đối tác / Vai trò","VIEW,CREATE,UPDATE,MANAGE_ROLES,DEACTIVATE,VIEW_BANK,UPDATE_BANK,IMPORT,EXPORT"),
    f("crm","CRM","Chăm sóc khách hàng","VIEW,CREATE,UPDATE,COMPLETE,VIEW_HISTORY,EXPORT"),
    f("receipt","Kế toán","Phiếu thu","VIEW,CREATE,ALLOCATE,PRINT,EXPORT"),
    f("payment","Kế toán","Phiếu chi","VIEW,CREATE,ALLOCATE,PRINT,EXPORT"),
    f("receivables","Kế toán","Phải thu","VIEW,ALLOCATE,EXPORT"),
    f("payables","Kế toán","Phải trả","VIEW,ALLOCATE,EXPORT"),
    f("opening-stock","Vận hành chung","Tồn đầu kỳ","VIEW,CREATE,SUBMIT,APPROVE,REJECT,IMPORT,REQUEST_CANCEL,APPROVE_CANCEL,EXPORT"),
    f("opening-ar","Kế toán","Phải thu đầu kỳ","VIEW,CREATE,SUBMIT,APPROVE,REJECT,IMPORT,REQUEST_CANCEL,APPROVE_CANCEL,EXPORT"),
    f("opening-ap","Kế toán","Phải trả đầu kỳ","VIEW,CREATE,SUBMIT,APPROVE,REJECT,IMPORT,REQUEST_CANCEL,APPROVE_CANCEL,EXPORT"),
    f("commission","Kế toán","Hoa hồng","VIEW,CREATE,EXPORT"),
    f("returns","Vận hành chung","Trả hàng","VIEW,CREATE,SUBMIT,APPROVE,REJECT,RECEIVE,LOOKUP_ORIGINAL,REQUEST_CANCEL,APPROVE_CANCEL,EXPORT"),
    f("warranty","Vận hành chung","Bảo hành","VIEW,CREATE,UPDATE,LOOKUP_ORIGINAL,EXPORT"),
    f("refund","Kế toán","Hoàn tiền","VIEW,CREATE,SUBMIT,APPROVE,REJECT,REQUEST_CANCEL,APPROVE_CANCEL,EXPORT"),
    f("setup","Vận hành chung","Thiết lập / Chi nhánh kho","VIEW,CREATE,UPDATE,DEACTIVATE"),
    f("scope","Quản trị hệ thống","Phạm vi chi nhánh / kho","VIEW,MANAGE,ALL_BRANCHES"),
    f("sensitive","Kế toán","Giá vốn / Lợi nhuận","VIEW,VIEW_COST,VIEW_PROFIT"),
    f("overview-sales","Tổng quan","Bán hàng","VIEW"),f("overview-purchase","Tổng quan","Mua hàng","VIEW"),f("overview-stock","Tổng quan","Kho","VIEW"),f("overview-products","Tổng quan","Sản phẩm","VIEW"),f("overview-crm","Tổng quan","CRM","VIEW"),f("overview-finance","Tổng quan","Kế toán","VIEW"),
    f("report-sales","Báo cáo","Bán hàng","VIEW,EXPORT"),f("report-purchase","Báo cáo","Mua hàng","VIEW,EXPORT"),f("report-stock","Báo cáo","Kho","VIEW,EXPORT"),f("report-products","Báo cáo","Sản phẩm","VIEW,EXPORT"),f("report-crm","Báo cáo","CRM","VIEW,EXPORT"),f("report-finance","Báo cáo","Kế toán","VIEW,EXPORT"));
  private final RetailAccess r;
  public RetailPermissions(RetailAccess r){this.r=r;}
  public static List<String> screenFeatures(String screen){return switch(screen){case "overview"->FEATURES.stream().map(Feature::code).filter(c->c.startsWith("overview-")).toList();case "reports"->FEATURES.stream().map(Feature::code).filter(c->c.startsWith("report-")).toList();case "approvals"->List.of("orders","purchase-order","purchase-receipt","purchase-return","issue","stock","count","transfer","returns","refund","opening-stock","opening-ar","opening-ap");case "purchase"->List.of("purchase-order","purchase-receipt","purchase-return");case "stock"->List.of("stock","count","opening-stock","issue");case "cashflow"->List.of("receipt","payment","commission","refund");case "debt"->List.of("receivables","payables");case "returns"->List.of("returns","warranty");case "customers"->List.of("crm");case "suppliers"->List.of("supplier");case "sales-customers","master-customers"->List.of("customer");case "master-partners"->List.of("partners");default->List.of(screen);};}
  public boolean screen(Authentication a,String screen){return screenFeatures(screen).stream().anyMatch(f->can(a,f,screen.equals("approvals")?"APPROVE":"VIEW"));}
  public static String resource(String feature){return "aicrm-"+feature;}
  public static String feature(String type){return switch(type){case "POS"->"pos";case "QUOTE","SALE_ORDER"->"orders";case "ISSUE"->"issue";case "PURCHASE_REQUEST","PURCHASE_ORDER"->"purchase-order";case "PURCHASE_RECEIPT"->"purchase-receipt";case "PURCHASE_RETURN"->"purchase-return";case "TRANSFER"->"transfer";case "COUNT"->"count";case "OTHER_RECEIPT","OTHER_ISSUE"->"stock";case "OPENING_STOCK"->"opening-stock";case "OPENING_AR"->"opening-ar";case "OPENING_AP"->"opening-ap";case "SALE_RETURN"->"returns";case "REFUND"->"refund";default->throw problem("INVALID_DOCUMENT_TYPE","Loại chứng từ không hợp lệ");};}
  public boolean can(Authentication a,String feature,String action){return r.permissions.decide(a,resource(feature),"VIEW",null).allowed() && (action.equals("VIEW") || r.permissions.decide(a,resource(feature),action,null).allowed());}
  public void require(Authentication a,String feature,String action){if(!can(a,feature,action))throw problem("PERMISSION_DENIED","Không có quyền "+action+" tại "+feature,HttpStatus.FORBIDDEN);}
  public boolean any(Authentication a,String action){return FEATURES.stream().anyMatch(f->f.actions.contains(action)&&can(a,f.code,action));}
  public Map<String,List<String>> capabilities(Authentication a){var result=new LinkedHashMap<String,List<String>>();for(var f:FEATURES)result.put(f.code,f.actions.stream().filter(action->can(a,f.code,action)).toList());return result;}
  public static final List<String> TYPES=List.of("QUOTE","SALE_ORDER","POS","ISSUE","PURCHASE_REQUEST","PURCHASE_ORDER","PURCHASE_RECEIPT","PURCHASE_RETURN","SALE_RETURN","TRANSFER","COUNT","OTHER_RECEIPT","OTHER_ISSUE","REFUND","OPENING_STOCK","OPENING_AR","OPENING_AP");
  public String documentFilter(Authentication a,String alias,String action){var allowed=TYPES.stream().filter(t->can(a,feature(t),action)).map(t->"'"+t+"'").toList();return allowed.isEmpty()?"false":alias+".type in ("+String.join(",",allowed)+")";}
  public String partnerFeature(String role){return switch(role){case "CUSTOMER"->"customer";case "SUPPLIER"->"supplier";default->"partners";};}
  public String configFeature(String kind){return switch(kind){case "INVENTORY"->"warehouse-config";case "UNIT"->"product-units";case "GROUP"->"product-groups";case "BRAND"->"product-brands";case "VARIANT","ATTRIBUTE"->"product-attributes";default->throw problem("INVALID_CONFIG","Cấu hình không hợp lệ");};}
  public String catalogFeature(String kind){return switch(kind){case "skus"->"products";case "price-rules","promotions"->"products";case "branches","warehouses","settings"->"setup";case "scopes"->"scope";case "partners"->"partners";default->throw problem("INVALID_CATALOG","Danh mục không hợp lệ");};}
  public void partnerWrite(Authentication a,UUID id,JsonNode x){
    var old=id==null?null:r.one("partner",r.tenant(a),id);var roles=x.path("roles");if(!roles.isObject())roles=r.json.createObjectNode().put(text(x,"kind","CUSTOMER"),true);
    var prior=old==null?r.json.createObjectNode():r.node(old.get("roles"));
    boolean permitted=false;
    for(String role:List.of("CUSTOMER","SUPPLIER","COLLABORATOR"))if(roles.path(role).asBoolean()||prior.path(role).asBoolean()){
      String f=partnerFeature(role);if(can(a,f,id==null?"CREATE":"UPDATE"))permitted=true;
      if(id!=null && roles.has(role) && roles.path(role).asBoolean()!=prior.path(role).asBoolean())require(a,"partners","MANAGE_ROLES");
      if(id==null&&roles.path(role).asBoolean())require(a,f,"CREATE");
    }
    if(!permitted)throw problem("PERMISSION_DENIED","Không có quyền sửa hồ sơ đối tác",HttpStatus.FORBIDDEN);
    if(x.has("bankName")||x.has("bankAccount")||x.has("bankHolder"))require(a,"partners","UPDATE_BANK");
    if(old!=null && x.has("active") && !x.path("active").asBoolean())require(a,"partners","DEACTIVATE");
  }
  public void command(Authentication a,String op,JsonNode x){
    if(x.hasNonNull("warehouseId"))r.warehouse(a,uuid(x,"warehouseId"));if(x.hasNonNull("branchId"))r.branch(a,uuid(x,"branchId"));
    if(op.equals("DOCUMENT_CREATE")){String f=feature(required(x,"type",30));require(a,f,"CREATE");if(num(x,"paid",java.math.BigDecimal.ZERO).signum()>0)require(a,f.equals("pos")?"pos":"payment",f.equals("pos")?"COLLECT_POS":"CREATE");return;}
    if(op.startsWith("DOCUMENT_")){int last=op.lastIndexOf('_');var d=r.one("document",r.tenant(a),UUID.fromString(op.substring(last+1)));String action=op.substring(9,last);r.warehouse(a,(UUID)d.get(action.equals("receive")?"destination_id":"warehouse_id"));String f=feature(d.get("type").toString());String p=switch(action){case "approve"->"APPROVE";case "reject"->"REJECT";case "cancel"->"DRAFT".equals(d.get("status"))?"REQUEST_CANCEL":"APPROVE_CANCEL";case "close"->"APPROVE_CANCEL";case "dispatch"->"DISPATCH";case "receive"->"RECEIVE";case "count","record-count"->"UPDATE";default->"SUBMIT";};require(a,f,p);if(action.equals("receive")&&x.path("lines").isArray())for(var l:x.path("lines"))if(l.path("damaged").asBoolean())require(a,"transfer","CONFIRM_DISCREPANCY");return;}
    if(op.equals("CUSTOMER_QUICK_CREATE")){require(a,"customer","CREATE");return;}
    if(op.equals("PARTNER_CREATE")){partnerWrite(a,null,x);return;}
    if(op.startsWith("PARTNER_UPDATE_")){partnerWrite(a,UUID.fromString(op.substring(15)),x);return;}
    if(op.startsWith("CATALOG_")){String kind=op.substring(8),f=catalogFeature(kind);require(a,f,kind.equals("scopes")?"MANAGE":kind.equals("settings")?"UPDATE":"CREATE");if(Set.of("price-rules","promotions").contains(kind)||kind.equals("skus")&&num(x,"price",java.math.BigDecimal.ZERO).signum()>0)require(a,"products","UPDATE_PRICE");return;}
    if(op.startsWith("DEACTIVATE_")){String kind=op.substring(11,op.lastIndexOf('_'));require(a,catalogFeature(kind),"DEACTIVATE");return;}
    if(op.equals("UOM_CONVERSION")){boolean existing=r.db.queryForObject("select count(*) from retail.uom_conversion where tenant_id=? and product_id=? and uom_id=?",Integer.class,r.tenant(a),uuid(x,"productId"),uuid(x,"uomId"))>0;require(a,"uom-conversions",existing?"UPDATE":"CREATE");return;}
    if(op.equals("WAREHOUSE_CONFIG")){String kind=text(x,"kind","");boolean existing=kind.equals("INVENTORY")||r.db.queryForObject("select count(*) from retail.warehouse_config where tenant_id=? and kind=? and code=?",Integer.class,r.tenant(a),kind,text(x,"code",""))>0;require(a,configFeature(kind),existing?"UPDATE":"CREATE");return;}
    if(op.equals("PAYMENT_CREATE")){if(x.path("allocations").isArray())for(var allocation:x.path("allocations")){var o=r.one("open_item",r.tenant(a),uuid(allocation,"openItemId"));r.branch(a,(UUID)o.get("branch_id"));require(a,o.get("kind").equals("AR")?"receivables":"payables","ALLOCATE");}String f=Set.of("RECEIPT","SUPPLIER_REFUND").contains(text(x,"kind","RECEIPT"))?"receipt":"payment";require(a,f,"CREATE");return;}
    if(op.equals("ALLOCATION")){var pay=r.one("payment",r.tenant(a),uuid(x,"paymentId"));r.branch(a,(UUID)pay.get("branch_id"));var o=r.one("open_item",r.tenant(a),uuid(x,"openItemId"));r.branch(a,(UUID)o.get("branch_id"));require(a,o.get("kind").equals("AR")?"receivables":"payables","ALLOCATE");return;}
    if(op.equals("COMMISSION_CREATE")){require(a,"commission","CREATE");return;}
    if(op.equals("DELIVERY_CREATE")){require(a,"delivery","CREATE");return;}
    if(op.startsWith("DELIVERY_")){require(a,"delivery",op.startsWith("DELIVERY_settle")?"SETTLE":"UPDATE");return;}
    if(op.equals("CARE_CREATE")){require(a,"crm","CREATE");return;}
    if(op.startsWith("CARE_COMPLETE_")){require(a,"crm","COMPLETE");return;}
    if(op.equals("WARRANTY_CREATE")){require(a,"warranty","CREATE");return;}
    if(op.startsWith("WARRANTY_STATUS_")){require(a,"warranty","UPDATE");return;}
    if(op.equals("IMPORT_PREVIEW")){importPermission(a,text(x,"kind",""),x.path("rows"));return;}
    if(op.startsWith("IMPORT_COMMIT_")){var b=r.one("import_batch",r.tenant(a),UUID.fromString(op.substring(14)));r.warehouse(a,(UUID)b.get("warehouse_id"));importPermission(a,b.get("kind").toString(),r.node(b.get("rows")));return;}
    throw problem("PERMISSION_OPERATION","Lệnh chưa được cấu hình phân quyền",HttpStatus.FORBIDDEN);
  }
  public void importPermission(Authentication a,String kind,JsonNode rows){switch(kind){case "SKUS"->{require(a,"products","IMPORT");require(a,"products","CREATE");}case "PARTNERS"->{for(var row:rows){require(a,partnerFeature(text(row,"kind","CUSTOMER")),"IMPORT");partnerWrite(a,null,row);}}case "OPENING_STOCK"->{require(a,"opening-stock","IMPORT");require(a,"opening-stock","CREATE");}default->{for(var row:rows){String f=text(row,"kind","").equals("AR")?"opening-ar":"opening-ap";require(a,f,"IMPORT");require(a,f,"CREATE");}}}}
}
