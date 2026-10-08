package vn.aicrm.sales;
import java.util.*;import com.fasterxml.jackson.databind.JsonNode;import org.springframework.web.bind.annotation.*;import org.springframework.security.core.Authentication;import vn.aicrm.shared.*;import static vn.aicrm.shared.RetailAccess.*;
@RestController @RequestMapping("/api/v1/retail/sales/documents")
public class SalesDocuments {
 private static final Set<String> TYPES=Set.of("QUOTE","SALE_ORDER","POS","SALE_RETURN");
 private final RetailAccess access;private final RetailDocuments documents;private final RetailCommands commands;
 public SalesDocuments(RetailAccess access,RetailDocuments documents,RetailCommands commands){this.access=access;this.documents=documents;this.commands=commands;}
 private void family(String type){access.modules.require(BusinessModules.SALES);if(!TYPES.contains(type))throw problem("MODULE_DOCUMENT_TYPE","Loại phiếu không thuộc module");}
 private void owned(Authentication a,UUID id){family(documents.document(a,id).get("type").toString());}
 @GetMapping Object list(Authentication a,@RequestParam(defaultValue="SALE_ORDER") String type,@RequestParam(defaultValue="") String status,@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="0") int page){family(type);return documents.list(a,type,status,q,page);}
 @GetMapping("/{id}") Object detail(Authentication a,@PathVariable UUID id){owned(a,id);return documents.detail(a,id);}
 @PostMapping Object create(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){family(required(x,"type",30));return commands.execute(a,key,"DOCUMENT_CREATE",x,()->documents.create(a,x));}
 @PostMapping("/{id}/{action}") Object transition(Authentication a,@PathVariable UUID id,@PathVariable String action,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){owned(a,id);return commands.execute(a,key,"DOCUMENT_"+action+"_"+id,x,()->documents.transition(a,id,action,x));}
}
