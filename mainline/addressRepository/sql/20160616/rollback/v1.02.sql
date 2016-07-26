alter table deliverers drop column DELIVERY_STATION_ID;
alter table deliverers drop column user_code;
alter table deliverer_rules drop column DELIVERY_STATION_ID;


-- DMP DB执行 
update dmp40_function set functionurl='addressdelivertostation/inittree?' ,functionname ='配送员关联维护'  where functionname = '小件员关联维护';