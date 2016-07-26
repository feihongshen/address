-- 小件员表新增字段
ALTER TABLE deliverers add COLUMN DELIVERY_STATION_ID INT(11) not NULL DEFAULT 0 COMMENT '配送站点id',
add COLUMN user_code varchar(32) NOT NULL DEFAULT '' COMMENT '小件员登录名';

-- 小件员规则新增字段
ALTER TABLE deliverer_rules add COLUMN DELIVERY_STATION_ID INT(11) not NULL DEFAULT 0 COMMENT '配送站点id';


-- 更新配送员维护菜单为小件员关联维护
update dmp40_function set functionurl='${addrUrl}/delivererStationRule/delivererStationRule?${addrUser}${addrPsw}' ,functionname ='小件员关联维护'  where functionname = '配送员关联维护';