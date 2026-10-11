package vn.coreplatform.shared;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import static org.junit.jupiter.api.Assertions.*;
class ApiExceptionHandlerTest {
 @Test void uninstalledApiReturns404WithoutExceptionLeak(){var request=new MockHttpServletRequest("GET","/api/v1/not-installed");var result=new ApiExceptionHandler().missingRoute(new NoResourceFoundException(HttpMethod.GET,"api/v1/not-installed"),request);assertEquals(404,result.getStatusCode().value());assertEquals("NOT_FOUND",result.getBody().getProperties().get("code"));assertEquals("/api/v1/not-installed",result.getBody().getProperties().get("path"));assertFalse(result.getBody().getDetail().contains("Exception"));}
}