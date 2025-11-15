/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : softcon_db

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 31/10/2025 14:30:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '考试描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `duration` int NOT NULL COMMENT '考试时长（分钟）',
  `total_score` int NOT NULL COMMENT '总分',
  `difficulty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '难度级别',
  `teacher_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教师姓名',
  `paper_id` int NULL DEFAULT NULL COMMENT '试卷id',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending(未开始), ongoing(进行中), completed(已结束)',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_teacher`(`teacher_name` ASC) USING BTREE,
  INDEX `idx_exam_status`(`status` ASC) USING BTREE,
  INDEX `idx_exam_end_time`(`end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for paper
-- ----------------------------
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '试卷标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '试卷描述',
  `total_questions` int NOT NULL COMMENT '题目总数',
  `total_score` int NOT NULL COMMENT '总分',
  `difficulty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '难度级别',
  `teacher_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者姓名',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_paper_teacher`(`teacher_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷库表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for paper_question
-- ----------------------------
DROP TABLE IF EXISTS `paper_question`;
CREATE TABLE `paper_question`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `paper_id` int NOT NULL COMMENT '试卷ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `score` int NOT NULL COMMENT '本题分值',
  `sort_order` int NOT NULL COMMENT '题目排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_paper_question_paper`(`paper_id` ASC) USING BTREE,
  INDEX `idx_paper_question_question`(`question_id` ASC) USING BTREE,
  INDEX `idx_paper_question_order`(`paper_id` ASC, `sort_order` ASC) USING BTREE,
  CONSTRAINT `paper_question_ibfk_1` FOREIGN KEY (`paper_id`) REFERENCES `paper` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `paper_question_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷题目关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_submission
-- ----------------------------
DROP TABLE IF EXISTS `exam_submission`;
CREATE TABLE `exam_submission`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `exam_id` int NOT NULL COMMENT '考试ID',
  `paper_id` int NOT NULL COMMENT '试卷id',
  `student_id` int NOT NULL COMMENT '学生ID',
  `student_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始答题时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `score` int NULL DEFAULT 0 COMMENT '得分',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'not_started' COMMENT '状态（未开始、进行中、已提交、已逾期）',
  `answers` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生答案（JSON格式存储）',
  `used_time` int NULL DEFAULT 0 COMMENT '用时（分钟）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_submission_exam`(`exam_id` ASC) USING BTREE,
  INDEX `idx_exam_submission_student`(`student_id` ASC) USING BTREE,
  INDEX `idx_exam_submission_status`(`status` ASC) USING BTREE,
  CONSTRAINT `exam_submission_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `exam_submission_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试提交记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Modify question table to support standalone questions
-- ----------------------------
ALTER TABLE `question` ADD COLUMN `is_standalone` tinyint(1) NULL DEFAULT 1 COMMENT '是否为独立题目（不属于特定作业）' AFTER `operator`;
ALTER TABLE `question` ADD COLUMN `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `is_standalone`;
ALTER TABLE `question` ADD INDEX `idx_question_standalone`(`is_standalone` ASC) USING BTREE;
ALTER TABLE `question` ADD INDEX `idx_question_operator`(`operator` ASC) USING BTREE;

SET FOREIGN_KEY_CHECKS = 1;