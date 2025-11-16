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

 Date: 31/10/2025 14:18:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for assignment
-- ----------------------------
DROP TABLE IF EXISTS `assignment`;
CREATE TABLE `assignment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '作业描述',
  `total_questions` int NULL DEFAULT 0 COMMENT '题目数量',
  `deadline` date NOT NULL COMMENT '截止日期',
  `is_completed` tinyint(1) NULL DEFAULT 0 COMMENT '是否已完成',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `difficulty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '难度等级',
  `teacher_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教师姓名',
  `subject` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '科目',
  `chapter` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '章节',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_assignment_teacher`(`teacher_name` ASC) USING BTREE,
  INDEX `idx_assignment_deadline`(`deadline` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of assignment
-- ----------------------------
INSERT INTO `assignment` VALUES (12, '基础加减法练习', '完成10道简单的加减法题目，巩固基础计算能力', 10, '2025-11-01', 0, '2025-10-28 00:00:00', 'easy', '管理员', '数学', '第一章');
INSERT INTO `assignment` VALUES (13, '混合运算练习', '完成15道加减混合运算题目，提高计算灵活性', 15, '2025-11-01', 0, '2025-10-28 00:00:00', 'medium', '管理员', '数学', '第一章');
INSERT INTO `assignment` VALUES (15, 's', 's', 3, '2025-10-31', 0, '2025-10-30 00:00:00', 'easy', '管理员', 's', 's');

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `assignment_id` int NULL DEFAULT NULL COMMENT '所属作业ID',
  `question_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `answer` int NOT NULL COMMENT '正确答案',
  `operator` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '运算符类型',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_assignment`(`assignment_id` ASC) USING BTREE,
  CONSTRAINT `question_ibfk_1` FOREIGN KEY (`assignment_id`) REFERENCES `assignment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 174 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 12, '5 + 3 = ?', 8, '+');
INSERT INTO `question` VALUES (2, 12, '9 - 4 = ?', 5, '-');
INSERT INTO `question` VALUES (3, 12, '6 + 7 = ?', 13, '+');
INSERT INTO `question` VALUES (4, 12, '12 - 5 = ?', 7, '-');
INSERT INTO `question` VALUES (5, 12, '8 + 9 = ?', 17, '+');
INSERT INTO `question` VALUES (6, 12, '15 - 8 = ?', 7, '-');
INSERT INTO `question` VALUES (7, 12, '10 + 6 = ?', 16, '+');
INSERT INTO `question` VALUES (8, 12, '18 - 9 = ?', 9, '-');
INSERT INTO `question` VALUES (9, 12, '11 + 4 = ?', 15, '+');
INSERT INTO `question` VALUES (10, 12, '20 - 12 = ?', 8, '-');
INSERT INTO `question` VALUES (34, 13, '67 + 27 = ?', 94, '+');
INSERT INTO `question` VALUES (35, 13, '47 + 71 = ?', 118, '+');
INSERT INTO `question` VALUES (36, 13, '94 + 85 = ?', 179, '+');
INSERT INTO `question` VALUES (37, 13, '56 + 79 = ?', 135, '+');
INSERT INTO `question` VALUES (38, 13, '94 + 54 = ?', 148, '+');
INSERT INTO `question` VALUES (39, 13, '41 + 76 = ?', 117, '+');
INSERT INTO `question` VALUES (40, 13, '26 + 22 = ?', 48, '+');
INSERT INTO `question` VALUES (41, 13, '7 + 58 = ?', 65, '+');
INSERT INTO `question` VALUES (42, 13, '79 + 45 = ?', 124, '+');
INSERT INTO `question` VALUES (43, 13, '11 + 40 = ?', 51, '+');
INSERT INTO `question` VALUES (44, 13, '62 + 86 = ?', 148, '+');
INSERT INTO `question` VALUES (45, 13, '54 + 7 = ?', 61, '+');
INSERT INTO `question` VALUES (46, 13, '63 + 42 = ?', 105, '+');
INSERT INTO `question` VALUES (47, 13, '76 + 97 = ?', 173, '+');
INSERT INTO `question` VALUES (48, 13, '24 + 4 = ?', 28, '+');
INSERT INTO `question` VALUES (49, 15, '59 + 81 = ?', 140, '+');
INSERT INTO `question` VALUES (50, 15, '22 + 30 = ?', 52, '+');
INSERT INTO `question` VALUES (51, 15, '96 + 22 = ?', 118, '+');
INSERT INTO `question` VALUES (52, NULL, '90 + 62 = ?', 152, '+');
INSERT INTO `question` VALUES (53, NULL, '74 + 87 = ?', 161, '+');
INSERT INTO `question` VALUES (54, NULL, '57 + 44 = ?', 101, '+');
INSERT INTO `question` VALUES (55, NULL, '66 + 77 = ?', 143, '+');
INSERT INTO `question` VALUES (56, NULL, '44 + 91 = ?', 135, '+');
INSERT INTO `question` VALUES (57, NULL, '4 + 88 = ?', 92, '+');
INSERT INTO `question` VALUES (58, NULL, '64 + 59 = ?', 123, '+');
INSERT INTO `question` VALUES (59, NULL, '33 + 38 = ?', 71, '+');
INSERT INTO `question` VALUES (60, NULL, '21 + 89 = ?', 110, '+');
INSERT INTO `question` VALUES (61, NULL, '5 + 3 = ?', 8, '+');
INSERT INTO `question` VALUES (62, NULL, '22 + 44 = ?', 66, '+');
INSERT INTO `question` VALUES (63, NULL, '67 + 60 = ?', 127, '+');
INSERT INTO `question` VALUES (64, NULL, '20 + 10 = ?', 30, '+');
INSERT INTO `question` VALUES (65, NULL, '10 + 51 = ?', 61, '+');
INSERT INTO `question` VALUES (66, NULL, '31 + 92 = ?', 123, '+');
INSERT INTO `question` VALUES (67, NULL, '23 + 39 = ?', 62, '+');
INSERT INTO `question` VALUES (68, NULL, '33 + 63 = ?', 96, '+');
INSERT INTO `question` VALUES (69, NULL, '22 + 39 = ?', 61, '+');
INSERT INTO `question` VALUES (70, NULL, '17 + 19 = ?', 36, '+');
INSERT INTO `question` VALUES (71, NULL, '73 + 76 = ?', 149, '+');
INSERT INTO `question` VALUES (72, NULL, '56 + 13 = ?', 69, '+');
INSERT INTO `question` VALUES (73, NULL, '100 + 82 = ?', 182, '+');
INSERT INTO `question` VALUES (74, NULL, '87 + 61 = ?', 148, '+');
INSERT INTO `question` VALUES (75, NULL, '23 + 78 = ?', 101, '+');
INSERT INTO `question` VALUES (76, NULL, '9 + 16 = ?', 25, '+');
INSERT INTO `question` VALUES (77, NULL, '3 + 10 = ?', 13, '+');
INSERT INTO `question` VALUES (78, NULL, '97 + 23 = ?', 120, '+');
INSERT INTO `question` VALUES (79, NULL, '21 + 54 = ?', 75, '+');
INSERT INTO `question` VALUES (80, NULL, '3 + 48 = ?', 51, '+');
INSERT INTO `question` VALUES (81, NULL, '47 + 58 = ?', 105, '+');
INSERT INTO `question` VALUES (82, NULL, '96 + 68 = ?', 164, '+');
INSERT INTO `question` VALUES (83, NULL, '77 + 95 = ?', 172, '+');
INSERT INTO `question` VALUES (84, NULL, '24 + 79 = ?', 103, '+');
INSERT INTO `question` VALUES (85, NULL, '80 + 17 = ?', 97, '+');
INSERT INTO `question` VALUES (86, NULL, '90 + 39 = ?', 129, '+');
INSERT INTO `question` VALUES (87, NULL, '61 + 1 = ?', 62, '+');
INSERT INTO `question` VALUES (88, NULL, '93 + 81 = ?', 174, '+');
INSERT INTO `question` VALUES (89, NULL, '63 + 74 = ?', 137, '+');
INSERT INTO `question` VALUES (90, NULL, '22 + 26 = ?', 48, '+');
INSERT INTO `question` VALUES (91, NULL, '50 + 53 = ?', 103, '+');
INSERT INTO `question` VALUES (92, NULL, '23 + 96 = ?', 119, '+');
INSERT INTO `question` VALUES (93, NULL, '100 + 53 = ?', 153, '+');
INSERT INTO `question` VALUES (94, NULL, '10 + 6 = ?', 16, '+');
INSERT INTO `question` VALUES (95, NULL, '30 + 23 = ?', 53, '+');
INSERT INTO `question` VALUES (96, NULL, '89 + 80 = ?', 169, '+');
INSERT INTO `question` VALUES (97, NULL, '64 + 36 = ?', 100, '+');
INSERT INTO `question` VALUES (98, NULL, '9 + 25 = ?', 34, '+');
INSERT INTO `question` VALUES (99, NULL, '83 + 39 = ?', 122, '+');
INSERT INTO `question` VALUES (100, NULL, '81 + 87 = ?', 168, '+');
INSERT INTO `question` VALUES (101, NULL, '80 + 88 = ?', 168, '+');
INSERT INTO `question` VALUES (102, NULL, '87 + 68 = ?', 155, '+');
INSERT INTO `question` VALUES (103, NULL, '47 + 8 = ?', 55, '+');
INSERT INTO `question` VALUES (104, NULL, '26 + 94 = ?', 120, '+');
INSERT INTO `question` VALUES (105, NULL, '90 + 39 = ?', 129, '+');
INSERT INTO `question` VALUES (106, NULL, '44 + 85 = ?', 129, '+');
INSERT INTO `question` VALUES (107, NULL, '28 + 3 = ?', 31, '+');
INSERT INTO `question` VALUES (108, NULL, '5 + 27 = ?', 32, '+');
INSERT INTO `question` VALUES (109, NULL, '88 + 56 = ?', 144, '+');
INSERT INTO `question` VALUES (110, NULL, '86 + 41 = ?', 127, '+');
INSERT INTO `question` VALUES (111, NULL, '20 + 69 = ?', 89, '+');
INSERT INTO `question` VALUES (112, NULL, '49 + 51 = ?', 100, '+');
INSERT INTO `question` VALUES (113, NULL, '85 + 65 = ?', 150, '+');
INSERT INTO `question` VALUES (114, NULL, '48 + 12 = ?', 60, '+');
INSERT INTO `question` VALUES (115, NULL, '54 + 9 = ?', 63, '+');
INSERT INTO `question` VALUES (116, NULL, '81 + 88 = ?', 169, '+');
INSERT INTO `question` VALUES (117, NULL, '85 + 48 = ?', 133, '+');
INSERT INTO `question` VALUES (118, NULL, '69 + 14 = ?', 83, '+');
INSERT INTO `question` VALUES (119, NULL, '20 + 65 = ?', 85, '+');
INSERT INTO `question` VALUES (120, NULL, '60 + 32 = ?', 92, '+');
INSERT INTO `question` VALUES (121, NULL, '50 + 61 = ?', 111, '+');
INSERT INTO `question` VALUES (122, NULL, '49 + 2 = ?', 51, '+');
INSERT INTO `question` VALUES (123, NULL, '42 + 24 = ?', 66, '+');
INSERT INTO `question` VALUES (124, NULL, '23 + 26 = ?', 49, '+');
INSERT INTO `question` VALUES (125, NULL, '2 + 21 = ?', 23, '+');
INSERT INTO `question` VALUES (126, NULL, '79 + 50 = ?', 129, '+');
INSERT INTO `question` VALUES (127, NULL, '40 + 88 = ?', 128, '+');
INSERT INTO `question` VALUES (128, NULL, '16 + 62 = ?', 78, '+');
INSERT INTO `question` VALUES (129, NULL, '73 + 25 = ?', 98, '+');
INSERT INTO `question` VALUES (130, NULL, '98 + 50 = ?', 148, '+');
INSERT INTO `question` VALUES (131, NULL, '4 + 10 = ?', 14, '+');
INSERT INTO `question` VALUES (132, NULL, '74 + 37 = ?', 111, '+');
INSERT INTO `question` VALUES (133, NULL, '3 + 33 = ?', 36, '+');
INSERT INTO `question` VALUES (134, NULL, '98 + 7 = ?', 105, '+');
INSERT INTO `question` VALUES (135, NULL, '35 + 87 = ?', 122, '+');
INSERT INTO `question` VALUES (136, NULL, '58 + 34 = ?', 92, '+');
INSERT INTO `question` VALUES (137, NULL, '86 + 91 = ?', 177, '+');
INSERT INTO `question` VALUES (138, NULL, '88 + 8 = ?', 96, '+');
INSERT INTO `question` VALUES (139, NULL, '9 + 30 = ?', 39, '+');
INSERT INTO `question` VALUES (140, NULL, '13 + 79 = ?', 92, '+');
INSERT INTO `question` VALUES (141, NULL, '25 + 84 = ?', 109, '+');
INSERT INTO `question` VALUES (142, NULL, '38 + 55 = ?', 93, '+');
INSERT INTO `question` VALUES (143, NULL, '13 + 9 = ?', 22, '+');
INSERT INTO `question` VALUES (144, NULL, '31 + 30 = ?', 61, '+');
INSERT INTO `question` VALUES (145, NULL, '54 + 19 = ?', 73, '+');
INSERT INTO `question` VALUES (146, NULL, '83 + 28 = ?', 111, '+');
INSERT INTO `question` VALUES (147, NULL, '30 + 92 = ?', 122, '+');
INSERT INTO `question` VALUES (148, NULL, '2 + 67 = ?', 69, '+');
INSERT INTO `question` VALUES (149, NULL, '28 + 87 = ?', 115, '+');
INSERT INTO `question` VALUES (150, NULL, '8 + 74 = ?', 82, '+');
INSERT INTO `question` VALUES (151, NULL, '65 + 45 = ?', 110, '+');
INSERT INTO `question` VALUES (152, NULL, '18 + 54 = ?', 72, '+');
INSERT INTO `question` VALUES (153, NULL, '76 + 97 = ?', 173, '+');
INSERT INTO `question` VALUES (154, NULL, '38 + 15 = ?', 53, '+');
INSERT INTO `question` VALUES (155, NULL, '83 - 19 = ?', 64, '-');
INSERT INTO `question` VALUES (156, NULL, '26 - 8 = ?', 18, '-');
INSERT INTO `question` VALUES (157, NULL, '10 - 10 = ?', 0, '-');
INSERT INTO `question` VALUES (158, NULL, '70 + 63 = ?', 133, '+');
INSERT INTO `question` VALUES (159, NULL, '74 - 9 = ?', 65, '-');
INSERT INTO `question` VALUES (160, NULL, '11 - 3 = ?', 8, '-');
INSERT INTO `question` VALUES (161, NULL, '6 - 5 = ?', 1, '-');
INSERT INTO `question` VALUES (162, NULL, '88 + 86 = ?', 174, '+');
INSERT INTO `question` VALUES (163, NULL, '54 - 38 = ?', 16, '-');
INSERT INTO `question` VALUES (164, NULL, '70 + 46 = ?', 116, '+');
INSERT INTO `question` VALUES (165, NULL, '45 + 43 = ?', 88, '+');
INSERT INTO `question` VALUES (166, NULL, '27 + 65 = ?', 92, '+');
INSERT INTO `question` VALUES (167, NULL, '72 + 11 = ?', 83, '+');
INSERT INTO `question` VALUES (168, NULL, '90 + 72 = ?', 162, '+');
INSERT INTO `question` VALUES (169, NULL, '85 + 97 = ?', 182, '+');
INSERT INTO `question` VALUES (170, NULL, '62 + 38 = ?', 100, '+');
INSERT INTO `question` VALUES (171, NULL, '99 + 5 = ?', 104, '+');
INSERT INTO `question` VALUES (172, NULL, '91 + 2 = ?', 93, '+');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学生姓名',
  `gender` tinyint NOT NULL COMMENT '性别：0-女，1-男',
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `class_info` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (1, 'student001', '123456', '王小明', 1, '001', '13812341234', 'wang@example.com', '软件工程1班', '2025-10-27 15:56:21', '2025-10-28 19:52:14');
INSERT INTO `student` VALUES (2, 'student002', '123456', '李晓华', 0, '002', '13812341235', 'li@example.com', '软件工程1班', '2025-10-27 15:56:21', '2025-10-28 19:52:20');
INSERT INTO `student` VALUES (3, 'student003', '123456', '张伟', 1, '003', '13812341236', 'zhang@example.com', '软件工程2班', '2025-10-27 15:56:21', '2025-10-28 19:52:22');
INSERT INTO `student` VALUES (4, 'student004', '123456', '刘芳', 0, '004', '13812341237', 'liu@example.com', '软件工程2班', '2025-10-27 15:56:21', '2025-10-28 19:52:25');
INSERT INTO `student` VALUES (5, 'student005', '123456', '陈强', 1, '005', '13812341238', 'chen@example.com', '软件工程1班', '2025-10-27 15:56:21', '2025-10-28 19:52:27');

-- ----------------------------
-- Table structure for submission
-- ----------------------------
DROP TABLE IF EXISTS `submission`;
CREATE TABLE `submission`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `assignment_id` int NOT NULL COMMENT '作业ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `student_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `submission_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `score` int NULL DEFAULT 0 COMMENT '得分',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending(未提交), completed(已完成), overdue(已逾期)',
  `answers` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生答案JSON字符串',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_submission_assignment`(`assignment_id` ASC) USING BTREE,
  INDEX `idx_submission_student`(`student_id` ASC) USING BTREE,
  CONSTRAINT `submission_ibfk_1` FOREIGN KEY (`assignment_id`) REFERENCES `assignment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业提交表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of submission
-- ----------------------------
INSERT INTO `submission` VALUES (16, 12, 5, '陈强', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (17, 12, 4, '刘芳', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (18, 12, 3, '张伟', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (19, 12, 2, '李晓华', '2025-10-28 19:58:56', 100, 'completed', '{\"1\":8,\"2\":5,\"3\":13,\"4\":7,\"5\":17,\"6\":7,\"7\":16,\"8\":9,\"9\":15,\"10\":8}');
INSERT INTO `submission` VALUES (20, 12, 1, '王小明', '2025-10-28 19:54:49', 60, 'completed', '{\"1\":8,\"2\":5,\"3\":13,\"4\":7,\"5\":17,\"6\":2,\"7\":25,\"8\":9,\"9\":25,\"10\":36}');
INSERT INTO `submission` VALUES (21, 13, 5, '陈强', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (22, 13, 4, '刘芳', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (23, 13, 3, '张伟', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (24, 13, 2, '李晓华', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (25, 13, 1, '王小明', '2025-10-28 20:13:27', 46, 'completed', '{\"34\":94,\"35\":118,\"36\":179,\"37\":25,\"38\":54,\"39\":541,\"40\":58,\"41\":52,\"42\":452,\"43\":553,\"44\":55,\"45\":61,\"46\":105,\"47\":173,\"48\":28}');
INSERT INTO `submission` VALUES (31, 15, 5, '陈强', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (32, 15, 4, '刘芳', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (33, 15, 3, '张伟', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (34, 15, 2, '李晓华', NULL, 0, 'pending', '{}');
INSERT INTO `submission` VALUES (35, 15, 1, '王小明', '2025-10-30 09:24:24', 33, 'completed', '{\"49\":36,\"50\":52,\"51\":25}');

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教师信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES (1, 'admin', '123456', '管理员', '13800138000', 'admin@example.com', '2025-10-22 11:27:58', '2025-10-22 11:29:16');
INSERT INTO `teacher` VALUES (2, 'teacher1', '123456', '张老师', '13900139000', 'teacher1@example.com', '2025-10-22 11:27:58', '2025-10-22 11:27:58');
INSERT INTO `teacher` VALUES (3, 'teacher2', '123456', '李老师', '13700137000', 'teacher2@example.com', '2025-10-22 11:27:58', '2025-10-22 11:27:58');

-- ----------------------------
-- Table structure for paper
-- ----------------------------
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '试卷名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '试卷描述',
  `total_questions` int NULL DEFAULT 0 COMMENT '题目数量',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷表' ROW_FORMAT = Dynamic;

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
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '未开始' COMMENT '状态：未开始/进行中/已结束',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `paper_id` int NOT NULL COMMENT '关联试卷ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_exam_paper`(`paper_id` ASC) USING BTREE,
  CONSTRAINT `fk_exam_paper` FOREIGN KEY (`paper_id`) REFERENCES `paper` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_submission
-- ----------------------------
DROP TABLE IF EXISTS `exam_submission`;
CREATE TABLE `exam_submission`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `exam_id` int NOT NULL COMMENT '考试ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `student_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `submission_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `score` int NULL DEFAULT 0 COMMENT '得分',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending/completed/overdue',
  `answers` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生答案JSON字符串',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_submission_exam`(`exam_id` ASC) USING BTREE,
  INDEX `idx_exam_submission_student`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_exam_submission_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试提交表' ROW_FORMAT = Dynamic;

-- Table structure for paper_question
-- ----------------------------
DROP TABLE IF EXISTS `paper_question`;
CREATE TABLE `paper_question`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `paper_id` int NOT NULL COMMENT '试卷ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `score` int NOT NULL DEFAULT 0 COMMENT '题目分值',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_paper_question_paper`(`paper_id` ASC) USING BTREE,
  INDEX `idx_paper_question_question`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_pq_paper` FOREIGN KEY (`paper_id`) REFERENCES `paper` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_pq_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷-题目关联表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
