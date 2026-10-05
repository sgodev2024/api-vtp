create table retail.commission(
 id uuid primary key default gen_random_uuid(),tenant_id uuid not null,branch_id uuid not null,
 partner_id uuid not null,document_id uuid,kind varchar(30) not null check(kind in ('ORDER','MONTHLY_ADDITION','ADJUSTMENT')),
 period date not null check(extract(day from period)=1),amount numeric(20,0) not null check(amount<>0),
 reason text not null,creator_id uuid not null,created_at timestamptz not null default now(),
 check(kind='ADJUSTMENT' or amount>0),
 foreign key(tenant_id,branch_id) references retail.branch(tenant_id,id),
 foreign key(tenant_id,partner_id) references retail.partner(tenant_id,id),
 foreign key(tenant_id,document_id) references retail.document(tenant_id,id));
create unique index commission_order_once on retail.commission(tenant_id,partner_id,document_id) where kind='ORDER';
create index on retail.commission(tenant_id,branch_id,partner_id,period);
alter table retail.payment add column commission_period date check(commission_period is null or extract(day from commission_period)=1);
alter table retail.commission enable row level security;
alter table retail.commission force row level security;
create policy tenant_isolation on retail.commission using(tenant_id=nullif(current_setting('core.tenant_id',true),'')::uuid) with check(tenant_id=nullif(current_setting('core.tenant_id',true),'')::uuid);
grant select,insert on retail.commission to core_app;
