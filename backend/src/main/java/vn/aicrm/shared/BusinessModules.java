package vn.aicrm.shared;
import java.util.*;
public final class BusinessModules {
 public static final String SHARED="aicrm-retail",STOCK="aicrm-stock",SALES="aicrm-sales",PURCHASE="aicrm-purchase",PRODUCTS="aicrm-products",PARTNERS="aicrm-partners",CRM="aicrm-crm",ACCOUNTING="aicrm-accounting",OPERATIONS="aicrm-operations",OVERVIEW="aicrm-overview",REPORTS="aicrm-reports";
 public static final List<String> ALL=List.of(SHARED,PRODUCTS,PARTNERS,OPERATIONS,ACCOUNTING,STOCK,SALES,PURCHASE,CRM,OVERVIEW,REPORTS);
 private BusinessModules(){}
 public static void registerResources(String owner,vn.coreplatform.kernel.ResourceRegistry resources){for(var feature:RetailPermissions.FEATURES)if(owner(feature.code()).equals(owner)){String key=RetailPermissions.resource(feature.code());for(String legacy:List.of(SHARED,SALES,PURCHASE,STOCK))resources.reassignLegacyOwner(key,legacy,owner);resources.register(new vn.coreplatform.kernel.ResourceDescriptor(key,feature.name(),owner,"DOMAIN","1.0.0",feature.actions(),"ALWAYS","CONFIDENTIAL"));}}
 public static String owner(String f){if(f.startsWith("overview-"))return OVERVIEW;if(f.startsWith("report-"))return REPORTS;return switch(f){
 case "pos","orders","delivery","returns","warranty"->SALES;case "purchase-order","purchase-receipt","purchase-return","supplier"->PURCHASE;case "stock","issue","transfer","count","opening-stock","warehouses","warehouse-config"->STOCK;
 case "products","product-groups","product-brands","product-units","uom-conversions","product-attributes"->PRODUCTS;case "customer","partners"->PARTNERS;case "crm"->CRM;case "receipt","payment","receivables","payables","opening-ar","opening-ap","commission","refund","sensitive"->ACCOUNTING;case "locations","setup","scope","approvals"->OPERATIONS;default->SHARED;};}
 public static String screenOwner(String s){return switch(s){case "purchase","suppliers"->PURCHASE;case "sales-customers","returns"->SALES;case "master-customers","master-partners"->PARTNERS;case "customers"->CRM;case "cashflow","debt"->ACCOUNTING;case "overview"->OVERVIEW;case "reports"->REPORTS;default->owner(s);};}
 public static String packageOwner(String pkg){return switch(pkg.substring(pkg.lastIndexOf('.')+1)){case "sales"->SALES;case "inventory"->STOCK;case "purchase"->PURCHASE;case "products"->PRODUCTS;case "partners"->PARTNERS;case "crm"->CRM;case "accounting"->ACCOUNTING;case "operations"->OPERATIONS;case "overview"->OVERVIEW;case "reports"->REPORTS;default->SHARED;};}
}
