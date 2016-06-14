-- delivery_stations新增字段
ALTER TABLE delivery_stations add COLUMN STATION_CODE VARCHAR (32) not null DEFAULT '' COMMENT 'DMP站点编码';

-- 增加系统参数控制电子地图的开关,customer_id需要配置当前客户
INSERT INTO system_config (name,value,description,editable,customer_id)
VALUES ('isOpenGisSearch','0','是否开启电子地图','1','6');





