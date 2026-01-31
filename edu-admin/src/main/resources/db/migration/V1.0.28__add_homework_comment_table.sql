-- 教育机构学生管理系统 - 作业点评表
-- V1.0.28__add_homework_comment_table.sql

-- =============================================
-- 作业点评表
-- =============================================

CREATE TABLE IF NOT EXISTS tch_homework_comment (
    id BIGINT NOT NULL COMMENT 'ID',
    homework_submit_id BIGINT NOT NULL COMMENT '作业提交ID',
    homework_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    comment_type VARCHAR(20) DEFAULT 'review' COMMENT '点评类型：review-批改点评，reply-回复点评',
    content TEXT NOT NULL COMMENT '点评内容',
    attachments TEXT COMMENT '附件URL（JSON数组）',
    parent_id BIGINT COMMENT '父点评ID（用于回复）',
    is_public TINYINT DEFAULT 1 COMMENT '是否公开（1-公开，0-私密）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_homework_submit_id (homework_submit_id),
    KEY idx_homework_id (homework_id),
    KEY idx_student_id (student_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业点评表';
