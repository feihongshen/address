-- 验证脚本
select station_code from delivery_stations limit 10;
select * from system_config where `name` = 'isOpenGisSearch';