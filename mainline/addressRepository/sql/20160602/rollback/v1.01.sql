-- 回滚脚本
alter table delivery_stations drop column STATION_CODE;
delete from system_config where `name` = 'isOpenGisSearch';