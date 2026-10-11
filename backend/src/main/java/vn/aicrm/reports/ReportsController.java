package vn.aicrm.reports;
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
public class ReportsController {
 private final ReportsPort reportService;
 public ReportsController(ReportsPort reportService){this.reportService=reportService;}
  @GetMapping("/reports") Object report(Authentication a,@RequestParam String from,@RequestParam String to){return reportService.report(a,from,to);}
}
