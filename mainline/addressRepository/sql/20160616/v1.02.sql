-- 小件员表新增字段
ALTER TABLE deliverers add COLUMN DELIVERY_STATION_ID INT(11) not NULL DEFAULT 0 COMMENT '配送站点id',
add COLUMN user_code varchar(32) NOT NULL DEFAULT '' COMMENT '小件员登录名';

-- 小件员规则新增字段
ALTER TABLE deliverer_rules add COLUMN DELIVERY_STATION_ID INT(11) not NULL DEFAULT 0 COMMENT '配送站点id';