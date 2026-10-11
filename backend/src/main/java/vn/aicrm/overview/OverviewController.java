package vn.aicrm.overview;
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
public class OverviewController {
 private final Overview overviewService;
 public OverviewController(Overview overviewService){this.overviewService=overviewService;}
  @GetMapping("/summary") Object summary(Authentication a){return overviewService.summary(a);}
}
