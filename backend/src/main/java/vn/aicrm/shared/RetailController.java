package vn.aicrm.shared;
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
public class RetailController {
 private final RetailAccess r;
 private final RetailCommands commands;
 private final CatalogPort catalog;
 private final RetailDocuments documents;
 private final InventoryPort inventory;
 private final FinancePort finance;
 private final ReportsPort reportService;
 public RetailController(RetailAccess r,RetailCommands commands,CatalogPort catalog,RetailDocuments documents,InventoryPort inventory,FinancePort finance,ReportsPort reportService){this.r=r;this.commands=commands;this.catalog=catalog;this.documents=documents;this.inventory=inventory;this.finance=finance;this.reportService=reportService;}
  @GetMapping("/catalog") Object catalog(Authentication a){r.modules.require(BusinessModules.PRODUCTS);return catalog.catalog(a);}
  @PostMapping("/catalog/{kind}") Object createCatalog(Authentication a,@PathVariable String kind,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"CATALOG_"+kind,x,()->catalog.create(a,kind,x));}
  @PostMapping("/catalog/{kind}/{id}/deactivate") Object deactivate(Authentication a,@PathVariable String kind,@PathVariable UUID id,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"DEACTIVATE_"+kind+"_"+id,x,()->catalog.deactivate(a,kind,id));}
  @GetMapping("/documents") Object documents(Authentication a,@RequestParam(defaultValue="") String type,@RequestParam(defaultValue="") String status,@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="0") int page){if(!type.isBlank())r.modules.require(BusinessModules.owner(RetailPermissions.feature(type)));return documents.list(a,type,status,q,page);}
  @GetMapping("/documents/{id}/print") Object print(Authentication a,@PathVariable UUID id){var d=documents.document(a,id);r.gate(a,RetailPermissions.feature(d.get("type").toString()),"PRINT");return documents.detail(a,id);}
  @GetMapping("/documents/{id}") Object detail(Authentication a,@PathVariable UUID id){return documents.detail(a,id);}
  @PostMapping("/documents") Object create(Authentication a,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"DOCUMENT_CREATE",x,()->documents.create(a,x));}
  @PostMapping("/documents/{id}/{action}") Object transition(Authentication a,@PathVariable UUID id,@PathVariable String action,@RequestBody JsonNode x,@RequestHeader("Idempotency-Key") String key){return commands.execute(a,key,"DOCUMENT_"+action+"_"+id,x,()->documents.transition(a,id,action,x));}
  @GetMapping("/export") ResponseEntity<String> export(Authentication a,@RequestParam String kind,@RequestParam(defaultValue="") String from,@RequestParam(defaultValue="") String to){r.require(a,"EXPORT");exportPermission(a,kind);List<Map<String,Object>> rows=(List<Map<String,Object>>)commands.export(a,kind,()->switch(kind){case "suppliers"->r.db.queryForList("select code,name,phone,email,address,active from retail.partner where tenant_id=? and roles->>'SUPPLIER'='true' order by code",r.tenant(a));case "products"->(List<Map<String,Object>>)((Map<?,?>)catalog.catalog(a)).get("skus");case "stock"->(List<Map<String,Object>>)inventory.stock(a);case "movements"->(List<Map<String,Object>>)inventory.movements(a);case "documents"->allDocuments(a);case "payments"->((List<Map<String,Object>>)((Map<?,?>)finance.overview(a)).get("payments")).stream().filter(row->r.granular(a,Set.of("RECEIPT","SUPPLIER_REFUND").contains(row.get("kind"))?"receipt":"payment","EXPORT")).toList();case "finance"->((List<Map<String,Object>>)((Map<?,?>)finance.overview(a)).get("openItems")).stream().filter(row->r.granular(a,row.get("kind").equals("AR")?"receivables":"payables","EXPORT")).toList();case "report"->((List<Map<String,Object>>)((Map<?,?>)reportService.report(a,from,to)).get("sales")).stream().filter(row->r.granular(a,Set.of("PURCHASE_RECEIPT","PURCHASE_RETURN").contains(row.get("type"))?"report-purchase":"report-sales","EXPORT")).toList();default->throw problem("EXPORT_KIND","Loại xuất không hợp lệ");});var csv=new StringBuilder("\ufeff");if(!rows.isEmpty()){var columns=new ArrayList<>(rows.getFirst().keySet());csv.append(String.join(",",columns)).append("\r\n");for(var row:rows){csv.append(columns.stream().map(c->escape(row.get(c))).collect(java.util.stream.Collectors.joining(","))).append("\r\n");}}return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=aicrm-"+kind+".csv").contentType(new MediaType("text","csv",java.nio.charset.StandardCharsets.UTF_8)).body(csv.toString());}
  private void exportPermission(Authentication a,String kind){String feature=switch(kind){case "suppliers"->"supplier";case "products"->"products";case "stock","movements"->"stock";case "payments"->"receipt";case "finance"->"receivables";case "report"->"report-sales";case "documents"->"";default->throw problem("EXPORT_KIND","Loại xuất không hợp lệ");};if(kind.equals("payments")){if(!r.granular(a,"receipt","EXPORT")&&!r.granular(a,"payment","EXPORT"))r.gate(a,"receipt","EXPORT");}else if(kind.equals("finance")){if(!r.granular(a,"receivables","EXPORT")&&!r.granular(a,"payables","EXPORT"))r.gate(a,"receivables","EXPORT");}else if(kind.equals("report")){if(!r.granular(a,"report-sales","EXPORT")&&!r.granular(a,"report-purchase","EXPORT"))r.gate(a,"report-sales","EXPORT");}else if(!feature.isEmpty())r.gate(a,feature,"EXPORT");}
  private List<Map<String,Object>> allDocuments(Authentication a){var rows=new ArrayList<Map<String,Object>>();int page=0;while(true){var result=(Map<?,?>)documents.list(a,"","","",page++);var items=(List<Map<String,Object>>)result.get("items");rows.addAll(items.stream().filter(d->r.granular(a,RetailPermissions.feature(d.get("type").toString()),"EXPORT")).toList());if(items.size()<50)break;}return rows;}
  private String escape(Object value){String text=value==null?"":value.toString();if(text.matches("^[=+@\\-].*"))text="'"+text;return "\""+text.replace("\"","\"\"")+"\"";}
}
