package vn.aicrm;
import org.junit.jupiter.api.Test;import com.tngtech.archunit.core.importer.ClassFileImporter;import com.tngtech.archunit.core.importer.ImportOption;import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
class BusinessBoundaryTest {
 private com.tngtech.archunit.core.domain.JavaClasses classes(){return new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests()).importPackages("vn.aicrm","vn.coreplatform");}
 @Test void foundationDoesNotImportBusinessImplementations(){noClasses().that().resideInAPackage("vn.aicrm.shared..").should().dependOnClassesThat().resideInAnyPackage("vn.aicrm.sales..","vn.aicrm.inventory..","vn.aicrm.purchase..","vn.aicrm.products..","vn.aicrm.partners..","vn.aicrm.accounting..","vn.aicrm.operations..","vn.aicrm.crm..","vn.aicrm.overview..","vn.aicrm.reports..").check(classes());}
 @Test void coreHasNoBusinessCompileDependency(){noClasses().that().resideInAPackage("vn.coreplatform..").should().dependOnClassesThat().resideInAPackage("vn.aicrm..").check(classes());}
 @Test void businessPackagesOnlyUseFoundationContracts(){String[] pkgs={"sales","inventory","purchase","products","partners","accounting","operations","crm","overview","reports"};var imported=classes();for(String p:pkgs)for(String q:pkgs)if(!p.equals(q))noClasses().that().resideInAPackage("vn.aicrm."+p+"..").should().dependOnClassesThat().resideInAPackage("vn.aicrm."+q+"..").check(imported);}
}
