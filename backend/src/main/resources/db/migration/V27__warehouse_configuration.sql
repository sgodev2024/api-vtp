create table retail.warehouse_config (
 id uuid primary key, tenant_id uuid not null references platform.tenant(id),
 kind varchar(30) not null check(kind in ('INVENTORY','UNIT','VARIANT','BRAND','ATTRIBUTE')),
 code varchar(60) not null, name varchar(160) not null, value varchar(1000) not null default '',
 active boolean not null default true, unique(tenant_id,kind,code)
);
alter table retail.warehouse_config enable row level security;
alter table retail.warehouse_config force row level security;
create policy tenant_scope on retail.warehouse_config using (tenant_id = nullif(current_setting('core.tenant_id',true),'')::uuid) with check (tenant_id = nullif(current_setting('core.tenant_id',true),'')::uuid);
do $$ begin if exists(select 1 from pg_roles where rolname='core_app') then grant select,insert,update on retail.warehouse_config to core_app; end if; end $$;
