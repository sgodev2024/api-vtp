package vn.aicrm.inventory;
import java.util.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.web.bind.annotation.*;import org.springframework.security.core.Authentication;import vn.aicrm.shared.*;import static vn.aicrm.shared.RetailAccess.*;
@RestController @RequestMapping("/api/v1/retail")
public class InventoryController {
 private final RetailAccess r;private final CatalogPort catalog;private final InventoryPort inventory;private final SalesOperationsPort operations;private final RetailCommands commands;
 public InventoryController(RetailAccess r,CatalogPort catalog,InventoryPort inventory,SalesOperationsPort operations,RetailCommands commands){this.r=r;this.catalog=catalog;this.inventory=inventory;this.operations=operations;this.commands=commands;}
  @GetMapping("/stock") Object stock(Authentication a){return inventory.stock(a);}
  @GetMapping("/movements") Object movements(Authentication a){return inventory.movements(a);}
  @GetMapping("/tracking") Object tracking(Authentication a,@RequestParam UUID warehouseId,@RequestParam UUID skuId){r.gate(a,"stock","VIEW");r.warehouse(a,warehouseId);return Map.of("lots",r.db.queryForList("select * from retail.lot where tenant_id=? and warehouse_id=? and sku_id=? order by expiry",r.tenant(a),warehouseId,skuId),"serials",r.db.queryForList("select serial,location from retail.serial where tenant_id=? and warehouse_id=? and sku_id=? order by serial",r.tenant(a),warehouseId,skuId));}
}
