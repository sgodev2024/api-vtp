package vn.coreplatform.kernel;
import org.springframework.security.core.Authentication;
/** Module-specific discovery checks; business API authorization stays in the module. */
public interface NavigationCapabilityProvider {
  boolean supports(String resource);
  boolean canRender(Authentication auth,String resource,String action);
}
