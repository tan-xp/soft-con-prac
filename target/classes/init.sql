-- 创建数据库
CREATE DATABASE IF NOT EXISTS softcon_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE softcon_db;

-- 创建教师表
CREATE TABLE IF NOT EXISTS teacher (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(50) NOT NULL COMMENT '密码',
    name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='教师信息表';

-- 创建学生表
CREATE TABLE IF NOT EXISTS student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(50) NOT NULL COMMENT '密码',
    name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    gender TINYINT NOT NULL COMMENT '性别：0-女，1-男',
    student_id VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    class_info VARCHAR(50) COMMENT '班级信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='学生信息表';

-- 插入教师测试数据
INSERT INTO teacher (username, password, name, phone, email) VALUES
('admin', 'admin', '管理员', '13800138000', 'admin@example.com'),
('teacher1', '123456', '张老师', '13900139000', 'teacher1@example.com'),
('teacher2', '123456', '李老师', '13700137000', 'teacher2@example.com');

-- 插入学生测试数据
INSERT INTO student (username, password, name, gender, student_id, phone, email, class_info) VALUES
('student001', '123456', '王小明', 1, '20230001', '13812341234', 'wang@example.com', '软件工程1班'),
('student002', '123456', '李晓华', 0, '20230002', '13812341235', 'li@example.com', '软件工程1班'),
('student003', '123456', '张伟', 1, '20230003', '13812341236', 'zhang@example.com', '软件工程2班'),
('student004', '123456', '刘芳', 0, '20230004', '13812341237', 'liu@example.com', '软件工程2班'),
('student005', '123456', '陈强', 1, '20230005', '13812341238', 'chen@example.com', '软件工程1班');