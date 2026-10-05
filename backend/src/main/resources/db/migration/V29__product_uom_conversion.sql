create table retail.uom(id uuid primary key default gen_random_uuid(),tenant_id uuid not null references platform.tenant(id),name varchar(40) not null,unique(tenant_id,name),unique(tenant_id,id));
insert into retail.uom(tenant_id,name) select tenant_id,unit from retail.sku union select tenant_id,jsonb_object_keys(units) from retail.sku union select tenant_id,name from retail.warehouse_config where kind='UNIT' and length(name)<=40;
alter table retail.sku add column base_uom_id uuid;
update retail.sku s set base_uom_id=u.id from retail.uom u where u.tenant_id=s.tenant_id and u.name=s.unit;
alter table retail.sku add constraint sku_base_uom_fk foreign key(tenant_id,base_uom_id) references retail.uom(tenant_id,id);
alter table retail.sku add constraint sku_tenant_id_unique unique(tenant_id,id);
create table retail.uom_conversion(tenant_id uuid not null,product_id uuid not null,uom_id uuid not null,factor numeric(20,4) not null check(factor>0),primary key(tenant_id,product_id,uom_id),foreign key(tenant_id,product_id) references retail.sku(tenant_id,id),foreign key(tenant_id,uom_id) references retail.uom(tenant_id,id));
insert into retail.uom_conversion select s.tenant_id,s.id,u.id,e.value::numeric from retail.sku s cross join lateral jsonb_each_text(s.units) e join retail.uom u on u.tenant_id=s.tenant_id and u.name=e.key where e.key<>s.unit;
create function retail.sync_product_uom() returns trigger language plpgsql as $$ begin
 insert into retail.uom(tenant_id,name) values(new.tenant_id,new.unit) on conflict do nothing;
 select id into new.base_uom_id from retail.uom where tenant_id=new.tenant_id and name=new.unit;
 return new; end $$;
create trigger sku_base_uom before insert or update of unit on retail.sku for each row execute function retail.sync_product_uom();
create function retail.sync_product_conversions() returns trigger language plpgsql as $$ declare e record; uid uuid; begin
 for e in select * from jsonb_each_text(new.units) loop
 insert into retail.uom(tenant_id,name) values(new.tenant_id,e.key) on conflict do nothing;
 select id into uid from retail.uom where tenant_id=new.tenant_id and name=e.key;
 if uid<>new.base_uom_id then insert into retail.uom_conversion values(new.tenant_id,new.id,uid,e.value::numeric) on conflict(tenant_id,product_id,uom_id) do update set factor=excluded.factor; end if;
 end loop; return new; end $$;
create trigger sku_uom_conversions after insert or update of units on retail.sku for each row execute function retail.sync_product_conversions();
do $$ declare tab text; begin foreach tab in array array['uom','uom_conversion'] loop
 execute format('alter table retail.%I enable row level security',tab);
 execute format('alter table retail.%I force row level security',tab);
 execute format($policy$create policy tenant_scope on retail.%I using (tenant_id=nullif(current_setting('core.tenant_id',true),'')::uuid) with check (tenant_id=nullif(current_setting('core.tenant_id',true),'')::uuid)$policy$,tab);
 end loop;
 if exists(select 1 from pg_roles where rolname='core_app') then grant select,insert,update on retail.uom,retail.uom_conversion to core_app; end if; end $$;
