package vn.aicrm.reports;
import vn.aicrm.shared.*;

import java.util.*;
import java.math.*;
import java.time.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import static vn.aicrm.shared.RetailAccess.*;

@Service
public class Reports implements ReportsPort {
  private final RetailAccess r;private final PartnersPort partners;private final RetailDocuments documents;private final FinancePort finance;
  public Reports(RetailAccess r,RetailDocuments documents,FinancePort finance,PartnersPort partners){this.r=r;this.partners=partners;this.documents=documents;this.finance=finance;}
private String reportFilter(Authentication a,String alias){return "("+(r.granular(a,"report-sales","VIEW")?alias+".type in ('POS','ISSUE','SALE_RETURN')":"false")+" or "+(r.granular(a,"report-purchase","VIEW")?alias+".type in ('PURCHASE_RECEIPT','PURCHASE_RETURN')":"false")+") and "+r.documentTypes(a,alias,"VIEW");}
public Object report(Authentication a,String from,String to){r.require(a,"REPORT");LocalDate start,end;try{start=LocalDate.parse(from);end=LocalDate.parse(to);}catch(Exception e){throw problem("INVALID_DATE","Khoảng báo cáo không hợp lệ");}if(end.isBefore(start)||end.isAfter(start.plusYears(3)))throw problem("INVALID_DATE","Khoảng báo cáo tối đa 3 năm");var t=r.tenant(a);var out=new LinkedHashMap<String,Object>();out.put("sales",r.db.queryForList("select d.id,d.number,d.type,d.channel,d.total,d.fee,d.cost_pending,d.posted_at,p.name partner,coalesce(sum(l.cost),0) cost from retail.document d left join retail.line l on l.tenant_id=d.tenant_id and l.document_id=d.id left join retail.partner p on p.tenant_id=d.tenant_id and p.id=d.partner_id where d.tenant_id=? and "+r.scope(a,"d")+" and "+reportFilter(a,"d")+" and d.status='POSTED' and d.type in ('POS','ISSUE','SALE_RETURN','PURCHASE_RECEIPT','PURCHASE_RETURN') and (d.posted_at at time zone 'Asia/Ho_Chi_Minh')::date between ? and ? group by d.id,p.name order by d.posted_at desc",t,start,end));if(!r.can(a,"COST"))((List<Map<String,Object>>)out.get("sales")).forEach(x->x.remove("cost"));out.put("inventory",r.db.queryForList("select w.name warehouse,s.code,s.name,coalesce(sum(case when (m.created_at at time zone 'Asia/Ho_Chi_Minh')::date<? then m.qty else 0 end),0) opening,coalesce(sum(case when (m.created_at at time zone 'Asia/Ho_Chi_Minh')::date between ? and ? and m.qty>0 then m.qty else 0 end),0) incoming,coalesce(sum(case when (m.created_at at time zone 'Asia/Ho_Chi_Minh')::date between ? and ? and m.qty<0 then -m.qty else 0 end),0) outgoing,sum(m.qty) closing from retail.movement m join retail.warehouse w on w.tenant_id=m.tenant_id and w.id=m.warehouse_id join retail.sku s on s.tenant_id=m.tenant_id and s.id=m.sku_id where m.tenant_id=? and "+r.scope(a,"m")+" and "+r.granular(a,"report-stock","VIEW")+" and (m.created_at at time zone 'Asia/Ho_Chi_Minh')::date<=? group by w.name,s.code,s.name order by w.name,s.code",start,start,end,start,end,t,end));return out;}
}
