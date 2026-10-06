package vn.aicrm.retail;

import java.math.*;
import java.util.*;
import java.time.*;
import com.fasterxml.jackson.databind.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.coreplatform.permission.PermissionService;
import vn.coreplatform.shared.ApiExceptionHandler.ApiProblem;

@Service
public class RetailAccess {
  public final JdbcTemplate db;
  public final PermissionService permissions;
  public final ObjectMapper json;
  public RetailAccess(JdbcTemplate db,PermissionService permissions,ObjectMapper json) {this.db=db;this.permissions=permissions;this.json=json;}
  public UUID tenant(Authentication a){return permissions.tenant(a);}
  public UUID actor(Authentication a){return permissions.account(a);}
  public void require(Authentication a,String action){permissions.require(a,"aicrm-retail",action,null);}
  public boolean can(Authentication a,String action){return permissions.decide(a,"aicrm-retail",action,null).allowed();}
  public boolean all(Authentication a){return can(a,"ALL_BRANCHES");}
  public List<Map<String,Object>> warehouses(Authentication a){
    return db.queryForList("select w.* from retail.warehouse w where w.tenant_id=? and (? or exists(select 1 from retail.access_scope acl where acl.account_id=? and acl.warehouse_id=w.id and acl.tenant_id=w.tenant_id)) order by w.code",tenant(a),all(a),actor(a));
  }
  public Map<String,Object> warehouse(Authentication a,UUID id){
    return warehouses(a).stream().filter(w->id.equals(w.get("id"))).findFirst().orElseThrow(()->problem("SCOPE_DENIED","Không có quyền tại kho này",HttpStatus.FORBIDDEN));
  }
  public void branch(Authentication a,UUID id){if(!all(a)&&db.queryForObject("select count(*) from retail.access_scope where tenant_id=? and account_id=? and branch_id=?",Integer.class,tenant(a),actor(a),id)==0)throw problem("SCOPE_DENIED","Không có quyền tại chi nhánh này",HttpStatus.FORBIDDEN);}
  public String scope(Authentication a,String alias){return all(a)?"true":"exists(select 1 from retail.access_scope acl where acl.tenant_id="+alias+".tenant_id and acl.account_id='"+actor(a)+"'::uuid and acl.warehouse_id="+alias+".warehouse_id)";}
  public String branchScope(Authentication a,String alias){return all(a)?"true":"exists(select 1 from retail.access_scope acl where acl.tenant_id="+alias+".tenant_id and acl.account_id='"+actor(a)+"'::uuid and acl.branch_id="+alias+".branch_id)";}
  public Map<String,Object> one(String table,UUID tenant,UUID id){
    var rows=db.queryForList("select * from retail."+table+" where tenant_id=? and id=?",tenant,id);
    if(rows.isEmpty())throw problem("NOT_FOUND","Không tìm thấy dữ liệu",HttpStatus.NOT_FOUND);return rows.getFirst();
  }
  public static ApiProblem problem(String code,String text){return problem(code,text,HttpStatus.UNPROCESSABLE_ENTITY);}
  public static ApiProblem problem(String code,String text,HttpStatus status){return new ApiProblem(status,code,text);}
  public static String text(JsonNode n,String key,String fallback){return n.path(key).isNull()?fallback:n.path(key).asText(fallback).trim();}
  public static String required(JsonNode n,String key,int max){var v=text(n,key,"");if(v.isBlank()||v.length()>max)throw problem("INVALID_FIELD","Trường "+key+" bắt buộc, tối đa "+max+" ký tự");return v;}
  public static UUID uuid(JsonNode n,String key){try{return UUID.fromString(required(n,key,36));}catch(IllegalArgumentException e){throw problem("INVALID_ID","Mã tham chiếu "+key+" không hợp lệ");}}
  public static UUID optional(JsonNode n,String key){return text(n,key,"").isBlank()?null:uuid(n,key);}
  public static BigDecimal num(JsonNode n,String key,BigDecimal fallback){try{var raw=n.path(key);var v=raw.isMissingNode()||raw.isNull()||raw.asText().isBlank()?fallback:new BigDecimal(raw.asText());if(v.stripTrailingZeros().precision()>20||v.stripTrailingZeros().scale()>8)throw new NumberFormatException();return v;}catch(Exception e){throw problem("INVALID_NUMBER","Số "+key+" không hợp lệ");}}
  public static BigDecimal decimal(Object value){return value==null?BigDecimal.ZERO:new BigDecimal(value.toString());}
  public static BigDecimal money(BigDecimal v){return v.setScale(0,RoundingMode.HALF_UP);}
  public static BigDecimal quantity(BigDecimal v){if(v.stripTrailingZeros().scale()>4||v.signum()<0)throw problem("INVALID_QUANTITY","Số lượng tối đa 4 chữ số thập phân, không âm");return v.setScale(4);}
  public static BigDecimal positive(JsonNode n,String key){var v=quantity(num(n,key,BigDecimal.ZERO));if(v.signum()<=0)throw problem("INVALID_QUANTITY","Số lượng phải lớn hơn 0");return v;}
  public static LocalDate date(JsonNode n,String key){try{return LocalDate.parse(required(n,key,10));}catch(Exception e){throw problem("INVALID_DATE","Ngày "+key+" không hợp lệ");}}
  public static LocalDate today(){return LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));}
  public JsonNode node(Object value){if(value==null)return json.createObjectNode();try{return json.readTree(value.toString());}catch(Exception e){throw new IllegalStateException(e);}}
  public Map<String,Object> clean(Map<String,Object> row){var m=new LinkedHashMap<>(row); for(var k:List.of("roles","units","metadata","sku_snapshot","serials","segments","rule_snapshot","rows","errors","result","details","count_lots"))if(m.get(k)!=null)m.put(k,node(m.get(k)));return m;}
}



