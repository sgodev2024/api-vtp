package vn.aicrm.crm;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import vn.aicrm.shared.RetailCommands;

@RestController
@RequestMapping("/api/v1/retail/crm")
public class CrmPipelineController {
 private final CrmPipeline service;private final RetailCommands commands;
 public CrmPipelineController(CrmPipeline service,RetailCommands commands){this.service=service;this.commands=commands;}
 @GetMapping("/options") Object options(Authentication a){return service.options(a);}
 @GetMapping("/leads") Object leads(Authentication a,@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="") String state,@RequestParam(defaultValue="0") int page){return service.list(a,false,q,state,page);}
 @GetMapping("/opportunities") Object opportunities(Authentication a,@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="") String state,@RequestParam(defaultValue="0") int page){return service.list(a,true,q,state,page);}
 @PostMapping("/leads") Object lead(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"CRM_LEAD_CREATE",x,()->service.save(a,false,null,x));}
 @PostMapping("/leads/{id}") Object leadUpdate(Authentication a,@PathVariable UUID id,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"CRM_LEAD_UPDATE_"+id,x,()->service.save(a,false,id,x));}
 @PostMapping("/leads/{id}/convert") Object convert(Authentication a,@PathVariable UUID id,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"CRM_LEAD_CONVERT_"+id,x,()->service.convert(a,id,x));}
 @PostMapping("/opportunities") Object opportunity(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"CRM_OPPORTUNITY_CREATE",x,()->service.save(a,true,null,x));}
 @PostMapping("/opportunities/{id}") Object opportunityUpdate(Authentication a,@PathVariable UUID id,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"CRM_OPPORTUNITY_UPDATE_"+id,x,()->service.save(a,true,id,x));}
}
