-- 教育机构学生管理系统 - 教师考勤表
-- V1.0.18__add_teacher_attendance_table.sql

-- 教师考勤表
CREATE TABLE IF NOT EXISTS tch_teacher_attendance (
    id BIGINT NOT NULL COMMENT 'ID',
    schedule_id BIGINT NOT NULL COMMENT '排课ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    class_id BIGINT COMMENT '班级ID',
    sign_in_time DATETIME COMMENT '签到时间',
    sign_out_time DATETIME COMMENT '签退时间',
    status VARCHAR(20) DEFAULT 'absent' COMMENT '状态：present-出勤，absent-缺勤，late-迟到，early_leave-早退，leave-请假',
    is_late TINYINT DEFAULT 0 COMMENT '是否迟到',
    is_early_leave TINYINT DEFAULT 0 COMMENT '是否早退',
    late_minutes INT DEFAULT 0 COMMENT '迟到分钟数',
    early_leave_minutes INT DEFAULT 0 COMMENT '早退分钟数',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_schedule_teacher (schedule_id, teacher_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_class_id (class_id),
    KEY idx_status (status),
    KEY idx_sign_in_time (sign_in_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师考勤表';
