-- ============================================================================
-- 教师状态管理功能
-- 版本: V1.0.20
-- 描述: 添加教师状态变更记录表，完善教师状态管理
-- ============================================================================

-- 创建教师状态变更记录表
CREATE TABLE IF NOT EXISTS tch_teacher_status_log (
    id BIGINT NOT NULL COMMENT '主键ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    teacher_name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    teacher_no VARCHAR(50) NOT NULL COMMENT '教师编号',
    from_status VARCHAR(20) COMMENT '原状态：active-在职，on_leave-休假，resigned-离职',
    to_status VARCHAR(20) NOT NULL COMMENT '新状态：active-在职，on_leave-休假，resigned-离职',
    reason VARCHAR(500) NOT NULL COMMENT '变更原因',
    effective_date DATE NOT NULL COMMENT '生效日期',
    expected_return_date DATE COMMENT '预计返回日期（休假时填写）',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    campus_id BIGINT COMMENT '校区ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
    PRIMARY KEY (id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_teacher_name (teacher_name),
    INDEX idx_to_status (to_status),
    INDEX idx_effective_date (effective_date),
    INDEX idx_campus_id (campus_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师状态变更记录表';

-- 更新教师表中的旧状态值 'leave' 为新状态值 'on_leave'
UPDATE tch_teacher SET status = 'on_leave' WHERE status = 'leave';

-- 更新教师表的状态字段注释，统一使用下划线命名
ALTER TABLE tch_teacher
    MODIFY COLUMN status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-在职，on_leave-休假，resigned-离职';

-- 为教师表的状态字段添加索引（如果不存在）
ALTER TABLE tch_teacher
    ADD INDEX IF NOT EXISTS idx_status (status);
