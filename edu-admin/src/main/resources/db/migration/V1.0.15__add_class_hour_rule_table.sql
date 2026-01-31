-- 教育机构学生管理系统 - 课时消课规则表
-- V1.0.15__add_class_hour_rule_table.sql

-- 课时消课规则表
CREATE TABLE IF NOT EXISTS fin_class_hour_rule (
    id BIGINT NOT NULL COMMENT 'ID',
    name VARCHAR(100) NOT NULL COMMENT '规则名称',
    course_id BIGINT COMMENT '课程ID（为空表示通用规则）',
    class_type VARCHAR(50) COMMENT '班级类型：one_on_one-一对一，small_class-小班课，large_class-大班课',
    deduct_type VARCHAR(20) NOT NULL DEFAULT 'per_hour' COMMENT '扣减类型：per_hour-按课时，per_class-按课次，custom-自定义',
    deduct_amount DECIMAL(10,2) NOT NULL DEFAULT 1.00 COMMENT '扣减数量',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    campus_id BIGINT COMMENT '校区ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_course_id (course_id),
    KEY idx_campus_id (campus_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课时消课规则表';

-- 插入默认规则
INSERT INTO fin_class_hour_rule (id, name, course_id, class_type, deduct_type, deduct_amount, status, campus_id, remark, create_by)
VALUES
(1, '默认规则-按课时扣减', NULL, NULL, 'per_hour', 1.00, 'active', NULL, '默认按实际课时扣减', 1),
(2, '一对一课程规则', NULL, 'one_on_one', 'per_class', 1.00, 'active', NULL, '一对一课程按课次扣减', 1),
(3, '小班课规则', NULL, 'small_class', 'per_hour', 1.00, 'active', NULL, '小班课按课时扣减', 1),
(4, '大班课规则', NULL, 'large_class', 'per_hour', 0.50, 'active', NULL, '大班课按0.5课时扣减', 1);
