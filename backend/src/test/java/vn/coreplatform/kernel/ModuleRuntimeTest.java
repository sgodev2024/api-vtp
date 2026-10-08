package vn.coreplatform.kernel;
import java.util.*;import java.sql.ResultSet;import org.junit.jupiter.api.Test;import org.springframework.jdbc.core.*;import static org.mockito.Mockito.*;import static org.junit.jupiter.api.Assertions.*;import vn.coreplatform.shared.ApiExceptionHandler.ApiProblem;
class ModuleRuntimeTest {
 private ModuleRuntime runtime(Map<String,String> states){var db=mock(JdbcTemplate.class);var registry=mock(ModuleRegistry.class);when(registry.modules()).thenReturn(List.of(new ModuleDescriptor("aicrm-retail","Shared","1.0.0",List.of(),List.of(),""),new ModuleDescriptor("aicrm-stock","Stock","1.0.0",List.of("aicrm-retail"),List.of(),""),new ModuleDescriptor("aicrm-sales","Sales","1.0.0",List.of("aicrm-stock"),List.of(),"")));
 doAnswer(call->{RowCallbackHandler callback=call.getArgument(1);for(var state:states.entrySet()){var rs=mock(ResultSet.class);when(rs.getString(1)).thenReturn(state.getKey());when(rs.getString(2)).thenReturn(state.getValue());callback.processRow(rs);}return null;}).when(db).query(anyString(),any(RowCallbackHandler.class));return new ModuleRuntime(registry,db);}
 @Test void staleDatabaseModuleCannotEnableMissingCode(){assertFalse(runtime(Map.of("aicrm-ghost","HEALTHY")).available("aicrm-ghost"));}
 @Test void missingModuleFailsClosed(){assertFalse(runtime(Map.of()).available("aicrm-sales"));}
 @Test void disabledTransitiveDependencyBlocksSales(){var r=runtime(Map.of("aicrm-retail","DISABLED","aicrm-stock","HEALTHY","aicrm-sales","HEALTHY"));assertFalse(r.available("aicrm-sales"));assertThrows(ApiProblem.class,()->r.require("aicrm-sales"));}
 @Test void enabledDependenciesAllowSales(){assertTrue(runtime(Map.of("aicrm-retail","HEALTHY","aicrm-stock","ATTENTION","aicrm-sales","HEALTHY")).available("aicrm-sales"));}
 @Test void salesSwitchDoesNotDisableSharedMaster(){var r=runtime(Map.of("aicrm-retail","HEALTHY","aicrm-stock","HEALTHY","aicrm-sales","DISABLED"));assertTrue(r.available("aicrm-retail"));assertTrue(r.available("aicrm-stock"));assertFalse(r.available("aicrm-sales"));}
}
