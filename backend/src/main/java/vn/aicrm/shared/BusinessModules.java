package vn.aicrm.shared;
import java.util.*;
public final class BusinessModules {
 public static final String SHARED="aicrm-retail",STOCK="aicrm-stock",SALES="aicrm-sales",PURCHASE="aicrm-purchase";
 private BusinessModules(){}
 public static void registerResources(String owner,vn.coreplatform.kernel.ResourceRegistry resources){
  for(var feature:RetailPermissions.FEATURES)if(owner(feature.code()).equals(owner)){
   resources.reassignLegacyOwner(RetailPermissions.resource(feature.code()),SHARED,owner);
   resources.register(new vn.coreplatform.kernel.ResourceDescriptor(RetailPermissions.resource(feature.code()),feature.name(),owner,"DOMAIN","1.0.0",feature.actions(),"ALWAYS","CONFIDENTIAL"));
  }
 }
 public static String owner(String feature){return switch(feature){
  case "pos","orders","delivery","returns","warranty","overview-sales","report-sales"->SALES;
  case "purchase-order","purchase-receipt","purchase-return","supplier","overview-purchase","report-purchase"->PURCHASE;
  case "stock","issue","transfer","count","opening-stock","warehouses","warehouse-config","overview-stock","report-stock"->STOCK;
  default->SHARED;};}
 public static String screenOwner(String screen){return switch(screen){case "purchase"->PURCHASE;case "suppliers"->PURCHASE;case "sales-customers"->SALES;case "returns"->SALES;default->owner(screen);};}
}
