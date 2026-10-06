package vn.aicrm.retail;

import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import vn.coreplatform.kernel.*;

@Component
@Order(20)
public class RetailModule implements ModuleContributor, ApplicationRunner {
  private final ResourceRegistry resources;
  public RetailModule(ResourceRegistry resources) { this.resources = resources; }
  public ModuleDescriptor descriptor() {
    return new ModuleDescriptor("aicrm-retail", "Bán hàng, Kho và Mua hàng", "1.0.0",
      List.of("kernel", "permission", "audit-store", "event-outbox"),
      List.of("retail-operations"), "Vận hành cửa hàng và chuỗi chi nhánh", ">=1.1.0 <2.0.0");
  }
  public List<NavigationWorkspaceDescriptor> navigationWorkspaces() {
    return List.of(
      new NavigationWorkspaceDescriptor("retail-overview", "Tổng quan", "navigation.retail.overview-group", "home", "BUSINESS", 11, ""),
      new NavigationWorkspaceDescriptor("retail-reports", "Báo cáo", "navigation.retail.reports-group", "activity", "BUSINESS", 21, ""),
      new NavigationWorkspaceDescriptor("retail", "Bán hàng", "navigation.retail", "shopping-bag", "BUSINESS", 15, ""),
      new NavigationWorkspaceDescriptor("retail-purchase", "Mua hàng", "navigation.retail.purchase-group", "download", "BUSINESS", 16, ""),
      new NavigationWorkspaceDescriptor("retail-stock", "Kho", "navigation.retail.stock-group", "layers", "BUSINESS", 17, ""),
      new NavigationWorkspaceDescriptor("retail-products", "Sản phẩm", "navigation.retail.products-group", "shopping-bag", "BUSINESS", 18, ""),
      new NavigationWorkspaceDescriptor("retail-partner-master", "Khách hàng / Đối tác", "navigation.retail.partner-master", "users", "BUSINESS", 18, ""),
      new NavigationWorkspaceDescriptor("retail-crm", "CRM", "navigation.retail.crm-group", "users", "BUSINESS", 18, ""),
      new NavigationWorkspaceDescriptor("retail-accounting", "Kế toán", "navigation.retail.accounting", "database", "BUSINESS", 19, ""),
      new NavigationWorkspaceDescriptor("retail-general", "Vận hành chung", "navigation.retail.general", "activity", "BUSINESS", 20, ""));
  }
  public List<NavigationItemDescriptor> navigationItems() {
    String[][] screens={{"overview","Tổng quan","home","READ","retail-overview"},
      {"pos","Bán tại quầy","shopping-bag","SELL","retail"},
      {"orders","Đơn bán hàng","clipboard-check","READ","retail"},
      {"sales-customers","Khách hàng","users","SELL","retail"},
      {"returns","Đổi trả & bảo hành","refresh","READ","retail-general"},
      {"purchase","Mua hàng","download","PURCHASE","retail-purchase"},
      {"suppliers","Nhà cung cấp","users","PURCHASE","retail-purchase"},
      {"stock","Kho & tồn","layers","STOCK","retail-stock"},
      {"transfer","Chuyển kho","refresh","STOCK","retail-stock"},
      {"products","Danh sách sản phẩm","shopping-bag","READ","retail-products"},
      {"product-groups","Nhóm sản phẩm","layers","SETUP","retail-products"},
      {"product-brands","Thương hiệu","shopping-bag","SETUP","retail-products"},
      {"product-units","Đơn vị tính","layers","SETUP","retail-products"},
      {"uom-conversions","Quy đổi đơn vị tính","refresh","SETUP","retail-products"},
      {"product-attributes","Thuộc tính","settings","SETUP","retail-products"},
      {"warehouse-config","Cấu hình kho","settings","SETUP","retail-stock"},
      {"master-customers","Khách hàng","users","READ","retail-partner-master"},
      {"master-partners","Đối tác","users","READ","retail-partner-master"},
      {"customers","Khách hàng","users","CRM","retail-crm"},
      {"cashflow","Thu chi","database","FINANCE","retail-accounting"},
      {"debt","Công nợ","clipboard-check","FINANCE","retail-accounting"},
      {"reports","Báo cáo","activity","REPORT","retail-reports"},
      {"approvals","Cần duyệt","shield","APPROVE","retail-general"},
      {"setup","Thiết lập ban đầu","settings","SETUP","retail-general"}};
    var items=new ArrayList<NavigationItemDescriptor>(); int order=10;
    for(var s:screens) items.add(new NavigationItemDescriptor("customer.aicrm."+s[0],s[4],"",s[1],"navigation.retail."+s[0],s[2],
      "retail-"+s[0],"/business/retail/"+s[0],order++,"","aicrm-retail",s[3],List.of(s[1],"cửa hàng")));
    return items;
  }
  public void run(ApplicationArguments args) {
    resources.register(new ResourceDescriptor("aicrm-retail","Vận hành cửa hàng","aicrm-retail","DOMAIN","1.0.0",
      List.of("READ","SELL","PURCHASE","STOCK","CRM","FINANCE","REPORT","APPROVE","SETUP","COST","EXPORT","ALL_BRANCHES"),"ALWAYS","CONFIDENTIAL"));
  }
}


