package vn.aicrm;
import org.junit.jupiter.api.Test;import com.tngtech.archunit.core.importer.ClassFileImporter;import com.tngtech.archunit.core.importer.ImportOption;import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
class BusinessBoundaryTest {
 @Test void reusableSharedCodeDoesNotImportBusinessImplementations(){var classes=new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests()).importPackages("vn.aicrm");noClasses().that().resideInAPackage("vn.aicrm.shared..").should().dependOnClassesThat().resideInAnyPackage("vn.aicrm.sales..","vn.aicrm.inventory..","vn.aicrm.purchase..").check(classes);}
 @Test void salesAndPurchaseOnlyUseSharedContracts(){var classes=new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests()).importPackages("vn.aicrm");noClasses().that().resideInAnyPackage("vn.aicrm.sales..","vn.aicrm.purchase..").should().dependOnClassesThat().resideInAPackage("vn.aicrm.inventory..").check(classes);}
}
