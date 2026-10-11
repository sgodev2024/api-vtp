package vn.aicrm.shared;
import jakarta.servlet.http.*;import org.springframework.context.annotation.Configuration;import org.springframework.web.servlet.*;import org.springframework.web.servlet.config.annotation.*;import org.springframework.web.method.HandlerMethod;import vn.coreplatform.kernel.ModuleRuntime;
/** Applies also to privileged accounts and direct/bookmarked API calls. */
@Configuration
public class ModuleApiBoundary implements WebMvcConfigurer {
 private final ModuleRuntime modules;
 public ModuleApiBoundary(ModuleRuntime modules){this.modules=modules;}
 public void addInterceptors(InterceptorRegistry registry){registry.addInterceptor(new HandlerInterceptor(){
  public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler){
   if(handler instanceof HandlerMethod method){String pkg=method.getBeanType().getPackageName();String owner=BusinessModules.packageOwner(pkg);modules.require(owner);}
   return true;
  }
 }).addPathPatterns("/api/v1/retail/**");}
}
