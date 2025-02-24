SET FOREIGN_KEY_CHECKS=0;

-- ----
-- CREATE DATABASE activiti DEFAULT CHARSET=utf8mb4;
-- ----

-- ----------------------------
-- 创建用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `address` varchar(64) DEFAULT NULL COMMENT '联系地址',
  `username` varchar(255) DEFAULT NULL COMMENT '账号',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `roles` varchar(255) DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 填充用户表
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admincn', 'beijing', 'admin', '$2a$10$gw46pmsOVYO.smHYQ2jH.OoXoe.lGP8OStDkHNs/E74GqZDL5K7ki', 'ROLE_ACTIVITI_ADMIN');
INSERT INTO `user` VALUES ('2', 'bajiecn', 'shanghang', 'bajie', '$2a$10$gw46pmsOVYO.smHYQ2jH.OoXoe.lGP8OStDkHNs/E74GqZDL5K7ki', 'ROLE_ACTIVITI_USER,GROUP_activitiTeam,g_bajiewukong');
INSERT INTO `user` VALUES ('3', 'wukongcn', 'beijing', 'wukong', '$2a$10$gw46pmsOVYO.smHYQ2jH.OoXoe.lGP8OStDkHNs/E74GqZDL5K7ki', 'ROLE_ACTIVITI_USER,GROUP_activitiTeam');
INSERT INTO `user` VALUES ('4', 'salaboycn', 'beijing', 'salaboy', '$2a$10$gw46pmsOVYO.smHYQ2jH.OoXoe.lGP8OStDkHNs/E74GqZDL5K7ki', 'ROLE_ACTIVITI_USER,GROUP_activitiTeam');

-- ----------------------------
-- 动态表单数据存储
-- ----------------------------
DROP TABLE IF EXISTS `formdata`;
CREATE TABLE `formdata` (
  `PROC_DEF_ID_` varchar(64) DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) DEFAULT NULL,
  `FORM_KEY_` varchar(255) DEFAULT NULL,
  `Control_ID_` varchar(100) DEFAULT NULL,
  `Control_VALUE_` varchar(2000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

