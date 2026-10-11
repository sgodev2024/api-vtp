package vn.aicrm.partners;
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
public class PartnersController {
 private final RetailCommands commands;
 private final PartnersPort partners;
 public PartnersController(RetailCommands commands,PartnersPort partners){this.commands=commands;this.partners=partners;}
  @GetMapping("/partner-owners") Object partnerOwners(Authentication a){return partners.accounts(a);}
  @GetMapping("/partners") Object partners(Authentication a,@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="") String role,@RequestParam(defaultValue="") String state,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="") String group){return partners.list(a,q,role,state,page,group);}
  @GetMapping("/partners/{id}") Object partner(Authentication a,@PathVariable UUID id){return partners.detail(a,id);}
  @PostMapping("/partners") Object createPartner(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"PARTNER_CREATE",x,()->partners.save(a,null,x));}
  @PostMapping("/customers/quick-create") Object quickCustomer(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"CUSTOMER_QUICK_CREATE",x,()->partners.quickCustomer(a,x));}
  @PostMapping("/partners/{id}") Object updatePartner(Authentication a,@PathVariable UUID id,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"PARTNER_UPDATE_"+id,x,()->partners.save(a,id,x));}
}
