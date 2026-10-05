alter table retail.warehouse_config drop constraint warehouse_config_kind_check;
alter table retail.warehouse_config add constraint warehouse_config_kind_check check(kind in ('INVENTORY','UNIT','VARIANT','BRAND','ATTRIBUTE','GROUP'));
