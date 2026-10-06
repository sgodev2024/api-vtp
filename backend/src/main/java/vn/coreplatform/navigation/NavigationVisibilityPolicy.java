package vn.coreplatform.navigation;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import vn.coreplatform.kernel.NavigationItemDescriptor;
import vn.coreplatform.permission.PermissionService;

/**
 * Chính sách hiển thị page trong navigation manifest hiệu lực.
 *
 * <p>Đây là discovery policy, không thay thế authorization ở endpoint. FE-BA-13
 * được giữ tại một điểm duy nhất: page ASSIGNMENT luôn cần capability chính xác
 * của tài khoản; quyền wildcard của System Administrator không tạo ra nhiệm vụ.
 */
@Component
public class NavigationVisibilityPolicy {
  private final PermissionService permissions;
  private final org.springframework.beans.factory.ObjectProvider<vn.coreplatform.kernel.NavigationCapabilityProvider> retail;

  public NavigationVisibilityPolicy(PermissionService permissions){this(permissions,null);}
  @org.springframework.beans.factory.annotation.Autowired
  public NavigationVisibilityPolicy(PermissionService permissions,org.springframework.beans.factory.ObjectProvider<vn.coreplatform.kernel.NavigationCapabilityProvider> retail) {
    this.permissions = permissions;this.retail=retail;
  }

  boolean canRender(Authentication auth, NavigationItemDescriptor item, boolean administrator) {
    if(retail!=null){var provider=retail.orderedStream().filter(p->p.supports(item.permissionResource())).findFirst();if(provider.isPresent())return provider.get().canRender(auth,item.permissionResource(),item.permissionAction());}
    if (item.group()) return true;
    if (item.assignmentScoped()) {
      return permissions.scopeExplicit(auth, item.permissionResource(), item.permissionAction()).allowed();
    }
    if (item.permissionResource().isBlank()) return true;
    return administrator || permissions.scope(auth, item.permissionResource(), item.permissionAction()).allowed();
  }
}
