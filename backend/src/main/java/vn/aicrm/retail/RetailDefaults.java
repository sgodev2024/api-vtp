package vn.aicrm.retail;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
@Service
public class RetailDefaults {
 private final RetailAccess r;private final TransactionTemplate tx;
 public RetailDefaults(RetailAccess r,TransactionTemplate tx){this.r=r;this.tx=tx;}
 @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
 public void initialize(){for(var row:r.db.queryForList("select id from platform.tenant"))ensure((UUID)row.get("id"));}
 public void ensure(UUID tenant){tx.executeWithoutResult(status->{r.db.queryForList("select set_config('core.tenant_id',?,true)",tenant.toString());r.db.queryForList("select pg_advisory_xact_lock(hashtextextended(?,0))",tenant.toString());var branches=r.db.queryForList("select id from retail.branch where tenant_id=? and active order by code",tenant);UUID branch;if(branches.isEmpty()){branch=UUID.randomUUID();r.db.update("insert into retail.branch(id,tenant_id,code,name) values(?,?,?,?)",branch,tenant,"MAIN-"+branch.toString().substring(0,8),"Trụ sở chính");}else branch=(UUID)branches.getFirst().get("id");if(r.db.queryForObject("select count(*) from retail.warehouse w join retail.branch b on b.tenant_id=w.tenant_id and b.id=w.branch_id where w.tenant_id=? and w.active and b.active",Integer.class,tenant)==0){UUID id=UUID.randomUUID();r.db.update("insert into retail.warehouse(id,tenant_id,branch_id,code,name) values(?,?,?,?,?)",id,tenant,branch,"MAIN-"+id.toString().substring(0,8),"Kho chính");}});}
}
