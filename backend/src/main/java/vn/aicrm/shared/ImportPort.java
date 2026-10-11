package vn.aicrm.shared;
import java.util.*;import java.math.*;import java.time.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.security.core.Authentication;import static vn.aicrm.shared.RetailAccess.*;
/** Stable contract; implementations belong to independently owned business modules. */
public interface ImportPort {
Object preview(Authentication a,JsonNode x);
Object commit(Authentication a,UUID id);
JsonNode parse(byte[] bytes,String filename);
}
