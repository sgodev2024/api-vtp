-- Additional company information is separate from the partner's primary contact address.
alter table retail.partner add column company_name varchar(160) not null default '';
alter table retail.partner add column company_address varchar(2000) not null default '';
