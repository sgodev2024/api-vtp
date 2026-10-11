package vn.aicrm.accounting;
import vn.aicrm.shared.*;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
import static vn.aicrm.shared.RetailAccess.*;

@RestController
@RequestMapping("/api/v1/retail")
public class AccountingController {
 private final RetailAccess r;
 private final RetailCommands commands;
 private final FinancePort finance;
 private final CommissionsPort commissions;
 public AccountingController(RetailAccess r,RetailCommands commands,FinancePort finance,CommissionsPort commissions){this.r=r;this.commands=commands;this.finance=finance;this.commissions=commissions;}
  @GetMapping("/commissions") Object commissions(Authentication a){return commissions.list(a);}
  @PostMapping("/commissions") Object commission(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"COMMISSION_CREATE",x,()->commissions.create(a,x));}
  @GetMapping("/finance") Object finance(Authentication a){return finance.overview(a);}
  @PostMapping("/payments") Object payment(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"PAYMENT_CREATE",x,()->finance.createPayment(a,x));}
  @PostMapping("/allocations") Object allocation(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){r.require(a,"FINANCE");return commands.execute(a,key,"ALLOCATION",x,()->finance.allocate(a,uuid(x,"paymentId"),uuid(x,"openItemId"),num(x,"amount",java.math.BigDecimal.ZERO)));}
}
