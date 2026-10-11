package vn.aicrm.shared;
import java.util.*;import java.math.*;import java.time.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.security.core.Authentication;import static vn.aicrm.shared.RetailAccess.*;
/** Stable contract; implementations belong to independently owned business modules. */
public interface CatalogPort {
Object catalog(Authentication a);
Object create(Authentication a, String kind, JsonNode x);
Object deactivate(Authentication a, String kind, UUID id);
Map<String, Object> price(Authentication a, UUID sku, UUID partner, UUID branch, String channel,
      BigDecimal qty);
}
