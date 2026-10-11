package vn.aicrm.shared;
import java.util.*;import java.math.*;import java.time.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.security.core.Authentication;import static vn.aicrm.shared.RetailAccess.*;
/** Stable contract; implementations belong to independently owned business modules. */
public interface CommissionsPort {
public static LocalDate period(JsonNode x){var period=date(x,"period");if(period.getDayOfMonth()!=1)throw problem("COMMISSION_PERIOD","Kỳ hoa hồng phải là ngày đầu tháng");return period;}
Object create(Authentication a,JsonNode x);
BigDecimal remaining(Authentication a,UUID branch,UUID partner,LocalDate period);
Object list(Authentication a);
}
