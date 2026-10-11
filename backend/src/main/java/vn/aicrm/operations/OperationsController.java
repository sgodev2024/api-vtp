package vn.aicrm.operations;
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
public class OperationsController {
 private final RetailAccess r;
 private final RetailCommands commands;
 private final ImportPort imports;
 private final LocationsPort locations;
 public OperationsController(RetailAccess r,RetailCommands commands,ImportPort imports,LocationsPort locations){this.r=r;this.commands=commands;this.imports=imports;this.locations=locations;}
  @GetMapping("/locations/{kind}") Object locations(Authentication a,@PathVariable String kind){if(!Set.of("branches","warehouses").contains(kind))throw problem("INVALID_CATALOG","Danh mục không hợp lệ");return locations.list(a,kind);}
  @PostMapping("/locations/{kind}") Object addLocation(Authentication a,@PathVariable String kind,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"LOCATION_CREATE_"+kind,x,()->locations.save(a,kind,null,x));}
  @PostMapping("/locations/{kind}/{id}") Object saveLocation(Authentication a,@PathVariable String kind,@PathVariable UUID id,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"LOCATION_UPDATE_"+kind,x,()->locations.save(a,kind,id,x));}
  @GetMapping("/onboarding") Object onboarding(Authentication a){return locations.onboarding(a);}
  @GetMapping("/location-owners") Object locationOwners(Authentication a){if(!r.granular(a,"locations","VIEW")&&!r.granular(a,"warehouses","VIEW")&&!r.granular(a,"setup","VIEW"))throw problem("PERMISSION_DENIED","Không có quyền quản lý địa điểm/kho",HttpStatus.FORBIDDEN);return r.db.queryForList("select id,display_name,email from identity.account where tenant_id=? and enabled order by display_name",r.tenant(a));}
  @GetMapping("/locations/{kind}/export") Object exportLocations(Authentication a,@PathVariable String kind){if(!Set.of("branches","warehouses").contains(kind))throw problem("INVALID_CATALOG","Danh mục không hợp lệ");r.gate(a,kind.equals("branches")?"locations":"warehouses","EXPORT");return commands.export(a,kind,()->locations.list(a,kind));}
  @PostMapping("/onboarding") Object progress(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"ONBOARDING",x,()->locations.progress(a,x));}
  @PostMapping("/imports") Object preview(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"IMPORT_PREVIEW",x,()->imports.preview(a,x));}
  @PostMapping("/imports/file") Object file(Authentication a,@RequestParam String kind,@RequestParam UUID warehouseId,@RequestParam MultipartFile file,@RequestHeader("Idempotency-Key") String key)throws java.io.IOException{r.require(a,"SETUP");var x=r.json.createObjectNode().put("kind",kind).put("warehouseId",warehouseId.toString());x.set("rows",imports.parse(file.getBytes(),file.getOriginalFilename()==null?"":file.getOriginalFilename()));return commands.execute(a,key,"IMPORT_PREVIEW",x,()->imports.preview(a,x));}
  @PostMapping("/imports/{id}/commit") Object commit(Authentication a,@PathVariable UUID id,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"IMPORT_COMMIT_"+id,x,()->imports.commit(a,id));}
  @GetMapping("/imports") Object batches(Authentication a){r.require(a,"SETUP");return r.db.queryForList("select * from retail.import_batch b where tenant_id=? and "+r.scope(a,"b")+" order by created_at desc limit 100",r.tenant(a)).stream().filter(b->{try{new RetailPermissions(r).importPermission(a,b.get("kind").toString(),r.node(b.get("rows")));return true;}catch(vn.coreplatform.shared.ApiExceptionHandler.ApiProblem denied){return false;}}).map(r::clean).toList();}
  @GetMapping("/scopes") Object scopes(Authentication a){r.gate(a,"scope","VIEW");return r.db.queryForList("select s.*,a.email,w.name warehouse from retail.access_scope s join identity.account a on a.id=s.account_id join retail.warehouse w on w.tenant_id=s.tenant_id and w.id=s.warehouse_id where s.tenant_id=? and "+r.scope(a,"s"),r.tenant(a));}
}
