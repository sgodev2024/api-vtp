package vn.aicrm.products;
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
public class ProductsController {
 private final RetailAccess r;
 private final RetailCommands commands;
 private final ProductConfigPort retailWarehouseConfigService;
 public ProductsController(RetailAccess r,RetailCommands commands,ProductConfigPort retailWarehouseConfigService){this.r=r;this.commands=commands;this.retailWarehouseConfigService=retailWarehouseConfigService;}
  @GetMapping("/uom-conversions") Object uomConversions(Authentication a){r.gate(a,"uom-conversions","VIEW");return Map.of("units",r.db.queryForList("select * from retail.uom where tenant_id=? order by name",r.tenant(a)),"conversions",r.db.queryForList("select c.*,s.code,s.name as product,u.name as unit,s.unit as base_unit from retail.uom_conversion c join retail.sku s on s.tenant_id=c.tenant_id and s.id=c.product_id join retail.uom u on u.tenant_id=c.tenant_id and u.id=c.uom_id where c.tenant_id=? order by s.code,u.name",r.tenant(a)));}
  @PostMapping("/uom-conversions") Object saveUomConversion(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"UOM_CONVERSION",x,()->{r.require(a,"SETUP");var t=r.tenant(a);var product=uuid(x,"productId");var sku=r.one("sku",t,product);var unit=r.db.queryForList("select * from retail.uom where tenant_id=? and id=?",t,uuid(x,"uomId"));if(unit.isEmpty())throw problem("INVALID_UOM","Đơn vị không tồn tại");var name=unit.getFirst().get("name").toString();var factor=positive(x,"factor");if(factor.scale()>4)throw problem("INVALID_UOM","Tỷ lệ tối đa 4 chữ số thập phân");if(name.equals(sku.get("unit")))throw problem("BASE_UOM","Đơn vị cơ sở luôn có tỷ lệ 1");r.db.queryForObject("select id from retail.sku where tenant_id=? and id=? for update",UUID.class,t,product);r.db.update("update retail.sku set units=jsonb_set(units,array[?]::text[],to_jsonb(?::numeric),true) where tenant_id=? and id=?",name,factor,t,product);return Map.of("ok",true);});}
  @GetMapping("/warehouse-config") Object warehouseConfig(Authentication a){r.require(a,"READ");return r.db.queryForList("select * from retail.warehouse_config where tenant_id=? order by kind,code",r.tenant(a)).stream().filter(c->r.granular(a,new RetailPermissions(r).configFeature(c.get("kind").toString()),"VIEW")).toList();}
  @PostMapping("/warehouse-config") Object saveWarehouseConfig(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"WAREHOUSE_CONFIG",x,()->retailWarehouseConfigService.doSetupConfig(a,x));}
}
