/*
Navicat MySQL Data Transfer

Source Server         : root
Source Server Version : 80034
Source Host           : localhost:3306
Source Database       : wordtreedb

Target Server Type    : MYSQL
Target Server Version : 80034
File Encoding         : 65001

Date: 2025-11-18 11:54:56
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for date_record
-- ----------------------------
DROP TABLE IF EXISTS `date_record`;
CREATE TABLE `date_record` (
  `userID` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `date` date NOT NULL,
  `learn_count` int DEFAULT '0',
  `review_count` int DEFAULT '0',
  `learning_time` int DEFAULT '0',
  PRIMARY KEY (`userID`,`date`),
  CONSTRAINT `fk_date_user` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of date_record
-- ----------------------------
INSERT INTO `date_record` VALUES ('u001', '2025-11-18', '5', '2', '30');

-- ----------------------------
-- Table structure for memory_record
-- ----------------------------
DROP TABLE IF EXISTS `memory_record`;
CREATE TABLE `memory_record` (
  `wordID` int NOT NULL,
  `userID` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `phase` int DEFAULT '0',
  `next_mmry_time` date DEFAULT NULL,
  PRIMARY KEY (`wordID`,`userID`),
  KEY `idx_memory_user` (`userID`),
  CONSTRAINT `fk_memory_user` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_memory_word` FOREIGN KEY (`wordID`) REFERENCES `word` (`wordID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of memory_record
-- ----------------------------
INSERT INTO `memory_record` VALUES ('1', 'u001', '1', '2025-11-18');

-- ----------------------------
-- Table structure for phrase
-- ----------------------------
DROP TABLE IF EXISTS `phrase`;
CREATE TABLE `phrase` (
  `phraseID` int NOT NULL AUTO_INCREMENT,
  `wordID` int NOT NULL,
  `en_phrase` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cn_phrase` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`phraseID`),
  KEY `idx_phrase_word` (`wordID`),
  CONSTRAINT `fk_phrase_word` FOREIGN KEY (`wordID`) REFERENCES `word` (`wordID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of phrase
-- ----------------------------
INSERT INTO `phrase` VALUES ('1', '1', 'apple pie', '苹果派');

-- ----------------------------
-- Table structure for sentence
-- ----------------------------
DROP TABLE IF EXISTS `sentence`;
CREATE TABLE `sentence` (
  `sentenceID` int NOT NULL AUTO_INCREMENT,
  `wordID` int NOT NULL,
  `en_sentence` text COLLATE utf8mb4_general_ci,
  `cn_sentence` text COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`sentenceID`),
  KEY `idx_sentence_word` (`wordID`),
  CONSTRAINT `fk_sentence_word` FOREIGN KEY (`wordID`) REFERENCES `word` (`wordID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of sentence
-- ----------------------------
INSERT INTO `sentence` VALUES ('1', '1', 'She ate an apple.', '她吃了一个苹果。');

-- ----------------------------
-- Table structure for translation
-- ----------------------------
DROP TABLE IF EXISTS `translation`;
CREATE TABLE `translation` (
  `translationID` int NOT NULL AUTO_INCREMENT,
  `wordID` int NOT NULL,
  `en_tran` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cn_tran` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `word_type` varchar(16) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`translationID`),
  KEY `idx_translation_word` (`wordID`),
  CONSTRAINT `fk_translation_word` FOREIGN KEY (`wordID`) REFERENCES `word` (`wordID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of translation
-- ----------------------------
INSERT INTO `translation` VALUES ('1', '1', 'a round fruit that is red or green', '苹果', 'n.');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userID` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `user_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `avator` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `target_book` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `need_count` int DEFAULT '20',
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('u001', 'tester', '123456', null, 'basic', '20');

-- ----------------------------
-- Table structure for word
-- ----------------------------
DROP TABLE IF EXISTS `word`;
CREATE TABLE `word` (
  `wordID` int NOT NULL AUTO_INCREMENT,
  `word` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `uk_phone` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `us_phone` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `rem_method` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `pic_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `belong_book` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`wordID`),
  KEY `idx_belong_book` (`belong_book`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of word
-- ----------------------------
INSERT INTO `word` VALUES ('1', 'apple', 'ˈæpl', 'ˈæpəl', '联想苹果logo', null, 'basic');
