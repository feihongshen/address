-- 小件员表新增字段
ALTER TABLE deliverers add COLUMN DELIVERY_STATION_ID INT(11) not NULL DEFAULT 0 COMMENT '配送站点id';
ALTER TABLE deliverers add COLUMN user_code varchar(32) NOT NULL DEFAULT '' COMMENT '小件员登录名';