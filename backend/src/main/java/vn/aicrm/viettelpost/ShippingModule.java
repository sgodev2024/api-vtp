package vn.aicrm.viettelpost;

import java.util.List;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import vn.aicrm.shared.BusinessModules;
import vn.coreplatform.kernel.*;

@Component
public class ShippingModule implements ModuleContributor, ApplicationRunner {
    private final ResourceRegistry resources;

    public ShippingModule(ResourceRegistry resources) {
        this.resources = resources;
    }

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        // Vẫn đăng ký resource cho cả 2 nghiệp vụ
        BusinessModules.registerResources("aicrm-shipping", resources);
        BusinessModules.registerResources("aicrm-order", resources);
    }

    @Override
    public ModuleDescriptor descriptor() {
        // Đổi key thành aicrm-viettelpost (hoặc giữ nguyên aicrm-shipping)
        return new ModuleDescriptor(
            "aicrm-viettelpost", 
            "Viettel Post", 
            "1.0.0", 
            List.of("aicrm-retail"), 
            List.of("aicrm-shipping-capability"), 
            "Tích hợp vận chuyển và đơn hàng Viettel Post", 
            ">=1.1.0 <2.0.0"
        );
    }

    @Override
    public List<NavigationWorkspaceDescriptor> navigationWorkspaces() {
        return List.of(
                new NavigationWorkspaceDescriptor(
                        "viettelpost-management",  // Key nhóm Menu
                        "Viettel Post",            // Tên nhóm Menu
                        "navigation.viettelpost",  
                        "truck",                   
                        "BUSINESS",                
                        20,                        
                        ""                         
                ));
    }

    @Override
    public List<NavigationItemDescriptor> navigationItems() {
        return List.of(
                // Menu 1: Cấu hình đối tác
                new NavigationItemDescriptor(
                        "module.aicrm-viettelpost.providers", 
                        "viettelpost-management", 
                        "", "Cấu hình đối tác", "navigation.viettelpost.providers", 
                        "settings", "shipping", "/shipping", 1, "", "", "", 
                        List.of("vận chuyển", "viettel post", "token", "đối tác") 
                ),
                // Menu 2: Quản lý đơn hàng
                new NavigationItemDescriptor(
                        "module.aicrm-viettelpost.orders", 
                        "viettelpost-management", 
                        "", "Quản lý Đơn hàng", "navigation.viettelpost.orders", 
                        "clipboard-list", "order", "/orders", 2, "", "", "", 
                        List.of("đơn hàng", "order", "viettel post") 
                ),
                // Menu 3: Quản lý API Key (THÊM VÀO ĐÂY)
                new NavigationItemDescriptor(
                        "module.aicrm-viettelpost.apikeys", // <-- Key phải bắt đầu bằng module.aicrm-viettelpost
                        "viettelpost-management",           // <-- Dùng chung workspace
                        "", "Quản lý API Key", "navigation.viettelpost.apikeys", 
                        "key", "apikeys", "/api-keys", 3, "", "", "", 
                        List.of("api key", "tích hợp", "bảo mật") 
                )
        );
    }
}