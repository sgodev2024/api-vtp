package vn.aicrm.accounting;
import java.util.*;import org.springframework.stereotype.Component;import vn.coreplatform.kernel.*;import vn.aicrm.shared.*;
@Component @org.springframework.core.annotation.Order(20)
public class AccountingModule implements ModuleContributor,org.springframework.boot.ApplicationRunner {
 private final ResourceRegistry resources;public AccountingModule(ResourceRegistry resources){this.resources=resources;}
 public void run(org.springframework.boot.ApplicationArguments args){BusinessModules.registerResources("aicrm-accounting",resources);}
 public ModuleDescriptor descriptor(){return new ModuleDescriptor("aicrm-accounting","Kế toán","1.0.0",List.of("aicrm-operations","aicrm-retail","aicrm-partners"),List.of("aicrm-accounting-capability"),"Kế toán — gói nghiệp vụ",">=1.1.0 <2.0.0");}
 public List<NavigationWorkspaceDescriptor> navigationWorkspaces(){return RetailModule.allWorkspaces().stream().filter(w->w.key().equals("retail-accounting")).toList();}
 public List<NavigationItemDescriptor> navigationItems(){return RetailModule.allItems().stream().filter(i->BusinessModules.screenOwner(i.viewKey().substring(7)).equals("aicrm-accounting")).toList();}
}
