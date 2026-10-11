package vn.aicrm.overview;
import vn.aicrm.shared.*;

import java.util.*;
import java.math.*;
import java.time.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import static vn.aicrm.shared.RetailAccess.*;

@Service
public class Overview {
  private final RetailAccess r;private final PartnersPort partners;private final RetailDocuments documents;private final FinancePort finance;
  public Overview(RetailAccess r,RetailDocuments documents,FinancePort finance,PartnersPort partners){this.r=r;this.partners=partners;this.documents=documents;this.finance=finance;}
public Object summary(Authentication a){r.require(a,"READ");var t=r.tenant(a);String ds=r.scope(a,"d")+" and "+r.documentTypes(a,"d","VIEW"),bs=r.branchScope(a,"o")+" and "+finance.debtFilter(a,"o");return Map.of("sales",r.db.queryForObject("select coalesce(sum(case when type='SALE_RETURN' then -total else total end),0) from retail.document d where tenant_id=? and "+ds+" and "+r.modules.available(BusinessModules.SALES)+" and status='POSTED' and type in ('POS','ISSUE','SALE_RETURN') and (posted_at at time zone 'Asia/Ho_Chi_Minh')::date=?",BigDecimal.class,t,today()),"pending",r.db.queryForObject("select count(*) from retail.document d where tenant_id=? and "+ds+" and status='PENDING'",Long.class,t),"openOrders",r.db.queryForObject("select count(*) from retail.document d where tenant_id=? and "+ds+" and "+r.modules.available(BusinessModules.SALES)+" and type='SALE_ORDER' and status in ('APPROVED','PARTIAL')",Long.class,t),"receivables",r.db.queryForObject("select coalesce(sum(amount-credit-allocated),0) from retail.open_item o where tenant_id=? and "+bs+" and kind='AR'",BigDecimal.class,t),"payables",r.db.queryForObject("select coalesce(sum(amount-credit-allocated),0) from retail.open_item o where tenant_id=? and "+bs+" and kind='AP'",BigDecimal.class,t),"reminders",r.db.queryForList("select c.*,p.name partner from retail.care c join retail.partner p on p.tenant_id=c.tenant_id and p.id=c.partner_id where c.tenant_id=? and "+r.branchScope(a,"c")+" and "+r.granular(a,"crm","VIEW")+" and not c.done and due_at is not null order by due_at limit 20",t));}
}
