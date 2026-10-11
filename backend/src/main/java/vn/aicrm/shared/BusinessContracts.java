package vn.aicrm.shared;
import java.lang.reflect.*;import org.springframework.context.annotation.*;import org.springframework.beans.factory.BeanFactory;
@Configuration
public class BusinessContracts {
 private <T>T adapter(Class<T> type,String bean,BeanFactory factory){return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(proxy,method,args)->{if(method.getDeclaringClass()==Object.class)return switch(method.getName()){case "toString"->"Business contract "+type.getSimpleName();case "hashCode"->System.identityHashCode(proxy);default->proxy==args[0];};if(!factory.containsBean(bean))throw RetailAccess.problem("MODULE_NOT_INSTALLED","Chưa cài gói cung cấp "+type.getSimpleName(),org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE);try{return method.invoke(factory.getBean(bean),args);}catch(InvocationTargetException ex){throw ex.getCause();}}));}
 @Bean @Primary CatalogPort catalogPort(BeanFactory factory){return adapter(CatalogPort.class,"retailCatalog",factory);}
 @Bean @Primary PartnersPort partnersPort(BeanFactory factory){return adapter(PartnersPort.class,"retailPartners",factory);}
 @Bean @Primary FinancePort financePort(BeanFactory factory){return adapter(FinancePort.class,"retailFinance",factory);}
 @Bean @Primary CommissionsPort commissionsPort(BeanFactory factory){return adapter(CommissionsPort.class,"retailCommissions",factory);}
 @Bean @Primary LocationsPort locationsPort(BeanFactory factory){return adapter(LocationsPort.class,"retailLocations",factory);}
 @Bean @Primary ImportPort importPort(BeanFactory factory){return adapter(ImportPort.class,"retailImport",factory);}
 @Bean @Primary ProductConfigPort productConfigPort(BeanFactory factory){return adapter(ProductConfigPort.class,"retailWarehouseConfigService",factory);}
 @Bean @Primary InventoryPort inventoryPort(BeanFactory factory){return adapter(InventoryPort.class,"retailInventory",factory);}
 @Bean @Primary SalesOperationsPort salesOperationsPort(BeanFactory factory){return adapter(SalesOperationsPort.class,"retailOperations",factory);}
 @Bean @Primary ReportsPort reportsPort(BeanFactory factory){return adapter(ReportsPort.class,"reports",factory);}
}
