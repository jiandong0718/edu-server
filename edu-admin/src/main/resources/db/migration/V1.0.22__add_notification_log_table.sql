-- 教育机构学生管理系统 - 通知发送记录表
-- V1.0.22__add_notification_log_table.sql

-- =============================================
-- 通知发送记录表
-- =============================================

CREATE TABLE IF NOT EXISTS sys_notification_log (
    id BIGINT NOT NULL COMMENT 'ID',
    notification_id BIGINT COMMENT '通知ID',
    type VARCHAR(20) COMMENT '通知类型：sms-短信，site-站内信，email-邮件，wechat-微信，push-推送',
    receiver VARCHAR(100) COMMENT '接收人（手机号/邮箱/用户ID）',
    receiver_name VARCHAR(100) COMMENT '接收人姓名',
    receiver_id BIGINT COMMENT '接收人ID',
    title VARCHAR(200) COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '发送状态：pending-待发送，sending-发送中，success-成功，failed-失败',
    send_time DATETIME COMMENT '发送时间',
    fail_reason VARCHAR(500) COMMENT '失败原因',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    campus_id BIGINT COMMENT '校区ID',
    biz_type VARCHAR(50) COMMENT '业务类型',
    biz_id BIGINT COMMENT '业务ID',
    template_code VARCHAR(50) COMMENT '模板编码',
    third_party_id VARCHAR(100) COMMENT '第三方平台消息ID',
    cost DECIMAL(10, 4) COMMENT '发送成本（元）',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_notification_id (notification_id),
    KEY idx_type (type),
    KEY idx_status (status),
    KEY idx_receiver (receiver),
    KEY idx_receiver_id (receiver_id),
    KEY idx_send_time (send_time),
    KEY idx_campus_id (campus_id),
    KEY idx_biz_type_id (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知发送记录表';
