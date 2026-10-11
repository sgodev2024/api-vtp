package vn.coreplatform.permission;

import java.util.*;
import com.fasterxml.jackson.databind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import vn.coreplatform.audit.AuditService;
import vn.coreplatform.shared.ApiExceptionHandler.ApiProblem;

/** User-role assignments belong to core identity, independent of installed business modules. */
@RestController
@RequestMapping("/api/v1/access/users")
public class UserRoleController {
 private final JdbcTemplate db;private final PermissionService permissions;private final AuditService audit;private final ObjectMapper json;
 public UserRoleController(JdbcTemplate db,PermissionService permissions,AuditService audit,ObjectMapper json){this.db=db;this.permissions=permissions;this.audit=audit;this.json=json;}
 private void manage(Authentication a){permissions.require(a,"ACCESS_ADMIN","MANAGE",null);}
 private void exists(UUID tenant,UUID account){if(db.queryForObject("select count(*) from identity.account where tenant_id=? and id=?",Integer.class,tenant,account)==0)throw new ApiProblem(HttpStatus.NOT_FOUND,"USER_NOT_FOUND","Người dùng không tồn tại");}
 @GetMapping("/{id}/roles") public Map<String,Object> roles(Authentication a,@PathVariable UUID id){manage(a);UUID t=permissions.tenant(a);exists(t,id);return Map.of("revision",db.queryForObject("select revision from identity.permission_revision where tenant_id=?",Long.class,t),"roleIds",db.queryForList("select role_id from identity.account_role where tenant_id=? and account_id=? order by role_id",t,id).stream().map(row->row.get("role_id")).toList());}
 @PostMapping("/{id}/roles") @Transactional public Object save(Authentication a,@PathVariable UUID id,@RequestBody JsonNode x){
  manage(a);var tenant=permissions.tenant(a);if(id.equals(permissions.account(a)))throw new ApiProblem(HttpStatus.FORBIDDEN,"SELF_PERMISSION_CHANGE","Không thay đổi vai trò của chính mình");
  String reason=x.path("reason").asText("").trim();if(reason.isEmpty()||reason.length()>1000)throw new ApiProblem(HttpStatus.UNPROCESSABLE_ENTITY,"INVALID_FIELD","Lý do bắt buộc, tối đa 1000 ký tự");
  exists(tenant,id);var revision=db.queryForObject("select revision from identity.permission_revision where tenant_id=? for update",Long.class,tenant);if(x.path("revision").asLong(-1)!=revision)throw new ApiProblem(HttpStatus.CONFLICT,"VERSION_CONFLICT","Phân quyền đã thay đổi; tải lại");
  if(!x.path("roleIds").isArray()||x.path("roleIds").size()>100)throw new ApiProblem(HttpStatus.UNPROCESSABLE_ENTITY,"INVALID_ROLE","Chọn danh sách vai trò, tối đa 100");
  var roles=new LinkedHashSet<UUID>();for(var value:x.path("roleIds")){UUID role;try{role=UUID.fromString(value.asText());}catch(IllegalArgumentException ex){throw new ApiProblem(HttpStatus.UNPROCESSABLE_ENTITY,"INVALID_ROLE","Mã vai trò không hợp lệ");}if(db.queryForObject("select count(*) from identity.role where tenant_id=? and id=?",Integer.class,tenant,role)==0)throw new ApiProblem(HttpStatus.UNPROCESSABLE_ENTITY,"INVALID_ROLE","Vai trò không thuộc công ty");roles.add(role);}
  var before=db.queryForList("select role_id from identity.account_role where tenant_id=? and account_id=?",tenant,id);db.update("delete from identity.account_role where tenant_id=? and account_id=?",tenant,id);for(UUID role:roles)db.update("insert into identity.account_role(tenant_id,account_id,role_id) values(?,?,?)",tenant,id,role);
  db.update("update identity.permission_revision set revision=revision+1,updated_at=now() where tenant_id=?",tenant);
  audit.record(permissions.tenantKey(a),permissions.account(a),a.getName(),"USER_ROLES_CHANGE","ACCESS_ADMIN",id.toString(),"SUCCESS",json.valueToTree(Map.of("reason",reason,"before",before,"after",roles)).toString());return roles(a,id);
 }
}