package vn.aicrm.sales;
import java.util.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.web.bind.annotation.*;import org.springframework.security.core.Authentication;import vn.aicrm.shared.*;import static vn.aicrm.shared.RetailAccess.*;
@RestController @RequestMapping("/api/v1/retail")
public class SalesController {
 private final RetailAccess r;private final CatalogPort catalog;private final InventoryPort inventory;private final SalesOperationsPort operations;private final RetailCommands commands;
 public SalesController(RetailAccess r,CatalogPort catalog,InventoryPort inventory,SalesOperationsPort operations,RetailCommands commands){this.r=r;this.catalog=catalog;this.inventory=inventory;this.operations=operations;this.commands=commands;}
  @PostMapping("/price") Object price(Authentication a,@RequestBody JsonNode x){r.modules.require(BusinessModules.SALES);r.require(a,"SELL");var w=r.warehouse(a,uuid(x,"warehouseId"));return catalog.price(a,uuid(x,"skuId"),optional(x,"partnerId"),(UUID)w.get("branch_id"),text(x,"channel","COUNTER"),positive(x,"qty"));}
  @GetMapping("/deliveries") Object deliveries(Authentication a){return operations.deliveries(a);}
  @PostMapping("/deliveries") Object createDelivery(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"DELIVERY_CREATE",x,()->operations.createDelivery(a,x));}
  @PostMapping("/deliveries/{id}/{action}") Object delivery(Authentication a,@PathVariable UUID id,@PathVariable String action,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"DELIVERY_"+action+"_"+id,x,()->operations.delivery(a,id,action,x));}
  @GetMapping("/original-sale") Object original(Authentication a,@RequestParam String number){return operations.lookupOriginal(a,number);}
  @GetMapping("/warranties") Object warranties(Authentication a){return operations.warrantyList(a);}
  @PostMapping("/warranties") Object warranty(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"WARRANTY_CREATE",x,()->operations.warranty(a,x));}
  @PostMapping("/warranties/{id}/status") Object warrantyStatus(Authentication a,@PathVariable UUID id,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"WARRANTY_STATUS_"+id,x,()->operations.warrantyStatus(a,id,x));}
}
