create table retail.crm_lead (
 id uuid primary key, tenant_id uuid not null references platform.tenant(id), branch_id uuid not null,
 code varchar(60) not null, name varchar(160) not null, company varchar(160) not null default '',
 phone varchar(60) not null default '', email varchar(160) not null default '', address varchar(2000) not null default '',
 source varchar(100) not null default '', owner_id uuid not null references identity.account(id),
 status varchar(30) not null default 'NEW' check(status in ('NEW','CONTACTED','QUALIFIED','DISQUALIFIED','CONVERTED')),
 note varchar(4000) not null default '', partner_id uuid, version integer not null default 0,
 created_at timestamptz not null default now(), updated_at timestamptz not null default now(),
 unique(tenant_id,id), unique(tenant_id,code), foreign key(tenant_id,branch_id) references retail.branch(tenant_id,id),
 foreign key(tenant_id,partner_id) references retail.partner(tenant_id,id),
 check ((status='CONVERTED') = (partner_id is not null))
);
create table retail.crm_opportunity (
 id uuid primary key, tenant_id uuid not null references platform.tenant(id), branch_id uuid not null,
 code varchar(60) not null, name varchar(160) not null, lead_id uuid, partner_id uuid,
 owner_id uuid not null references identity.account(id), amount numeric(20,0) not null default 0 check(amount>=0),
 expected_close date, stage varchar(30) not null default 'NEW' check(stage in ('NEW','CONTACTED','QUOTED','NEGOTIATION','WON','LOST')),
 lost_reason varchar(1000) not null default '', note varchar(4000) not null default '', version integer not null default 0,
 created_at timestamptz not null default now(), updated_at timestamptz not null default now(),
 unique(tenant_id,id), unique(tenant_id,code), foreign key(tenant_id,branch_id) references retail.branch(tenant_id,id),
 foreign key(tenant_id,lead_id) references retail.crm_lead(tenant_id,id), foreign key(tenant_id,partner_id) references retail.partner(tenant_id,id),
 check(lead_id is not null or partner_id is not null), check(stage<>'LOST' or length(trim(lost_reason))>0)
);
create index crm_lead_scope on retail.crm_lead(tenant_id,branch_id,status,created_at desc);
create index crm_opportunity_scope on retail.crm_opportunity(tenant_id,branch_id,stage,created_at desc);
alter table retail.crm_lead enable row level security;
alter table retail.crm_lead force row level security;
create policy tenant_scope on retail.crm_lead using(tenant_id=nullif(current_setting('core.tenant_id',true),'')::uuid) with check(tenant_id=nullif(current_setting('core.tenant_id',true),'')::uuid);
alter table retail.crm_opportunity enable row level security;
alter table retail.crm_opportunity force row level security;
create policy tenant_scope on retail.crm_opportunity using(tenant_id=nullif(current_setting('core.tenant_id',true),'')::uuid) with check(tenant_id=nullif(current_setting('core.tenant_id',true),'')::uuid);
grant select,insert,update,delete on retail.crm_lead,retail.crm_opportunity to core_app;
