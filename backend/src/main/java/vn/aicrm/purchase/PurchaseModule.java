package vn.aicrm.purchase;
import java.util.*;import org.springframework.stereotype.Component;import vn.coreplatform.kernel.*;import vn.aicrm.shared.*;
@Component
@org.springframework.core.annotation.Order(20)
public class PurchaseModule implements ModuleContributor,org.springframework.boot.ApplicationRunner {
 private final ResourceRegistry resources;
 public PurchaseModule(ResourceRegistry resources){this.resources=resources;}
 public void run(org.springframework.boot.ApplicationArguments args){BusinessModules.registerResources("aicrm-purchase",resources);}
 public ModuleDescriptor descriptor(){return new ModuleDescriptor("aicrm-purchase","Mua hàng","1.0.0",List.of("aicrm-retail","aicrm-stock","aicrm-partners","aicrm-accounting"),List.of("purchase-operations"),"Mua hàng — gói nghiệp vụ",">=1.1.0 <2.0.0");}
 public List<NavigationWorkspaceDescriptor> navigationWorkspaces(){return RetailModule.allWorkspaces().stream().filter(w->w.key().equals("retail-purchase")).toList();}
 public List<NavigationItemDescriptor> navigationItems(){return RetailModule.allItems().stream().filter(i->BusinessModules.screenOwner(i.viewKey().substring(7)).equals("aicrm-purchase")).toList();}
}
