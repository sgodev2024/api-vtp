package vn.aicrm.crm;
import vn.aicrm.shared.*;

import java.util.*;
import java.math.*;
import java.time.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import static vn.aicrm.shared.RetailAccess.*;

@Service
public class CustomerCare {
  private final RetailAccess r;private final PartnersPort partners;private final RetailDocuments documents;private final FinancePort finance;
  public CustomerCare(RetailAccess r,RetailDocuments documents,FinancePort finance,PartnersPort partners){this.r=r;this.partners=partners;this.documents=documents;this.finance=finance;}
public Object care(Authentication a,UUID partner){r.require(a,"CRM");var d=(Map<String,Object>)partners.detail(a,partner);return Map.of("notes",d.get("notes"),"history",d.get("history"));}
public Object createCare(Authentication a,JsonNode x){r.require(a,"CRM");UUID partner=uuid(x,"partnerId"),branch=uuid(x,"branchId"),id=UUID.randomUUID();r.one("partner",r.tenant(a),partner);r.branch(a,branch);Instant due=null;try{if(!text(x,"dueAt","").isBlank())due=Instant.parse(text(x,"dueAt",""));}catch(Exception e){throw problem("INVALID_DATE","Lịch chăm sóc không hợp lệ");}r.db.update("insert into retail.care(id,tenant_id,branch_id,partner_id,note,due_at,creator_id) values(?,?,?,?,?,?,?)",id,r.tenant(a),branch,partner,required(x,"note",4000),due==null?null:java.sql.Timestamp.from(due),r.actor(a));return Map.of("id",id);}
public Object completeCare(Authentication a,UUID id){r.require(a,"CRM");var row=r.one("care",r.tenant(a),id);r.branch(a,(UUID)row.get("branch_id"));r.db.update("update retail.care set done=true where tenant_id=? and id=?",r.tenant(a),id);return Map.of("ok",true);}
}
