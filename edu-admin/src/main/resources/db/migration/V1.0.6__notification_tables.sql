-- 教育机构学生管理系统 - 消息通知模块表
-- V1.0.6__notification_tables.sql

-- =============================================
-- 消息模板表
-- =============================================

CREATE TABLE IF NOT EXISTS msg_template (
    id BIGINT NOT NULL COMMENT 'ID',
    code VARCHAR(50) NOT NULL COMMENT '模板编码',
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    type VARCHAR(20) COMMENT '消息类型：system-系统通知，class-上课提醒，homework-作业通知，payment-缴费提醒，activity-活动通知',
    channel VARCHAR(20) COMMENT '发送渠道：site-站内信，sms-短信，wechat-微信，push-APP推送',
    title VARCHAR(200) COMMENT '模板标题',
    content TEXT COMMENT '模板内容（支持变量占位符）',
    sms_template_id VARCHAR(100) COMMENT '短信模板ID（第三方平台）',
    wechat_template_id VARCHAR(100) COMMENT '微信模板ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息模板表';

-- =============================================
-- 通知消息表
-- =============================================

CREATE TABLE IF NOT EXISTS msg_notification (
    id BIGINT NOT NULL COMMENT 'ID',
    title VARCHAR(200) COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    type VARCHAR(20) COMMENT '消息类型：system-系统通知，class-上课提醒，homework-作业通知，payment-缴费提醒，activity-活动通知',
    channel VARCHAR(20) COMMENT '发送渠道：site-站内信，sms-短信，wechat-微信，push-APP推送',
    receiver_type VARCHAR(20) COMMENT '接收人类型：all-全部，student-学员，parent-家长，teacher-教师，user-指定用户',
    receiver_id BIGINT COMMENT '接收人ID',
    campus_id BIGINT COMMENT '校区ID',
    biz_type VARCHAR(50) COMMENT '关联业务类型',
    biz_id BIGINT COMMENT '关联业务ID',
    send_status VARCHAR(20) DEFAULT 'pending' COMMENT '发送状态：pending-待发送，sent-已发送，failed-发送失败',
    send_time DATETIME COMMENT '发送时间',
    scheduled_time DATETIME COMMENT '定时发送时间',
    sender_id BIGINT COMMENT '发送人ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_type (type),
    KEY idx_channel (channel),
    KEY idx_send_status (send_status),
    KEY idx_campus_id (campus_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知消息表';

-- =============================================
-- 用户消息表（站内信收件箱）
-- =============================================

CREATE TABLE IF NOT EXISTS msg_user_message (
    id BIGINT NOT NULL COMMENT 'ID',
    notification_id BIGINT NOT NULL COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_type VARCHAR(20) DEFAULT 'user' COMMENT '用户类型：user-系统用户，student-学员，parent-家长',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读',
    read_time DATETIME COMMENT '阅读时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（用户删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_notification_id (notification_id),
    KEY idx_user_id (user_id),
    KEY idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户消息表';

-- =============================================
-- 初始化消息模板数据
-- =============================================

INSERT INTO msg_template (id, code, name, type, channel, title, content, status) VALUES
(1, 'CLASS_REMINDER', '上课提醒', 'class', 'site', '上课提醒', '亲爱的${studentName}同学，您有一节${courseName}课程将于${scheduleDate} ${startTime}开始，请准时到达${classroomName}教室。', 1),
(2, 'HOMEWORK_NOTICE', '作业通知', 'homework', 'site', '新作业通知', '亲爱的${studentName}同学，${teacherName}老师布置了新作业《${homeworkTitle}》，请在${deadline}前完成提交。', 1),
(3, 'PAYMENT_REMINDER', '缴费提醒', 'payment', 'site', '缴费提醒', '亲爱的家长，${studentName}同学的合同即将到期，请及时续费。', 1),
(4, 'ATTENDANCE_NOTICE', '考勤通知', 'class', 'site', '考勤通知', '${studentName}同学${scheduleDate}的${courseName}课程考勤状态为：${status}。', 1),
(5, 'LEAVE_APPROVED', '请假审批通知', 'system', 'site', '请假审批结果', '您的请假申请已${result}。${remark}', 1);
