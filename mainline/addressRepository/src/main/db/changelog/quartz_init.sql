SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `QTZ_BLOB_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_BLOB_TRIGGERS`;
CREATE TABLE `QTZ_BLOB_TRIGGERS` (
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `blob_data` longblob,
  PRIMARY KEY (`trigger_name`,`trigger_group`),
  UNIQUE KEY `SYS_C00100332` (`trigger_name`,`trigger_group`),
  CONSTRAINT `sys_c00100333` FOREIGN KEY (`trigger_name`, `trigger_group`) REFERENCES `QTZ_TRIGGERS` (`trigger_name`, `trigger_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_BLOB_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_CALENDARS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_CALENDARS`;
CREATE TABLE `QTZ_CALENDARS` (
  `calendar_name` varchar(200) NOT NULL,
  `calendar` longblob NOT NULL,
  PRIMARY KEY (`calendar_name`),
  UNIQUE KEY `SYS_C00100341` (`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_CALENDARS
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_CRON_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_CRON_TRIGGERS`;
CREATE TABLE `QTZ_CRON_TRIGGERS` (
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `cron_expression` varchar(120) NOT NULL,
  `time_zone_id` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`trigger_name`,`trigger_group`),
  CONSTRAINT `sys_c00100329` FOREIGN KEY (`trigger_name`, `trigger_group`) REFERENCES `QTZ_TRIGGERS` (`trigger_name`, `trigger_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_CRON_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_FIRED_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_FIRED_TRIGGERS`;
CREATE TABLE `QTZ_FIRED_TRIGGERS` (
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
  UNIQUE KEY `SYS_C00100352` (`entry_id`),
  KEY `IDX_QTZ_FT_JOB_REQ_RECOVERY` (`requests_recovery`),
  KEY `IDX_QTZ_FT_JOB_STATEFUL` (`is_stateful`),
  KEY `IDX_QTZ_FT_JOB_GROUP` (`job_group`),
  KEY `IDX_QTZ_FT_JOB_NAME` (`job_name`),
  KEY `IDX_QTZ_FT_TRIG_INST_NAME` (`instance_name`),
  KEY `IDX_QTZ_FT_TRIG_VOLATILE` (`is_volatile`),
  KEY `IDX_QTZ_FT_TRIG_NM_GP` (`trigger_name`,`trigger_group`),
  KEY `IDX_QTZ_FT_TRIG_GROUP` (`trigger_group`),
  KEY `IDX_QTZ_FT_TRIG_NAME` (`trigger_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_FIRED_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_JOB_DETAILS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_JOB_DETAILS`;
CREATE TABLE `QTZ_JOB_DETAILS` (
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
  UNIQUE KEY `SYS_C00100302` (`job_name`,`job_group`),
  KEY `IDX_QTZ_J_REQ_RECOVERY` (`requests_recovery`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_JOB_DETAILS
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_JOB_LISTENERS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_JOB_LISTENERS`;
CREATE TABLE `QTZ_JOB_LISTENERS` (
  `job_name` varchar(200) NOT NULL,
  `job_group` varchar(200) NOT NULL,
  `job_listener` varchar(200) NOT NULL,
  PRIMARY KEY (`job_name`,`job_group`,`job_listener`),
  UNIQUE KEY `SYS_C00100306` (`job_name`,`job_group`,`job_listener`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_JOB_LISTENERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_LOCKS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_LOCKS`;
CREATE TABLE `QTZ_LOCKS` (
  `lock_name` varchar(40) NOT NULL,
  PRIMARY KEY (`lock_name`),
  UNIQUE KEY `SYS_C00100358` (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_LOCKS
-- ----------------------------
INSERT INTO `QTZ_LOCKS` VALUES ('STATE_ACCESS');
INSERT INTO `QTZ_LOCKS` VALUES ('TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for `QTZ_PAUSED_TRIGGER_GRPS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QTZ_PAUSED_TRIGGER_GRPS` (
  `trigger_group` varchar(200) NOT NULL,
  PRIMARY KEY (`trigger_group`),
  UNIQUE KEY `SYS_C00100343` (`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_SCHEDULER_STATE`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_SCHEDULER_STATE`;
CREATE TABLE `QTZ_SCHEDULER_STATE` (
  `instance_name` varchar(200) NOT NULL,
  `last_checkin_time` bigint(13) NOT NULL,
  `checkin_interval` bigint(13) NOT NULL,
  PRIMARY KEY (`instance_name`),
  UNIQUE KEY `SYS_C00100356` (`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_SCHEDULER_STATE
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_SIMPLE_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QTZ_SIMPLE_TRIGGERS` (
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `repeat_count` int(7) NOT NULL,
  `repeat_interval` bigint(12) NOT NULL,
  `times_triggered` bigint(10) NOT NULL,
  PRIMARY KEY (`trigger_name`,`trigger_group`),
  CONSTRAINT `sys_c00100324` FOREIGN KEY (`trigger_name`, `trigger_group`) REFERENCES `QTZ_TRIGGERS` (`trigger_name`, `trigger_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_SIMPLE_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_TRIGGER_LISTENERS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_TRIGGER_LISTENERS`;
CREATE TABLE `QTZ_TRIGGER_LISTENERS` (
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `trigger_listener` varchar(200) NOT NULL,
  PRIMARY KEY (`trigger_name`,`trigger_group`,`trigger_listener`),
  CONSTRAINT `sys_c00100338` FOREIGN KEY (`trigger_name`, `trigger_group`) REFERENCES `QTZ_TRIGGERS` (`trigger_name`, `trigger_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_TRIGGER_LISTENERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QTZ_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QTZ_TRIGGERS`;
CREATE TABLE `QTZ_TRIGGERS` (
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
  KEY `sys_c00100317` (`job_name`,`job_group`),
  KEY `IDX_QTZ_T_VOLATILE` (`is_volatile`),
  KEY `IDX_QTZ_T_NFT_ST` (`next_fire_time`,`trigger_state`),
  KEY `IDX_QTZ_T_STATE` (`trigger_state`),
  KEY `IDX_QTZ_T_NEXT_FIRE_TIME` (`next_fire_time`),
  CONSTRAINT `sys_c00100317` FOREIGN KEY (`job_name`, `job_group`) REFERENCES `QTZ_JOB_DETAILS` (`job_name`, `job_group`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QTZ_TRIGGERS
-- ----------------------------
