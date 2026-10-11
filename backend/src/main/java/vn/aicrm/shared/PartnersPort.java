package vn.aicrm.shared;
import java.util.*;import java.math.*;import java.time.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.security.core.Authentication;import static vn.aicrm.shared.RetailAccess.*;
/** Stable contract; implementations belong to independently owned business modules. */
public interface PartnersPort {
public static boolean hasRole(RetailAccess r,Map<String,Object> p,String role){return r.node(p.get("roles")).path(role).asBoolean();}
List<Map<String,Object>> catalog(Authentication a);
Object list(Authentication a,String query,String role,String state,int page,String group);
Object detail(Authentication a,UUID id);
Object save(Authentication a,UUID id,JsonNode x);
Object quickCustomer(Authentication a,JsonNode x);
Object importPartner(Authentication a,JsonNode input,boolean preview);
Object deactivate(Authentication a,UUID id);
Object accounts(Authentication a);
}
