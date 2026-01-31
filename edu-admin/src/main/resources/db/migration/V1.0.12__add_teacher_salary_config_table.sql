-- 教师课酬配置表
-- V1.0.12__add_teacher_salary_config_table.sql

-- 创建教师课酬配置表
CREATE TABLE IF NOT EXISTS tch_teacher_salary_config (
    id BIGINT NOT NULL COMMENT '课酬配置ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    course_id BIGINT COMMENT '课程ID（为空表示通用配置）',
    class_type VARCHAR(20) COMMENT '班级类型：one_to_one-一对一，small_class-小班课，large_class-大班课',
    salary_type VARCHAR(20) NOT NULL COMMENT '课酬类型：per_hour-按课时，per_class-按课次，fixed-固定',
    amount DECIMAL(10,2) NOT NULL COMMENT '课酬金额',
    effective_date DATE NOT NULL COMMENT '生效日期',
    expiry_date DATE COMMENT '失效日期（为空表示长期有效）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_course_id (course_id),
    KEY idx_class_type (class_type),
    KEY idx_effective_date (effective_date),
    KEY idx_expiry_date (expiry_date),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师课酬配置表';

-- 插入示例数据
INSERT INTO tch_teacher_salary_config (id, teacher_id, course_id, class_type, salary_type, amount, effective_date, status, remark)
VALUES
    (1, 1, NULL, 'one_to_one', 'per_hour', 200.00, '2024-01-01', 1, '一对一课程默认课酬'),
    (2, 1, NULL, 'small_class', 'per_hour', 150.00, '2024-01-01', 1, '小班课默认课酬'),
    (3, 1, NULL, 'large_class', 'per_hour', 100.00, '2024-01-01', 1, '大班课默认课酬'),
    (4, 2, NULL, 'one_to_one', 'per_hour', 180.00, '2024-01-01', 1, '一对一课程默认课酬'),
    (5, 2, NULL, 'small_class', 'per_hour', 130.00, '2024-01-01', 1, '小班课默认课酬');
