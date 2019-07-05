/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : localhost:3306
 Source Schema         : jeecg

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 05/07/2019 19:31:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for spider_36kr_record
-- ----------------------------
DROP TABLE IF EXISTS `spider_36kr_record`;
CREATE TABLE `spider_36kr_record`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `kr_id` int(11) NULL DEFAULT NULL COMMENT '平台推文id',
  `titile` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `description` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '描述',
  `updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  `news_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '原文链接',
  `published_at` datetime(0) NULL DEFAULT NULL COMMENT '推送日期',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_analyze` tinyint(2) NULL DEFAULT 0 COMMENT '是否分析 0否 1是',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '新闻图片',
  `oplatform_core` int(4) NULL DEFAULT NULL COMMENT '1:36kr 2:新闻联播',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `kr_id_index`(`kr_id`) USING BTREE,
  INDEX `createtm_kr_index`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
