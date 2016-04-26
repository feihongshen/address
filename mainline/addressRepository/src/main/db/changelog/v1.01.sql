-- delivery_stations新增字段
ALTER TABLE delivery_stations add COLUMN STATION_CODE VARCHAR (32) not null DEFAULT '' COMMENT 'DMP站点编码';
