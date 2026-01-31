-- 创建预警配置表
CREATE TABLE IF NOT EXISTS sys_warning_config (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    warning_type VARCHAR(50) NOT NULL COMMENT '预警类型：course_hour_low-课时不足，course_hour_expire-课时即将到期，overdue-欠费，contract_expire-合同即将到期，student_loss-学员流失，class_full-班级满员，schedule_conflict-排课冲突，classroom_conflict-教室冲突，trial_conversion_low-试听转化率低，income_abnormal-收入异常，refund_rate_high-退费率高',
    warning_name VARCHAR(100) NOT NULL COMMENT '预警名称',
    warning_level VARCHAR(20) NOT NULL COMMENT '预警级别：normal-正常，warning-警告，urgent-紧急',
    threshold_config TEXT NOT NULL COMMENT '阈值配置（JSON格式）',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    campus_id BIGINT COMMENT '校区ID（为空表示全局配置）',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
    INDEX idx_warning_type (warning_type),
    INDEX idx_campus_id (campus_id),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警配置表';

-- 插入默认预警配置
INSERT INTO sys_warning_config (id, warning_type, warning_name, warning_level, threshold_config, enabled, campus_id, remark, create_time, update_time, create_by, update_by, deleted)
VALUES
    (1, 'course_hour_low', '课时不足预警', 'warning', '{"courseHourThreshold":5}', 1, NULL, '当学员剩余课时低于5小时时触发预警', NOW(), NOW(), 1, 1, 0),
    (2, 'course_hour_expire', '课时即将到期预警', 'warning', '{"daysThreshold":30}', 1, NULL, '当课时距离到期不足30天时触发预警', NOW(), NOW(), 1, 1, 0),
    (3, 'overdue', '欠费预警', 'urgent', '{"daysThreshold":7}', 1, NULL, '当欠费天数超过7天时触发预警', NOW(), NOW(), 1, 1, 0),
    (4, 'contract_expire', '合同即将到期预警', 'warning', '{"daysThreshold":30}', 1, NULL, '当合同距离到期不足30天时触发预警', NOW(), NOW(), 1, 1, 0),
    (5, 'student_loss', '学员流失预警', 'urgent', '{"daysThreshold":30}', 1, NULL, '当学员超过30天未上课时触发预警', NOW(), NOW(), 1, 1, 0),
    (6, 'class_full', '班级满员预警', 'normal', '{}', 1, NULL, '当班级报名人数达到容量上限时触发预警', NOW(), NOW(), 1, 1, 0),
    (7, 'schedule_conflict', '教师排课冲突预警', 'urgent', '{}', 1, NULL, '当教师存在排课时间冲突时触发预警', NOW(), NOW(), 1, 1, 0),
    (8, 'classroom_conflict', '教室使用冲突预警', 'urgent', '{}', 1, NULL, '当教室存在使用时间冲突时触发预警', NOW(), NOW(), 1, 1, 0),
    (9, 'trial_conversion_low', '试听转化率低预警', 'warning', '{"rateThreshold":0.3}', 1, NULL, '当试听转化率低于30%时触发预警', NOW(), NOW(), 1, 1, 0),
    (10, 'income_abnormal', '收入异常预警', 'warning', '{"rateThreshold":0.8}', 1, NULL, '当本月收入低于上月80%时触发预警', NOW(), NOW(), 1, 1, 0),
    (11, 'refund_rate_high', '退费率高预警', 'urgent', '{"rateThreshold":0.1}', 1, NULL, '当退费率高于10%时触发预警', NOW(), NOW(), 1, 1, 0);
