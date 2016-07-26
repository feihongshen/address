select DELIVERY_STATION_ID,user_code from deliverers limit 10;

select DELIVERY_STATION_ID from deliverer_rules limit 10;

 -- DMP DB执行 
select * from  where functionname = '小件员关联维护';