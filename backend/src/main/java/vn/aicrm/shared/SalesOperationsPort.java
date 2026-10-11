package vn.aicrm.shared;
import java.util.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.security.core.Authentication;
public interface SalesOperationsPort {Object deliveries(Authentication a);
Object createDelivery(Authentication a,JsonNode x);
Object delivery(Authentication a,UUID id,String action,JsonNode x);
Object warrantyList(Authentication a);
Object warranty(Authentication a,JsonNode x);
Object warrantyStatus(Authentication a,UUID id,JsonNode x);
Object lookupOriginal(Authentication a,String number);}
