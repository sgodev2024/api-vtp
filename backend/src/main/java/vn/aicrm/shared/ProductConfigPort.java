package vn.aicrm.shared;
import java.util.*;import java.math.*;import java.time.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.security.core.Authentication;import static vn.aicrm.shared.RetailAccess.*;
/** Stable contract; implementations belong to independently owned business modules. */
public interface ProductConfigPort {
Object doSetupConfig(Authentication a, JsonNode x);
}
