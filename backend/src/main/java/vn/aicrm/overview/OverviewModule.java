package vn.aicrm.overview;
import java.util.*;import org.springframework.stereotype.Component;import vn.coreplatform.kernel.*;import vn.aicrm.shared.*;
@Component @org.springframework.core.annotation.Order(20)
public class OverviewModule implements ModuleContributor,org.springframework.boot.ApplicationRunner {
 private final ResourceRegistry resources;public OverviewModule(ResourceRegistry resources){this.resources=resources;}
 public void run(org.springframework.boot.ApplicationArguments args){BusinessModules.registerResources("aicrm-overview",resources);}
 public ModuleDescriptor descriptor(){return new ModuleDescriptor("aicrm-overview","Tổng quan","1.0.0",List.of("aicrm-retail","aicrm-accounting","aicrm-products"),List.of("aicrm-overview-capability"),"Tổng quan — gói nghiệp vụ",">=1.1.0 <2.0.0");}
 public List<NavigationWorkspaceDescriptor> navigationWorkspaces(){return RetailModule.allWorkspaces().stream().filter(w->w.key().equals("retail-overview")).toList();}
 public List<NavigationItemDescriptor> navigationItems(){return RetailModule.allItems().stream().filter(i->BusinessModules.screenOwner(i.viewKey().substring(7)).equals("aicrm-overview")).toList();}
}
