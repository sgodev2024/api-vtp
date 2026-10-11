package vn.aicrm.crm;
import java.util.*;import org.springframework.stereotype.Component;import vn.coreplatform.kernel.*;import vn.aicrm.shared.*;
@Component @org.springframework.core.annotation.Order(20)
public class CrmModule implements ModuleContributor,org.springframework.boot.ApplicationRunner {
 private final ResourceRegistry resources;public CrmModule(ResourceRegistry resources){this.resources=resources;}
 public void run(org.springframework.boot.ApplicationArguments args){BusinessModules.registerResources("aicrm-crm",resources);}
 public ModuleDescriptor descriptor(){return new ModuleDescriptor("aicrm-crm","CRM","1.0.0",List.of("aicrm-operations","aicrm-products","aicrm-retail","aicrm-partners"),List.of("aicrm-crm-capability"),"CRM — gói nghiệp vụ",">=1.1.0 <2.0.0");}
 public List<NavigationWorkspaceDescriptor> navigationWorkspaces(){return RetailModule.allWorkspaces().stream().filter(w->w.key().equals("retail-crm")).toList();}
 public List<NavigationItemDescriptor> navigationItems(){return RetailModule.allItems().stream().filter(i->BusinessModules.screenOwner(i.viewKey().substring(7)).equals("aicrm-crm")).toList();}
}
