-- 教育机构学生管理系统 - 作业模块表
-- V1.0.5__homework_tables.sql

-- =============================================
-- 作业表
-- =============================================

CREATE TABLE IF NOT EXISTS tch_homework (
    id BIGINT NOT NULL COMMENT 'ID',
    title VARCHAR(200) NOT NULL COMMENT '作业标题',
    content TEXT COMMENT '作业内容',
    class_id BIGINT COMMENT '班级ID',
    course_id BIGINT COMMENT '课程ID',
    schedule_id BIGINT COMMENT '排课ID（关联到具体课节）',
    teacher_id BIGINT COMMENT '教师ID',
    campus_id BIGINT COMMENT '校区ID',
    type VARCHAR(20) DEFAULT 'practice' COMMENT '作业类型：practice-练习，test-测验，project-项目',
    deadline DATETIME COMMENT '截止时间',
    attachments TEXT COMMENT '附件URL（JSON数组）',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿，published-已发布，closed-已截止',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_class_id (class_id),
    KEY idx_course_id (course_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_campus_id (campus_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业表';

-- =============================================
-- 作业提交表
-- =============================================

CREATE TABLE IF NOT EXISTS tch_homework_submit (
    id BIGINT NOT NULL COMMENT 'ID',
    homework_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    content TEXT COMMENT '提交内容',
    attachments TEXT COMMENT '附件URL（JSON数组）',
    submit_time DATETIME COMMENT '提交时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待批改，reviewed-已批改，returned-已退回',
    score INT COMMENT '评分（0-100）',
    grade VARCHAR(10) COMMENT '评级：A/B/C/D/E',
    comment TEXT COMMENT '教师点评',
    reviewer_id BIGINT COMMENT '批改人ID',
    review_time DATETIME COMMENT '批改时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_homework_student (homework_id, student_id),
    KEY idx_student_id (student_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业提交表';
