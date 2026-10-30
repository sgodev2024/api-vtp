package vn.aicrm.viettelpost.order;

import java.util.List;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import vn.aicrm.shared.BusinessModules;
import vn.coreplatform.kernel.ModuleContributor;
import vn.coreplatform.kernel.ModuleDescriptor;
import vn.coreplatform.kernel.NavigationItemDescriptor;
import vn.coreplatform.kernel.NavigationWorkspaceDescriptor;
import vn.coreplatform.kernel.ResourceRegistry;

@Component
public class OrderModule implements ModuleContributor, ApplicationRunner {
    private final ResourceRegistry resourceRegistry;

    public OrderModule(ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
    }

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        BusinessModules.registerResources("aicrm-order", resourceRegistry);
    }

    @Override
    public ModuleDescriptor descriptor() {
        return new ModuleDescriptor(
            "aicrm-order", 
            "Đơn hàng", 
            "1.0.0", 
            List.of("aicrm-retail"), 
            List.of("aicrm-order-capability"), // Sửa lại capability cho đúng tên
            "Quản lý Đơn hàng", 
            ">=1.1.0 <2.0.0"
        );
    }

    @Override
    public List<NavigationWorkspaceDescriptor> navigationWorkspaces() {
        return List.of(
                new NavigationWorkspaceDescriptor(
                        "order-management",    // key (không trùng với module nào khác)
                        "Quản lý Đơn hàng",    // label
                        "navigation.order",    // labelKey (không được để rỗng)
                        "shopping-cart",       // icon (dùng icon có sẵn trên hệ thống)
                        "BUSINESS",            // category phải là BUSINESS
                        21,                    // sortOrder (nằm dưới Vận chuyển)
                        ""                     // requiredAuthority
                ));
    }

    @Override
    public List<NavigationItemDescriptor> navigationItems() {
        return List.of(
                new NavigationItemDescriptor(
                        "module.aicrm-order.management", // key BẮT BUỘC bắt đầu bằng "module.aicrm-order."
                        "order-management",              // workspaceKey (trỏ lên trên)
                        "",                              // parentKey
                        "Danh sách đơn hàng",            // label
                        "navigation.order.list",         // labelKey
                        "clipboard-list",                // icon
                        "order",                         // viewKey (Khớp với Frontend)
                        "/orders",                       // route (Đường dẫn URL)
                        1,                               // sortOrder
                        "",                              // requiredAuthority
                        "",                              // permissionResource
                        "",                              // permissionAction
                        List.of("đơn hàng", "order", "danh sách") // keywords
                ));
    }
}