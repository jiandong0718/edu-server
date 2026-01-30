-- 教育机构学生管理系统 - 考勤扩展表
-- V1.0.4__attendance_tables.sql

-- =============================================
-- 请假申请表
-- =============================================

CREATE TABLE IF NOT EXISTS tch_leave_request (
    id BIGINT NOT NULL COMMENT 'ID',
    leave_no VARCHAR(50) NOT NULL COMMENT '请假单号',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    schedule_id BIGINT COMMENT '排课ID（针对特定课节请假）',
    class_id BIGINT COMMENT '班级ID',
    campus_id BIGINT COMMENT '校区ID',
    type VARCHAR(20) DEFAULT 'single' COMMENT '请假类型：single-单次请假，period-时段请假',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    reason VARCHAR(500) COMMENT '请假原因',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审批，approved-已批准，rejected-已拒绝，cancelled-已取消',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    need_makeup TINYINT DEFAULT 0 COMMENT '是否需要补课',
    makeup_schedule_id BIGINT COMMENT '补课排课ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_leave_no (leave_no),
    KEY idx_student_id (student_id),
    KEY idx_schedule_id (schedule_id),
    KEY idx_class_id (class_id),
    KEY idx_campus_id (campus_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假申请表';

-- =============================================
-- 补课记录表
-- =============================================

CREATE TABLE IF NOT EXISTS tch_makeup_lesson (
    id BIGINT NOT NULL COMMENT 'ID',
    leave_request_id BIGINT COMMENT '请假申请ID',
    original_schedule_id BIGINT COMMENT '原排课ID',
    makeup_schedule_id BIGINT COMMENT '补课排课ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    campus_id BIGINT COMMENT '校区ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待补课，completed-已完成，cancelled-已取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_leave_request_id (leave_request_id),
    KEY idx_original_schedule_id (original_schedule_id),
    KEY idx_makeup_schedule_id (makeup_schedule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='补课记录表';

-- =============================================
-- 教师考勤表
-- =============================================

CREATE TABLE IF NOT EXISTS tch_teacher_attendance (
    id BIGINT NOT NULL COMMENT 'ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    schedule_id BIGINT COMMENT '排课ID',
    campus_id BIGINT COMMENT '校区ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    sign_in_time DATETIME COMMENT '签到时间',
    sign_out_time DATETIME COMMENT '签退时间',
    status VARCHAR(20) DEFAULT 'absent' COMMENT '状态：present-出勤，absent-缺勤，late-迟到，early_leave-早退，leave-请假',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_schedule_id (schedule_id),
    KEY idx_attendance_date (attendance_date),
    KEY idx_campus_id (campus_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师考勤表';
