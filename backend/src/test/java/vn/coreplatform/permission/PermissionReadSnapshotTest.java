package vn.coreplatform.permission;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.coreplatform.audit.AuditService;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class PermissionReadSnapshotTest {
  @AfterEach void cleanup(){RequestContextHolder.resetRequestAttributes();}

  @Test void readSnapshotIsDiscardedOnNextRequest(){
    var db=mock(JdbcTemplate.class);var tenant=UUID.randomUUID();
    var auth=new UsernamePasswordAuthenticationToken("user","unused");
    auth.setDetails(Map.of("tenantId",tenant,"accountId",UUID.randomUUID()));
    var service=new PermissionService(db,mock(AuditService.class));
    String sql="select revision from identity.permission_revision where tenant_id=?";
    when(db.queryForObject(sql,Long.class,tenant)).thenReturn(1L,2L);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest("GET","/catalog")));
    assertThat(service.decide(auth,"products","VIEW",null).allowed()).isFalse();
    service.decide(auth,"products","CREATE",null);
    verify(db,times(1)).queryForObject(sql,Long.class,tenant);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest("GET","/catalog")));
    service.decide(auth,"products","VIEW",null);
    verify(db,times(2)).queryForObject(sql,Long.class,tenant);
  }

  @Test void writesAlwaysReadCurrentRevision(){
    var db=mock(JdbcTemplate.class);var tenant=UUID.randomUUID();
    var auth=new UsernamePasswordAuthenticationToken("user","unused");
    auth.setDetails(Map.of("tenantId",tenant,"accountId",UUID.randomUUID()));
    var service=new PermissionService(db,mock(AuditService.class));
    String sql="select revision from identity.permission_revision where tenant_id=?";
    when(db.queryForObject(sql,Long.class,tenant)).thenReturn(1L,2L);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest("POST","/permissions")));
    service.decide(auth,"products","VIEW",null);
    service.decide(auth,"products","CREATE",null);
    verify(db,times(2)).queryForObject(sql,Long.class,tenant);
  }
}
