/*
 Navicat MySQL Data Transfer

 Source Server Type    : MySQL
 Source Server Version : 50644
 Source Host           : localhost:3307
 Source Schema         : flycat_blog

 Target Server Type    : MySQL
 Target Server Version : 50644
 File Encoding         : 65001

 Date: 03/11/2019 13:30:29
*/
CREATE DATABASE IF NOT EXISTS flycat_blog;
USE flycat_blog;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `content` varchar(4096) NOT NULL,
  `nickname` varchar(512) DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `ctime` datetime NOT NULL COMMENT 'create time',
  `post_id` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COMMENT='开播历史';

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `uid` int(11) unsigned NOT NULL COMMENT 'uid',
  `title` varchar(512) NOT NULL,
  `content` mediumtext NOT NULL,
  `flag` varchar(512) DEFAULT NULL,
  `ctime` datetime NOT NULL COMMENT 'create time',
  `status` tinyint(1) DEFAULT '0',
  `visit_count` int(10) DEFAULT '0',
  `like_count` int(10) DEFAULT '0',
  `source` tinyint(1) DEFAULT '0',
  `path` varchar(256) DEFAULT NULL,
  `parsed_content` mediumtext,
  `post_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'post time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'post time',
  `comment_count` int(10) NOT NULL DEFAULT '0',
  `password` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='开播历史';

SET FOREIGN_KEY_CHECKS = 1;
