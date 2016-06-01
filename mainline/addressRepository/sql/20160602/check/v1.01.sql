-- 验证脚本
select station_code from delivery_stations limit 10;
select * from system_config where `name` = 'isOpenGisSearch';
-- 此验证脚本如果查询出来的记录为空，需要手工插入一条记录
select * from Scheduled_Tasks where fire_Time < now() and status = 0 and task_type = 'updateIndexTask';

-- 如果上一步为空，需要插入该sql：
insert into `scheduled_tasks` ( `TASK_TYPE`, `STATUS`, `REFERENCE_TYPE`, `FIRE_TIME`, `COMPLETED_TIME`, `CREATED_AT`, `TRY_COUNT`) 
VALUES ( 'updateIndexTask', '0', 'addressId', NOW(), NULL,NOW(), '0');
