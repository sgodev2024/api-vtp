package vn.aicrm.shared;
import java.util.*;import java.math.*;import org.springframework.security.core.Authentication;
public interface InventoryPort {
 Map<String,Object> balance(UUID t,UUID wh,UUID sku);
 void unlocked(UUID t,UUID wh,UUID sku,UUID current);
 BigDecimal available(UUID t,UUID wh,UUID sku);
 BigDecimal average(UUID t,UUID wh,UUID sku);
 void inbound(Authentication a,Map<String,Object> doc,Map<String,Object> line,UUID wh,BigDecimal qty,BigDecimal value,boolean quarantine,String kind,List<Map<String,Object>> transferSegments);
 BigDecimal outbound(Authentication a,Map<String,Object> doc,Map<String,Object> line,UUID wh,BigDecimal qty,String kind);
 List<String> serials(Map<String,Object> line,BigDecimal qty);
 Object stock(Authentication a);
 Object movements(Authentication a);
}
