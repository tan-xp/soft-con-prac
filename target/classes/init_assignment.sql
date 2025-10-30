-- 初始化作业管理相关表

-- 创建作业表
CREATE TABLE IF NOT EXISTS assignment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '作业标题',
    description TEXT COMMENT '作业描述',
    total_questions INT DEFAULT 0 COMMENT '题目数量',
    deadline DATE NOT NULL COMMENT '截止日期',
    is_completed BOOLEAN DEFAULT FALSE COMMENT '是否已完成',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    difficulty VARCHAR(20) COMMENT '难度等级',
    teacher_name VARCHAR(100) COMMENT '教师姓名',
    subject VARCHAR(100) COMMENT '科目',
    chapter VARCHAR(100) COMMENT '章节'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- 创建题目表
CREATE TABLE IF NOT EXISTS question (
    id INT PRIMARY KEY AUTO_INCREMENT,
    assignment_id INT COMMENT '所属作业ID，NULL表示未分配',
    question_text TEXT NOT NULL COMMENT '题目内容',
    answer INT NOT NULL COMMENT '正确答案',
    operator VARCHAR(10) COMMENT '运算符类型',
    FOREIGN KEY (assignment_id) REFERENCES assignment(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 创建作业提交表
CREATE TABLE IF NOT EXISTS submission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    assignment_id INT NOT NULL COMMENT '作业ID',
    student_id INT NOT NULL COMMENT '学生ID',
    student_name VARCHAR(100) NOT NULL COMMENT '学生姓名',
    submission_time DATETIME COMMENT '提交时间',
    score INT DEFAULT 0 COMMENT '得分',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending(未提交), completed(已完成), overdue(已逾期)',
    answers TEXT COMMENT '学生答案JSON字符串',
    FOREIGN KEY (assignment_id) REFERENCES assignment(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- 创建索引
CREATE INDEX idx_assignment_teacher ON assignment(teacher_name);
CREATE INDEX idx_assignment_deadline ON assignment(deadline);
CREATE INDEX idx_question_assignment ON question(assignment_id);
CREATE INDEX idx_submission_assignment ON submission(assignment_id);
CREATE INDEX idx_submission_student ON submission(student_id);

-- 插入测试数据
INSERT INTO assignment (title, description, total_questions, deadline, is_completed, created_at, difficulty, teacher_name, subject, chapter)
VALUES 
('基础加减法练习', '完成10道简单的加减法题目，巩固基础计算能力', 10, '2025-10-30', FALSE, '2025-10-20', 'easy', '张老师', '数学', '第一章 整数运算'),
('混合运算练习', '完成15道加减混合运算题目，提高计算灵活性', 15, '2025-11-05', FALSE, '2025-10-21', 'medium', '张老师', '数学', '第二章 混合运算'),
('进阶加减法挑战', '完成20道较难的加减法题目，锻炼计算速度', 20, '2025-11-10', TRUE, '2025-10-19', 'hard', '张老师', '数学', '第三章 进阶运算');

-- 插入题目测试数据
INSERT INTO question (assignment_id, question_text, answer, operator)
VALUES 
(1, '5 + 3 = ?', 8, '+'),
(1, '9 - 4 = ?', 5, '-'),
(1, '6 + 7 = ?', 13, '+'),
(1, '12 - 5 = ?', 7, '-'),
(1, '8 + 9 = ?', 17, '+'),
(1, '15 - 8 = ?', 7, '-'),
(1, '10 + 6 = ?', 16, '+'),
(1, '18 - 9 = ?', 9, '-'),
(1, '11 + 4 = ?', 15, '+'),
(1, '20 - 12 = ?', 8, '-'),
(NULL, '3 + 7 = ?', 10, '+'),
(NULL, '11 - 6 = ?', 5, '-');

-- 插入作业提交测试数据
INSERT INTO submission (assignment_id, student_id, student_name, submission_time, score, status)
VALUES 
(1, 1001, '张三', '2025-10-25 14:30:22', 90, 'completed'),
(1, 1002, '李四', '2025-10-26 09:15:45', 100, 'completed'),
(1, 1003, '王五', NULL, 0, 'pending'),
(1, 1004, '赵六', '2025-10-24 16:45:12', 85, 'completed'),
(1, 1005, '钱七', '2025-10-27 11:20:33', 95, 'completed');

-- 更新assignment表中的题目数量
UPDATE assignment SET total_questions = (SELECT COUNT(*) FROM question WHERE assignment_id = assignment.id);