/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50538
Source Host           : localhost:3306
Source Database       : address_init

Target Server Type    : MYSQL
Target Server Version : 50538
File Encoding         : 65001

Date: 2016-08-22 15:55:28
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `ID` int(12) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL,
  `OLD_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `STATUS` int(2) NOT NULL,
  `ADDRESS_TYPE_ID` int(2) DEFAULT NULL,
  `ADDRESS_LEVEL` int(2) DEFAULT NULL,
  `PARENT_ID` int(12) DEFAULT NULL,
  `PATH` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `INDEXED` int(1) DEFAULT NULL,
  `CREATION_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_ADDRESS_NAME` (`NAME`) USING BTREE,
  KEY `IDX_ADDRESS_PARENT_ID` (`PARENT_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3000000 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES ('1', '中国', null, '1', null, '0', '-1', '', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('2', '北京市', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('21', '天津市', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('40', '河北省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('235', '山西省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('377', '内蒙古自治区', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('501', '辽宁省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('630', '吉林省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('708', '黑龙江省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('862', '上海市', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('882', '江苏省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('1009', '浙江省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('1122', '安徽省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('1260', '福建省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('1364', '江西省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('1487', '山东省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('1660', '河南省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('1855', '湖北省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('1985', '湖南省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('2135', '广东省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('2297', '广西壮族自治区', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('2436', '海南省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('2466', '重庆市', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('2507', '四川省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('2730', '贵州省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('2833', '云南省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('2987', '西藏自治区', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('3070', '陕西省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('3198', '甘肃省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('3311', '青海省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('3364', '宁夏回族自治区', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('3397', '新疆维吾尔自治区', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('3514', '台湾省', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');
INSERT INTO `address` VALUES ('3515', '香港特别行政区', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:30');
INSERT INTO `address` VALUES ('3516', '澳门特别行政区', null, '1', null, '1', '1', '1', null, '2016-07-18 14:44:31');

-- ----------------------------
-- Table structure for address_detail
-- ----------------------------
DROP TABLE IF EXISTS `address_detail`;
CREATE TABLE `address_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `province` varchar(255) DEFAULT NULL COMMENT '省名称',
  `city` varchar(255) DEFAULT NULL COMMENT '市名称',
  `district` varchar(255) DEFAULT NULL COMMENT '区名称',
  `address_id1` int(20) DEFAULT NULL COMMENT '关键词1的名称',
  `address_name1` varchar(255) DEFAULT NULL COMMENT '关键词1的名称',
  `address_id2` int(20) DEFAULT NULL COMMENT '关键词2的id',
  `address_name2` varchar(255) DEFAULT NULL COMMENT '关键词2的名称',
  `address_id3` int(255) DEFAULT NULL COMMENT '关键词3的id',
  `address_name3` varchar(255) DEFAULT NULL COMMENT '关键词3的名称',
  `delivery_station_name` varchar(255) DEFAULT NULL COMMENT '配送站点名称',
  `customer_id` int(11) DEFAULT NULL COMMENT '所对应的用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地址详细信息表（用于拆分结果的展现）';

-- ----------------------------
-- Records of address_detail
-- ----------------------------

-- ----------------------------
-- Table structure for address_import_details
-- ----------------------------
DROP TABLE IF EXISTS `address_import_details`;
CREATE TABLE `address_import_details` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `RESULT_ID` int(12) DEFAULT NULL COMMENT '导入结果id',
  `ADDRESS_ID` int(12) DEFAULT NULL COMMENT '关键词id',
  `STATUS` int(2) DEFAULT NULL COMMENT '状态',
  `MESSAGE` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '导入结果信息',
  `PROVINCE` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '导入的省',
  `CITY` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '导入的市',
  `DISTRICT` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '导入的区',
  `ADDRESS1` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '关键词1',
  `ADDRESS2` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '关键词2',
  `ADDRESS3` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '关键词3',
  `DELIVERY_STATION_ID` int(12) DEFAULT NULL COMMENT '配送站点id',
  `DELIVERY_STATION_NAME` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '配送站点名称',
  `DELIVERER_ID` int(12) DEFAULT NULL COMMENT '配送员id',
  `DELIVERER_NAME` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '配送员名称',
  PRIMARY KEY (`ID`),
  KEY `IDX_ADDRESS_IMPORT_DETAILS_RESULT_ID` (`RESULT_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='地址导入结果明细表';

-- ----------------------------
-- Records of address_import_details
-- ----------------------------

-- ----------------------------
-- Table structure for address_import_results
-- ----------------------------
DROP TABLE IF EXISTS `address_import_results`;
CREATE TABLE `address_import_results` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `USER_ID` int(12) DEFAULT NULL COMMENT '用户id',
  `SUCCESS_COUNT` int(12) DEFAULT NULL COMMENT '成功个数',
  `FAILURE_COUNT` int(12) DEFAULT NULL COMMENT '失败个数',
  `IMPORT_DATE` datetime DEFAULT NULL COMMENT '导入时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_ADDRESS_IMPORT_RESULTS_USER` (`USER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='地址导入结果表';

-- ----------------------------
-- Records of address_import_results
-- ----------------------------

-- ----------------------------
-- Table structure for address_order
-- ----------------------------
DROP TABLE IF EXISTS `address_order`;
CREATE TABLE `address_order` (
  `ID` int(12) NOT NULL AUTO_INCREMENT,
  `EXTERNAL_ORDER_ID` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT 'DMP订单ID',
  `CUSTOMER_ID` int(12) NOT NULL COMMENT '客户ID',
  `ADDRESS_LINE` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '地址串',
  `DELIVERY_STATION_IDS` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '配送站点ID',
  `DELIVERER_IDS` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '配送员ID',
  `CREATION_DATE` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='地址订单关联';

-- ----------------------------
-- Records of address_order
-- ----------------------------

-- ----------------------------
-- Table structure for address_permissions
-- ----------------------------
DROP TABLE IF EXISTS `address_permissions`;
CREATE TABLE `address_permissions` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ADDRESS_ID` int(12) NOT NULL COMMENT '关键词id',
  `CUSTOMER_ID` int(12) DEFAULT NULL COMMENT '客户id',
  PRIMARY KEY (`ID`),
  KEY `IDX_ADDRESS_PERMISSION_ADDRESS_ID` (`ADDRESS_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='地址权限表';

-- ----------------------------
-- Records of address_permissions
-- ----------------------------
INSERT INTO `address_permissions` VALUES ('31', '1', '2');
INSERT INTO `address_permissions` VALUES ('32', '1', '4');
INSERT INTO `address_permissions` VALUES ('33', '1', '5');
INSERT INTO `address_permissions` VALUES ('34', '1', '6');
INSERT INTO `address_permissions` VALUES ('35', '1', '7');
INSERT INTO `address_permissions` VALUES ('36', '1', '8');
INSERT INTO `address_permissions` VALUES ('37', '1', '9');
INSERT INTO `address_permissions` VALUES ('38', '1', '10');
INSERT INTO `address_permissions` VALUES ('39', '1', '12');
INSERT INTO `address_permissions` VALUES ('40', '1', '13');
INSERT INTO `address_permissions` VALUES ('41', '1', '14');
INSERT INTO `address_permissions` VALUES ('42', '1', '15');
INSERT INTO `address_permissions` VALUES ('43', '1', '18');
INSERT INTO `address_permissions` VALUES ('44', '1', '19');
INSERT INTO `address_permissions` VALUES ('45', '1', '20');
INSERT INTO `address_permissions` VALUES ('46', '1', '26');
INSERT INTO `address_permissions` VALUES ('47', '1', '27');
INSERT INTO `address_permissions` VALUES ('48', '1', '28');
INSERT INTO `address_permissions` VALUES ('49', '1', '31');
INSERT INTO `address_permissions` VALUES ('50', '1', '32');
INSERT INTO `address_permissions` VALUES ('51', '1', '33');
INSERT INTO `address_permissions` VALUES ('52', '1', '37');
INSERT INTO `address_permissions` VALUES ('53', '1', '38');
INSERT INTO `address_permissions` VALUES ('54', '1', '39');
INSERT INTO `address_permissions` VALUES ('55', '1', '41');
INSERT INTO `address_permissions` VALUES ('56', '1', '42');
INSERT INTO `address_permissions` VALUES ('57', '1', '43');
INSERT INTO `address_permissions` VALUES ('58', '1', '44');
INSERT INTO `address_permissions` VALUES ('59', '1', '45');
INSERT INTO `address_permissions` VALUES ('60', '1', '47');

-- ----------------------------
-- Table structure for address_station_relation
-- ----------------------------
DROP TABLE IF EXISTS `address_station_relation`;
CREATE TABLE `address_station_relation` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ADDRESS_ID` int(12) NOT NULL COMMENT '关键词id',
  `DELIVERY_STATION_ID` int(12) NOT NULL COMMENT '配送站点id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='配送站点与地址关系表';

-- ----------------------------
-- Records of address_station_relation
-- ----------------------------

-- ----------------------------
-- Table structure for alias
-- ----------------------------
DROP TABLE IF EXISTS `alias`;
CREATE TABLE `alias` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ADDRESS_ID` int(12) NOT NULL COMMENT '关键词id',
  `CUSTOMER_ID` int(12) DEFAULT NULL COMMENT '客户id',
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '别名',
  `OLD_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '前别名',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='地址别名表';

-- ----------------------------
-- Records of alias
-- ----------------------------

-- ----------------------------
-- Table structure for biz_log
-- ----------------------------
DROP TABLE IF EXISTS `biz_log`;
CREATE TABLE `biz_log` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CUSTOMER_ID` int(11) NOT NULL COMMENT '客户ID',
  `OPERATION_TYPE` int(11) NOT NULL COMMENT '操作类型',
  `ADDRESS_ID` int(11) DEFAULT NULL COMMENT '关键词ID',
  `ADDRESS_NAME` varchar(255) DEFAULT NULL COMMENT '关键词名称',
  `ALIAS_ID` int(11) DEFAULT NULL COMMENT '别名ID',
  `ALIAS_NAME` varchar(255) DEFAULT NULL COMMENT '别名名称',
  `DELIVERY_STATION_RULE_ID` int(11) DEFAULT NULL COMMENT '规则ID',
  `RULE_EXPRESSION` varchar(1000) DEFAULT NULL COMMENT '规则表达式',
  `ORIGIN_STATION_ID` int(11) DEFAULT NULL COMMENT '原站点ID',
  `ORIGIN_STATION_NAME` varchar(255) DEFAULT NULL COMMENT '原站点名称',
  `MODIFIED_STATION_ID` int(11) DEFAULT NULL COMMENT '修改后站点ID',
  `MODIFIED_STATION_NAME` varchar(255) DEFAULT NULL COMMENT '修改后站点名称',
  `SOURCE_STATION_ID` int(11) DEFAULT NULL COMMENT '源站点ID',
  `SOURCE_STATION_NAME` varchar(255) DEFAULT NULL COMMENT '源站点名称',
  `DEST_STATION_ID` int(11) DEFAULT NULL COMMENT '目的站点ID',
  `DEST_STATION_NAME` varchar(255) DEFAULT NULL COMMENT '目的站点名称',
  `SPLIT_COMBINE_ADDRESS` text COMMENT '拆合关键词ID列表',
  `OPERATOR_IP` varchar(255) DEFAULT NULL COMMENT '操作人IP',
  `OPERATION_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_OPERATION_TYPE` (`OPERATION_TYPE`) USING BTREE,
  KEY `IDX_OPERATOR_IP` (`OPERATOR_IP`) USING BTREE,
  KEY `IDX_OPERATION_TIME` (`OPERATION_TIME`) USING BTREE,
  KEY `IDX_CUSTOMER_ID` (`CUSTOMER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务日志';

-- ----------------------------
-- Records of biz_log
-- ----------------------------

-- ----------------------------
-- Table structure for biz_log1
-- ----------------------------
DROP TABLE IF EXISTS `biz_log1`;
CREATE TABLE `biz_log1` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CUSTOMER_ID` int(11) NOT NULL COMMENT '客户ID',
  `OPERATION_TYPE` int(11) NOT NULL COMMENT '操作类型',
  `ADDRESS_ID` int(11) DEFAULT NULL COMMENT '关键词ID',
  `ADDRESS_NAME` varchar(255) DEFAULT NULL COMMENT '关键词名称',
  `ALIAS_ID` int(11) DEFAULT NULL COMMENT '别名ID',
  `ALIAS_NAME` varchar(255) DEFAULT NULL COMMENT '别名名称',
  `DELIVERY_STATION_RULE_ID` int(11) DEFAULT NULL COMMENT '规则ID',
  `RULE_EXPRESSION` varchar(1000) DEFAULT NULL COMMENT '规则表达式',
  `ORIGIN_STATION_ID` int(11) DEFAULT NULL COMMENT '原站点ID',
  `ORIGIN_STATION_NAME` varchar(255) DEFAULT NULL COMMENT '原站点名称',
  `MODIFIED_STATION_ID` int(11) DEFAULT NULL COMMENT '修改后站点ID',
  `MODIFIED_STATION_NAME` varchar(255) DEFAULT NULL COMMENT '修改后站点名称',
  `SOURCE_STATION_ID` int(11) DEFAULT NULL COMMENT '源站点ID',
  `SOURCE_STATION_NAME` varchar(255) DEFAULT NULL COMMENT '源站点名称',
  `DEST_STATION_ID` int(11) DEFAULT NULL COMMENT '目的站点ID',
  `DEST_STATION_NAME` varchar(255) DEFAULT NULL COMMENT '目的站点名称',
  `SPLIT_COMBINE_ADDRESS` text COMMENT '拆合关键词ID列表',
  `OPERATOR_IP` varchar(255) DEFAULT NULL COMMENT '操作人IP',
  `OPERATION_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_OPERATION_TYPE` (`OPERATION_TYPE`) USING BTREE,
  KEY `IDX_OPERATOR_IP` (`OPERATOR_IP`) USING BTREE,
  KEY `IDX_OPERATION_TIME` (`OPERATION_TIME`) USING BTREE,
  KEY `IDX_CUSTOMER_ID` (`CUSTOMER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务日志';

-- ----------------------------
-- Records of biz_log1
-- ----------------------------

-- ----------------------------
-- Table structure for client_applications
-- ----------------------------
DROP TABLE IF EXISTS `client_applications`;
CREATE TABLE `client_applications` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NAME` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '登录名',
  `PASSWORD` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '登陆密码',
  `CUSTOMER_ID` int(12) NOT NULL COMMENT '客户id',
  `STATUS` int(2) DEFAULT NULL COMMENT '状态标识（是否启用）',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='客户端应用表';

-- ----------------------------
-- Records of client_applications
-- ----------------------------

-- ----------------------------
-- Table structure for customers
-- ----------------------------
DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NAME` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '客户名称',
  `STATUS` int(2) DEFAULT NULL COMMENT '状态标识（是否启用）',
  `DESCRIPTION` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='客户信息表';

-- ----------------------------
-- Records of customers
-- ----------------------------
INSERT INTO `customers` VALUES ('2', '贵州\r\n品信', '0', null);
INSERT INTO `customers` VALUES ('4', '广西\r\n品信', '0', null);
INSERT INTO `customers` VALUES ('5', '重庆华宇', '0', null);
INSERT INTO `customers` VALUES ('6', '广州通路', '0', '');
INSERT INTO `customers` VALUES ('7', '宁夏\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('8', '海南\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('9', '福建飞远', '0', null);
INSERT INTO `customers` VALUES ('10', '安徽\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('12', '江西\r\n飞远', '0', null);
INSERT INTO `customers` VALUES ('13', '上海\r\n品俊', '0', '');
INSERT INTO `customers` VALUES ('14', '吉林\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('15', '黑龙江\r\n品俊', '0', '');
INSERT INTO `customers` VALUES ('18', '青海\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('19', '甘肃\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('20', '沈阳品俊', '0', null);
INSERT INTO `customers` VALUES ('26', '西藏\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('27', '湖南\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('28', '江苏品俊', '0', null);
INSERT INTO `customers` VALUES ('31', '四川\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('32', '新疆\r\n品信', '0', null);
INSERT INTO `customers` VALUES ('33', '云南\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('37', '山西\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('38', '浙江\r\n飞远', '0', null);
INSERT INTO `customers` VALUES ('39', '武汉\r\n飞远', '0', null);
INSERT INTO `customers` VALUES ('41', '河南\r\n豫铁', '0', null);
INSERT INTO `customers` VALUES ('42', '山东\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('43', '河北\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('44', '天津\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('45', '北京\r\n品俊', '0', null);
INSERT INTO `customers` VALUES ('47', '陕西城联', '0', null);

-- ----------------------------
-- Table structure for databasechangelog
-- ----------------------------
DROP TABLE IF EXISTS `databasechangelog`;
CREATE TABLE `databasechangelog` (
  `ID` varchar(63) NOT NULL,
  `AUTHOR` varchar(63) NOT NULL,
  `FILENAME` varchar(200) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`,`AUTHOR`,`FILENAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of databasechangelog
-- ----------------------------

-- ----------------------------
-- Table structure for databasechangeloglock
-- ----------------------------
DROP TABLE IF EXISTS `databasechangeloglock`;
CREATE TABLE `databasechangeloglock` (
  `ID` int(11) NOT NULL,
  `LOCKED` tinyint(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of databasechangeloglock
-- ----------------------------

-- ----------------------------
-- Table structure for deliverers
-- ----------------------------
DROP TABLE IF EXISTS `deliverers`;
CREATE TABLE `deliverers` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '配送员名称',
  `EXTERNAL_ID` int(12) NOT NULL COMMENT 'DMP中配送员ID',
  `CUSTOMER_ID` int(12) NOT NULL COMMENT '客户id',
  `STATUS` int(2) NOT NULL COMMENT '状态标识（是否启用）',
  `CREATION_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `DELIVERY_STATION_ID` int(11) NOT NULL DEFAULT '0' COMMENT '配送站点id',
  `user_code` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '小件员登录名',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDX_DELIVERER_CUSTOMER` (`EXTERNAL_ID`,`CUSTOMER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='配送员信息表';

-- ----------------------------
-- Records of deliverers
-- ----------------------------

-- ----------------------------
-- Table structure for deliverer_rules
-- ----------------------------
DROP TABLE IF EXISTS `deliverer_rules`;
CREATE TABLE `deliverer_rules` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `RULE` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '规则',
  `RULE_EXPRESSION` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '规则表达式',
  `RULE_TYPE` int(2) NOT NULL COMMENT '规则类型',
  `ADDRESS_ID` int(12) NOT NULL COMMENT '关键词id',
  `DELIVERER_ID` int(12) NOT NULL COMMENT '配送员id',
  `CREATION_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `DELIVERY_STATION_ID` int(11) NOT NULL DEFAULT '0' COMMENT '配送站点id',
  PRIMARY KEY (`ID`),
  KEY `IDX_DELIVERER_RULE_ADDRESS_ID` (`ADDRESS_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='配送员规则表';

-- ----------------------------
-- Records of deliverer_rules
-- ----------------------------

-- ----------------------------
-- Table structure for delivery_stations
-- ----------------------------
DROP TABLE IF EXISTS `delivery_stations`;
CREATE TABLE `delivery_stations` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NAME` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '站点名称',
  `COORDINATE` varchar(20000) COLLATE utf8_bin DEFAULT NULL COMMENT '区域坐标',
  `MAPCENTER_LNG` decimal(50,8) DEFAULT NULL COMMENT '地图中心纬度',
  `MAPCENTER_LAT` decimal(50,8) DEFAULT NULL COMMENT '地图中心经度',
  `UID` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '地图唯一标识',
  `EXTERNAL_ID` int(12) DEFAULT NULL,
  `CUSTOMER_ID` int(12) NOT NULL COMMENT '客户id',
  `STATUS` int(2) NOT NULL COMMENT '状态标识（是否启用）',
  `CREATION_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `STATION_CODE` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'DMP站点编码',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDX_DELIVERY_STATION_EXTERNAL_ID` (`EXTERNAL_ID`,`CUSTOMER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='配送站点表';

-- ----------------------------
-- Records of delivery_stations
-- ----------------------------

-- ----------------------------
-- Table structure for delivery_station_rules
-- ----------------------------
DROP TABLE IF EXISTS `delivery_station_rules`;
CREATE TABLE `delivery_station_rules` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `RULE` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '规则',
  `RULE_EXPRESSION` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '规则表达式',
  `RULE_TYPE` int(2) NOT NULL COMMENT '规则类型',
  `ADDRESS_ID` int(12) NOT NULL COMMENT '关键词id',
  `DELIVERY_STATION_ID` int(12) NOT NULL COMMENT '配送站点id',
  `CREATION_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_DS_RULE_ADDRESS_ID` (`ADDRESS_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='站点规则表';

-- ----------------------------
-- Records of delivery_station_rules
-- ----------------------------

-- ----------------------------
-- Table structure for keyword_suffix
-- ----------------------------
DROP TABLE IF EXISTS `keyword_suffix`;
CREATE TABLE `keyword_suffix` (
  `ID` int(12) NOT NULL AUTO_INCREMENT,
  `CUSTOMER_ID` int(12) DEFAULT NULL COMMENT '客户ID',
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '名称',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='关键词后缀（用于自动拆分地址）';

-- ----------------------------
-- Records of keyword_suffix
-- ----------------------------

-- ----------------------------
-- Table structure for old_id
-- ----------------------------
DROP TABLE IF EXISTS `old_id`;
CREATE TABLE `old_id` (
  `ID` int(12) NOT NULL AUTO_INCREMENT,
  `type` int(1) DEFAULT '1',
  `tab` varchar(50) DEFAULT NULL,
  `old_id` int(12) DEFAULT NULL,
  `customer_id` int(12) DEFAULT NULL,
  `new_id` int(12) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `zhuhe` (`type`,`tab`,`old_id`,`customer_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of old_id
-- ----------------------------

-- ----------------------------
-- Table structure for qtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qtz_blob_triggers`;
CREATE TABLE `qtz_blob_triggers` (
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `blob_data` longblob,
  PRIMARY KEY (`trigger_name`,`trigger_group`),
  UNIQUE KEY `SYS_C00100332` (`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `qtz_blob_triggers_ibfk_1` FOREIGN KEY (`trigger_name`, `trigger_group`) REFERENCES `qtz_triggers` (`trigger_name`, `trigger_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qtz_calendars`;
CREATE TABLE `qtz_calendars` (
  `calendar_name` varchar(200) NOT NULL,
  `calendar` longblob NOT NULL,
  PRIMARY KEY (`calendar_name`),
  UNIQUE KEY `SYS_C00100341` (`calendar_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qtz_cron_triggers`;
CREATE TABLE `qtz_cron_triggers` (
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `cron_expression` varchar(120) NOT NULL,
  `time_zone_id` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`trigger_name`,`trigger_group`),
  CONSTRAINT `qtz_cron_triggers_ibfk_1` FOREIGN KEY (`trigger_name`, `trigger_group`) REFERENCES `qtz_triggers` (`trigger_name`, `trigger_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_cron_triggers
-- ----------------------------
INSERT INTO `qtz_cron_triggers` VALUES ('updateIndexTimer', 'DEFAULT', '0 */10 * * * ?', 'Asia/Shanghai');

-- ----------------------------
-- Table structure for qtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qtz_fired_triggers`;
CREATE TABLE `qtz_fired_triggers` (
  `entry_id` varchar(95) NOT NULL,
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `is_volatile` varchar(1) NOT NULL,
  `instance_name` varchar(200) NOT NULL,
  `fired_time` bigint(13) NOT NULL,
  `priority` bigint(13) NOT NULL,
  `state` varchar(16) NOT NULL,
  `job_name` varchar(200) DEFAULT NULL,
  `job_group` varchar(200) DEFAULT NULL,
  `is_stateful` varchar(1) DEFAULT NULL,
  `requests_recovery` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`entry_id`),
  UNIQUE KEY `SYS_C00100352` (`entry_id`) USING BTREE,
  KEY `IDX_QTZ_FT_JOB_REQ_RECOVERY` (`requests_recovery`) USING BTREE,
  KEY `IDX_QTZ_FT_JOB_STATEFUL` (`is_stateful`) USING BTREE,
  KEY `IDX_QTZ_FT_JOB_GROUP` (`job_group`) USING BTREE,
  KEY `IDX_QTZ_FT_JOB_NAME` (`job_name`) USING BTREE,
  KEY `IDX_QTZ_FT_TRIG_INST_NAME` (`instance_name`) USING BTREE,
  KEY `IDX_QTZ_FT_TRIG_VOLATILE` (`is_volatile`) USING BTREE,
  KEY `IDX_QTZ_FT_TRIG_NM_GP` (`trigger_name`,`trigger_group`) USING BTREE,
  KEY `IDX_QTZ_FT_TRIG_GROUP` (`trigger_group`) USING BTREE,
  KEY `IDX_QTZ_FT_TRIG_NAME` (`trigger_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qtz_job_details`;
CREATE TABLE `qtz_job_details` (
  `job_name` varchar(200) NOT NULL,
  `job_group` varchar(200) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `job_class_name` varchar(250) NOT NULL,
  `is_durable` varchar(1) NOT NULL,
  `is_volatile` varchar(1) NOT NULL,
  `is_stateful` varchar(1) NOT NULL,
  `requests_recovery` varchar(1) NOT NULL,
  `job_data` longblob,
  PRIMARY KEY (`job_name`,`job_group`),
  UNIQUE KEY `SYS_C00100302` (`job_name`,`job_group`) USING BTREE,
  KEY `IDX_QTZ_J_REQ_RECOVERY` (`requests_recovery`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_job_details
-- ----------------------------
INSERT INTO `qtz_job_details` VALUES ('updateIndexTask', 'DEFAULT', null, 'cn.explink.util.BeanInvokingJobDetailFactoryBean$BeanInvokingJob', '0', '0', '0', '1', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000003740009617267756D656E74737074000A7461726765744265616E7400147363686564756C65645461736B5365727669636574000C7461726765744D6574686F6474001770726F63657373557064617465496E6465785461736B737800);

-- ----------------------------
-- Table structure for qtz_job_listeners
-- ----------------------------
DROP TABLE IF EXISTS `qtz_job_listeners`;
CREATE TABLE `qtz_job_listeners` (
  `job_name` varchar(200) NOT NULL,
  `job_group` varchar(200) NOT NULL,
  `job_listener` varchar(200) NOT NULL,
  PRIMARY KEY (`job_name`,`job_group`,`job_listener`),
  UNIQUE KEY `SYS_C00100306` (`job_name`,`job_group`,`job_listener`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_job_listeners
-- ----------------------------

-- ----------------------------
-- Table structure for qtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qtz_locks`;
CREATE TABLE `qtz_locks` (
  `lock_name` varchar(40) NOT NULL,
  PRIMARY KEY (`lock_name`),
  UNIQUE KEY `SYS_C00100358` (`lock_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_locks
-- ----------------------------
INSERT INTO `qtz_locks` VALUES ('STATE_ACCESS\r\n\r\n\r\n');
INSERT INTO `qtz_locks` VALUES ('TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for qtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qtz_paused_trigger_grps`;
CREATE TABLE `qtz_paused_trigger_grps` (
  `trigger_group` varchar(200) NOT NULL,
  PRIMARY KEY (`trigger_group`),
  UNIQUE KEY `SYS_C00100343` (`trigger_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qtz_scheduler_state`;
CREATE TABLE `qtz_scheduler_state` (
  `instance_name` varchar(200) NOT NULL,
  `last_checkin_time` bigint(13) NOT NULL,
  `checkin_interval` bigint(13) NOT NULL,
  PRIMARY KEY (`instance_name`),
  UNIQUE KEY `SYS_C00100356` (`instance_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qtz_simple_triggers`;
CREATE TABLE `qtz_simple_triggers` (
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `repeat_count` int(7) NOT NULL,
  `repeat_interval` bigint(12) NOT NULL,
  `times_triggered` bigint(10) NOT NULL,
  PRIMARY KEY (`trigger_name`,`trigger_group`),
  CONSTRAINT `qtz_simple_triggers_ibfk_1` FOREIGN KEY (`trigger_name`, `trigger_group`) REFERENCES `qtz_triggers` (`trigger_name`, `trigger_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qtz_triggers`;
CREATE TABLE `qtz_triggers` (
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `job_name` varchar(200) NOT NULL,
  `job_group` varchar(200) NOT NULL,
  `is_volatile` varchar(1) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `next_fire_time` bigint(13) DEFAULT NULL,
  `prev_fire_time` bigint(13) DEFAULT NULL,
  `priority` bigint(13) DEFAULT NULL,
  `trigger_state` varchar(16) NOT NULL,
  `trigger_type` varchar(8) NOT NULL,
  `start_time` bigint(13) NOT NULL,
  `end_time` bigint(13) DEFAULT NULL,
  `calendar_name` varchar(200) DEFAULT NULL,
  `misfire_instr` int(2) DEFAULT NULL,
  `job_data` longblob,
  PRIMARY KEY (`trigger_name`,`trigger_group`),
  KEY `sys_c00100317` (`job_name`,`job_group`) USING BTREE,
  KEY `IDX_QTZ_T_VOLATILE` (`is_volatile`) USING BTREE,
  KEY `IDX_QTZ_T_NFT_ST` (`next_fire_time`,`trigger_state`) USING BTREE,
  KEY `IDX_QTZ_T_STATE` (`trigger_state`) USING BTREE,
  KEY `IDX_QTZ_T_NEXT_FIRE_TIME` (`next_fire_time`) USING BTREE,
  CONSTRAINT `qtz_triggers_ibfk_1` FOREIGN KEY (`job_name`, `job_group`) REFERENCES `qtz_job_details` (`job_name`, `job_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_triggers
-- ----------------------------
INSERT INTO `qtz_triggers` VALUES ('updateIndexTimer', 'DEFAULT', 'updateIndexTask', 'DEFAULT', '0', null, '1471401600000', '1471401000000', '5', 'WAITING', 'CRON', '1471400076000', '0', null, '0', '');

-- ----------------------------
-- Table structure for qtz_trigger_listeners
-- ----------------------------
DROP TABLE IF EXISTS `qtz_trigger_listeners`;
CREATE TABLE `qtz_trigger_listeners` (
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `trigger_listener` varchar(200) NOT NULL,
  PRIMARY KEY (`trigger_name`,`trigger_group`,`trigger_listener`),
  CONSTRAINT `qtz_trigger_listeners_ibfk_1` FOREIGN KEY (`trigger_name`, `trigger_group`) REFERENCES `qtz_triggers` (`trigger_name`, `trigger_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qtz_trigger_listeners
-- ----------------------------

-- ----------------------------
-- Table structure for raw_address
-- ----------------------------
DROP TABLE IF EXISTS `raw_address`;
CREATE TABLE `raw_address` (
  `ID` int(12) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '名称',
  `OLD_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '原名称',
  `STATUS` int(2) NOT NULL COMMENT '状态',
  `ADDRESS_TYPE_ID` int(2) DEFAULT NULL COMMENT '地址类型ID',
  `ADDRESS_LEVEL` int(2) DEFAULT NULL COMMENT '层级',
  `PARENT_ID` int(12) DEFAULT NULL COMMENT '父级地址ID',
  `PATH` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '路径',
  `INDEXED` int(1) DEFAULT NULL COMMENT '是否被索引',
  `CREATION_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_ADDRESS_NAME` (`NAME`) USING BTREE,
  KEY `IDX_ADDRESS_PARENT_ID` (`PARENT_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='粗地址表（用于存储自动拆分后的地址信息）';

-- ----------------------------
-- Records of raw_address
-- ----------------------------

-- ----------------------------
-- Table structure for raw_address_permissions
-- ----------------------------
DROP TABLE IF EXISTS `raw_address_permissions`;
CREATE TABLE `raw_address_permissions` (
  `ID` int(12) NOT NULL AUTO_INCREMENT,
  `RAW_ADDRESS_ID` int(12) NOT NULL COMMENT '地址ID',
  `CUSTOMER_ID` int(12) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_ADDRESS_PERMISSION_ADDRESS_ID` (`RAW_ADDRESS_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='粗地址权限';

-- ----------------------------
-- Records of raw_address_permissions
-- ----------------------------

-- ----------------------------
-- Table structure for raw_delivery_stations
-- ----------------------------
DROP TABLE IF EXISTS `raw_delivery_stations`;
CREATE TABLE `raw_delivery_stations` (
  `ID` int(12) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '站点名称',
  `COORDINATE` varchar(20000) COLLATE utf8_bin DEFAULT NULL COMMENT '站点坐标',
  `MAPCENTER_LNG` decimal(50,8) DEFAULT NULL COMMENT '地图中心纬度',
  `MAPCENTER_LAT` decimal(50,8) DEFAULT NULL COMMENT '地图中心经度',
  `UID` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '地图唯一标识ID',
  `EXTERNAL_ID` int(12) DEFAULT NULL COMMENT 'DMP中站点ID',
  `CUSTOMER_ID` int(12) NOT NULL COMMENT '客户ID',
  `STATUS` int(2) NOT NULL COMMENT '站点状态',
  `CREATION_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDX_DELIVERY_STATION_EXTERNAL_ID` (`EXTERNAL_ID`,`CUSTOMER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='粗站点';

-- ----------------------------
-- Records of raw_delivery_stations
-- ----------------------------

-- ----------------------------
-- Table structure for raw_delivery_station_rules
-- ----------------------------
DROP TABLE IF EXISTS `raw_delivery_station_rules`;
CREATE TABLE `raw_delivery_station_rules` (
  `ID` int(12) NOT NULL AUTO_INCREMENT,
  `RULE` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '规则',
  `RULE_EXPRESSION` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '规则表达式',
  `RULE_TYPE` int(2) NOT NULL COMMENT '规则类型',
  `RAW_ADDRESS_ID` int(12) NOT NULL COMMENT '地址ID',
  `RAW_DELIVERY_STATION_ID` int(12) NOT NULL COMMENT '站点ID',
  `CREATION_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_DS_RULE_ADDRESS_ID` (`RAW_ADDRESS_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='粗配送站点规则';

-- ----------------------------
-- Records of raw_delivery_station_rules
-- ----------------------------

-- ----------------------------
-- Table structure for scheduled_tasks
-- ----------------------------
DROP TABLE IF EXISTS `scheduled_tasks`;
CREATE TABLE `scheduled_tasks` (
  `ID` int(22) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `TASK_TYPE` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '任务类型',
  `STATUS` int(2) NOT NULL COMMENT '任务状态',
  `REFERENCE_TYPE` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '引用类型',
  `REFERENCE_ID` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '引用ID',
  `FIRE_TIME` datetime DEFAULT NULL COMMENT '触发时间',
  `COMPLETED_TIME` datetime DEFAULT NULL COMMENT '完成时间',
  `CREATED_AT` datetime DEFAULT NULL COMMENT '创建时间',
  `TRY_COUNT` int(10) DEFAULT NULL COMMENT '尝试次数',
  PRIMARY KEY (`ID`),
  KEY `IDX_TASKS_STATUS_FIRE_TIME` (`STATUS`,`FIRE_TIME`) USING BTREE,
  KEY `IDX_TASKS_STATUS_TYPE_FIRE_TIME` (`STATUS`,`TASK_TYPE`,`FIRE_TIME`) USING BTREE,
  KEY `IDX_TASKS_REFERENCE_ID` (`REFERENCE_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='调度任务';

-- ----------------------------
-- Records of scheduled_tasks
-- ----------------------------

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NAME` varchar(60) NOT NULL COMMENT '参数名称',
  `VALUE` varchar(32) DEFAULT NULL COMMENT '参数值',
  `DESCRIPTION` varchar(100) DEFAULT NULL COMMENT '参数的描述',
  `EDITABLE` int(1) DEFAULT NULL COMMENT '是否可以编辑',
  `CUSTOMER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='系统配置表';

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES ('1', 'pageSize4Tree', '100', '地址树分页大小', '1', null);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NAME` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '用户名称',
  `PASSWORD` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '登录密码',
  `STATUS` int(2) DEFAULT NULL COMMENT '状态标识（是否启用）',
  `CUSTOMER_ID` int(12) DEFAULT NULL COMMENT '客户id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户表';

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1', 'merge', 'merge', '1', null);

-- ----------------------------
-- Table structure for vendors
-- ----------------------------
DROP TABLE IF EXISTS `vendors`;
CREATE TABLE `vendors` (
  `ID` int(12) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '供货商名称',
  `EXTERNAL_ID` int(12) NOT NULL COMMENT 'DMP中供应商ID',
  `CUSTOMER_ID` int(12) NOT NULL COMMENT '客户id',
  `STATUS` int(2) NOT NULL COMMENT '状态标识（是否启用）',
  `CREATION_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDX_VENDOR_CUSTOMER_VENDOR` (`EXTERNAL_ID`,`CUSTOMER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='供货商表';

-- ----------------------------
-- Records of vendors
-- ----------------------------

-- ----------------------------
-- Table structure for vendors_aging
-- ----------------------------
DROP TABLE IF EXISTS `vendors_aging`;30
CREATE TABLE `vendors_aging` (
  `ID` int(12) NOT NULL AUTO_INCREMENT,
  `CUSTOMER_ID` int(12) NOT NULL COMMENT '客户ID',
  `ADDRESS_ID` int(12) NOT NULL COMMENT '地址ID',
  `VENDORS_ID` int(12) NOT NULL COMMENT '供应商ID',
  `AGING` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '时效',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='供应商时效';

-- ----------------------------
-- Records of vendors_aging
-- ----------------------------
