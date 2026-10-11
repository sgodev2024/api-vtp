package vn.aicrm.crm;
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
public class CrmController {
 private final RetailCommands commands;
 private final CustomerCare careService;
 public CrmController(RetailCommands commands,CustomerCare careService){this.commands=commands;this.careService=careService;}
  @GetMapping("/customers/{id}/history") Object history(Authentication a,@PathVariable UUID id){return careService.care(a,id);}
  @PostMapping("/care") Object care(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"CARE_CREATE",x,()->careService.createCare(a,x));}
  @PostMapping("/care/{id}/complete") Object completeCare(Authentication a,@PathVariable UUID id,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"CARE_COMPLETE_"+id,x,()->careService.completeCare(a,id));}
}
