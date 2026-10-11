package vn.aicrm.shared;
import java.util.*;import java.math.*;import java.time.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.security.core.Authentication;import static vn.aicrm.shared.RetailAccess.*;
/** Stable contract; implementations belong to independently owned business modules. */
public interface LocationsPort {
Object list(Authentication a,String kind);
Object onboarding(Authentication a);
Object save(Authentication a,String kind,UUID id,JsonNode x);
Object progress(Authentication a,JsonNode x);
void checkDeactivate(Authentication a,String kind,UUID id);
}
