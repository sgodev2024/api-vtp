alter table retail.partner add column roles jsonb not null default '{}';
update retail.partner set roles=jsonb_build_object(kind,true);
alter table retail.partner add constraint partner_roles_valid check (
 jsonb_typeof(roles)='object' and roles <> '{}'::jsonb
 and roles - array['CUSTOMER','SUPPLIER','COLLABORATOR'] = '{}'::jsonb
 and (not roles ? 'CUSTOMER' or jsonb_typeof(roles->'CUSTOMER')='boolean')
 and (not roles ? 'SUPPLIER' or jsonb_typeof(roles->'SUPPLIER')='boolean')
 and (not roles ? 'COLLABORATOR' or jsonb_typeof(roles->'COLLABORATOR')='boolean'));
alter table retail.partner add column entity_type varchar(15) not null default 'PERSON' check(entity_type in ('PERSON','BUSINESS'));
alter table retail.partner add column tax_code varchar(30) not null default '';
alter table retail.partner add column contact_name varchar(160) not null default '';
alter table retail.partner add column contact_title varchar(80) not null default '';
alter table retail.partner add column contact_phone varchar(60) not null default '';
alter table retail.partner add column shipping_address text not null default '';
alter table retail.partner add column sale_terms varchar(500) not null default '';
alter table retail.partner add column purchase_terms varchar(500) not null default '';
alter table retail.partner add column sale_days integer check(sale_days between 0 and 3650);
alter table retail.partner add column purchase_days integer check(purchase_days between 0 and 3650);
alter table retail.partner add column bank_name varchar(160) not null default '';
alter table retail.partner add column bank_account varchar(80) not null default '';
alter table retail.partner add column bank_holder varchar(160) not null default '';
alter table retail.partner add column owner_id uuid references identity.account(id);
alter table retail.partner add column version integer not null default 0;
alter table retail.partner add column created_at timestamptz not null default now();
alter table retail.partner add column updated_at timestamptz not null default now();
alter table retail.partner add column created_by uuid references identity.account(id);
alter table retail.partner add column updated_by uuid references identity.account(id);
create unique index partner_tax_unique on retail.partner(tenant_id,tax_code) where tax_code <> '';
-- Existing documents retain the available master information at migration time.
update retail.document d set metadata=jsonb_set(d.metadata,'{partnerSnapshot}',jsonb_build_object('id',p.id,'code',p.code,'name',p.name,'phone',p.phone,'email',p.email,'address',p.address,'taxCode',p.tax_code))
from retail.partner p where p.tenant_id=d.tenant_id and p.id=d.partner_id;
