package vn.aicrm.shared;
import java.util.*;import java.math.*;import java.time.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.security.core.Authentication;import static vn.aicrm.shared.RetailAccess.*;
/** Stable contract; implementations belong to independently owned business modules. */
public interface FinancePort {
UUID open(Authentication a,Map<String,Object> doc,String kind,BigDecimal amount);
UUID payment(Authentication a,UUID branch,UUID partner,UUID doc,String kind,BigDecimal amount,String method,String reference);
Object createPayment(Authentication a,JsonNode x);
Object allocate(Authentication a,UUID payment,UUID openItem,BigDecimal amount);
void settlePost(Authentication a,Map<String,Object> doc,UUID openItem,String kind);
BigDecimal creditReturn(Authentication a,Map<String,Object> doc,BigDecimal amount);
String debtFilter(Authentication a,String alias);
String paymentFilter(Authentication a,String alias);
Object overview(Authentication a);
}
