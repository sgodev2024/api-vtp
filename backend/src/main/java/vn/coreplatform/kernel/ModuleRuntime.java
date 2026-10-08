package vn.coreplatform.kernel;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import vn.coreplatform.shared.ApiExceptionHandler.ApiProblem;
/** Runtime switches are deployment-wide, evaluated independently of account roles. */
@Service
public class ModuleRuntime {
 private final ModuleRegistry registry; private final JdbcTemplate db;
 public ModuleRuntime(ModuleRegistry registry,JdbcTemplate db){this.registry=registry;this.db=db;}
 public List<String> dependencies(String key){return registry.modules().stream().filter(m->m.key().equals(key)).findFirst().map(ModuleDescriptor::dependencies).orElse(List.of());}
 public Map<String,String> statuses(){var attrs=org.springframework.web.context.request.RequestContextHolder.getRequestAttributes(); var request=attrs instanceof org.springframework.web.context.request.ServletRequestAttributes servlet?servlet.getRequest():null; String cache=ModuleRuntime.class.getName(); if(request!=null&&request.getAttribute(cache) instanceof Map snapshot)return (Map<String,String>)snapshot; var result=new LinkedHashMap<String,String>();db.query("select module_key,status from platform.module",rs->{result.put(rs.getString(1),rs.getString(2));});if(request!=null)request.setAttribute(cache,result);return result;}
 public boolean available(String key){return available(key,statuses(),new HashSet<>());}
 private boolean available(String key,Map<String,String> states,Set<String> seen){if(registry.modules().stream().noneMatch(m->m.key().equals(key)))return false;if(!seen.add(key))return true;if(!states.containsKey(key)||"DISABLED".equals(states.get(key)))return false;return dependencies(key).stream().allMatch(d->available(d,states,seen));}
 public void require(String key){if(!available(key))throw new ApiProblem(HttpStatus.FORBIDDEN,"MODULE_DISABLED","Module hoặc phụ thuộc đang tắt: "+key);}
 /** Must be invoked inside the command transaction; prevents switching off during a write. */
 public void lockForCommand(){db.queryForList("select module_key from platform.module order by module_key for share");var attrs=org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();if(attrs instanceof org.springframework.web.context.request.ServletRequestAttributes servlet)servlet.getRequest().removeAttribute(ModuleRuntime.class.getName());}
 @Transactional
 public void change(UUID id,String state){
  var rows=db.queryForList("select id,module_key,status from platform.module order by module_key for update");
  var target=rows.stream().filter(r->id.equals(r.get("id"))).findFirst().orElseThrow(()->new ApiProblem(HttpStatus.NOT_FOUND,"MODULE_NOT_FOUND","Module không tồn tại"));
  String key=target.get("module_key").toString();var states=new LinkedHashMap<String,String>();rows.forEach(r->states.put(r.get("module_key").toString(),r.get("status").toString()));
  if("DISABLED".equals(state)){
   if(!key.startsWith("aicrm-"))throw new ApiProblem(HttpStatus.CONFLICT,"CORE_MODULE_REQUIRED","Không được tắt module nền tảng trong runtime");
   var dependents=registry.modules().stream().filter(m->!m.key().equals(key)&&!"DISABLED".equals(states.get(m.key()))&&dependsOn(m.key(),key,new HashSet<>())).map(ModuleDescriptor::key).toList();
   if(!dependents.isEmpty())throw new ApiProblem(HttpStatus.CONFLICT,"MODULE_DEPENDENTS_ACTIVE","Tắt các module phụ thuộc trước: "+String.join(", ",dependents));
  }else{states.put(key,state);if(!available(key,states,new HashSet<>()))throw new ApiProblem(HttpStatus.CONFLICT,"MODULE_DEPENDENCY_DISABLED","Bật các phụ thuộc trước: "+String.join(", ",dependencies(key)));}
  db.update("update platform.module set status=? where id=?",state,id);
  var attrs=org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();if(attrs instanceof org.springframework.web.context.request.ServletRequestAttributes servlet)servlet.getRequest().removeAttribute(ModuleRuntime.class.getName());
 }
 private boolean dependsOn(String from,String target,Set<String> seen){return seen.add(from)&&dependencies(from).stream().anyMatch(d->d.equals(target)||dependsOn(d,target,seen));}
}
