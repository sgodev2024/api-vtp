package vn.aicrm.operations;
import java.util.*;import org.springframework.stereotype.Component;import vn.coreplatform.kernel.*;import vn.aicrm.shared.*;
@Component @org.springframework.core.annotation.Order(20)
public class OperationsModule implements ModuleContributor,org.springframework.boot.ApplicationRunner {
 private final ResourceRegistry resources;public OperationsModule(ResourceRegistry resources){this.resources=resources;}
 public void run(org.springframework.boot.ApplicationArguments args){BusinessModules.registerResources("aicrm-operations",resources);}
 public ModuleDescriptor descriptor(){return new ModuleDescriptor("aicrm-operations","Vận hành chung","1.0.0",List.of("aicrm-products","aicrm-retail"),List.of("aicrm-operations-capability"),"Vận hành chung — gói nghiệp vụ",">=1.1.0 <2.0.0");}
 public List<NavigationWorkspaceDescriptor> navigationWorkspaces(){return RetailModule.allWorkspaces().stream().filter(w->w.key().equals("retail-general")).toList();}
 public List<NavigationItemDescriptor> navigationItems(){return RetailModule.allItems().stream().filter(i->BusinessModules.screenOwner(i.viewKey().substring(7)).equals("aicrm-operations")).toList();}
}
