-- 通知规则表
CREATE TABLE IF NOT EXISTS `sys_notification_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `rule_code` VARCHAR(50) NOT NULL COMMENT '规则编码',
    `description` VARCHAR(500) COMMENT '规则描述',
    `event_type` VARCHAR(50) NOT NULL COMMENT '事件类型：STUDENT_REGISTER-学员注册, CONTRACT_SIGNED-合同签署, PAYMENT_SUCCESS-支付成功, CLASS_REMIND-上课提醒, ATTENDANCE_ABSENT-缺勤, CLASS_HOUR_LOW-课时不足, TRIAL_LESSON-试听预约, CONTRACT_EXPIRE-合同到期',
    `trigger_condition` TEXT COMMENT '触发条件，JSON格式',
    `notification_type` VARCHAR(50) NOT NULL COMMENT '通知类型：SMS-短信, EMAIL-邮件, WECHAT-微信, SYSTEM-站内信，多个用逗号分隔',
    `template_id` BIGINT COMMENT '消息模板ID',
    `receiver_type` VARCHAR(50) NOT NULL COMMENT '接收人类型：STUDENT-学员, PARENT-家长, TEACHER-教师, ADVISOR-顾问, ADMIN-管理员，多个用逗号分隔',
    `send_time_type` VARCHAR(20) NOT NULL DEFAULT 'IMMEDIATE' COMMENT '发送时间类型：IMMEDIATE-立即, SCHEDULED-定时, DELAYED-延迟',
    `send_time_config` VARCHAR(500) COMMENT '发送时间配置，JSON格式',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用, INACTIVE-禁用',
    `priority` INT NOT NULL DEFAULT 0 COMMENT '优先级，数字越大优先级越高',
    `campus_id` BIGINT COMMENT '校区ID，null表示全部校区',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rule_code` (`rule_code`, `deleted`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_status` (`status`),
    KEY `idx_campus_id` (`campus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知规则表';

-- 插入默认规则
INSERT INTO `sys_notification_rule` (`rule_name`, `rule_code`, `description`, `event_type`, `trigger_condition`, `notification_type`, `receiver_type`, `send_time_type`, `status`, `priority`) VALUES
('学员注册欢迎通知', 'STUDENT_REGISTER_WELCOME', '学员注册成功后发送欢迎通知', 'STUDENT_REGISTER', NULL, 'SYSTEM,SMS', 'STUDENT', 'IMMEDIATE', 'ACTIVE', 10),
('合同签署通知', 'CONTRACT_SIGNED_NOTIFY', '合同签署成功后通知学员和顾问', 'CONTRACT_SIGNED', NULL, 'SYSTEM,SMS', 'STUDENT,ADVISOR', 'IMMEDIATE', 'ACTIVE', 10),
('支付成功通知', 'PAYMENT_SUCCESS_NOTIFY', '支付成功后通知学员', 'PAYMENT_SUCCESS', NULL, 'SYSTEM,SMS', 'STUDENT', 'IMMEDIATE', 'ACTIVE', 10),
('上课提醒', 'CLASS_REMIND_NOTIFY', '上课前30分钟提醒学员和教师', 'CLASS_REMIND', '{"type":"DELAYED","delay":30,"unit":"MINUTES","beforeEvent":true}', 'SYSTEM,SMS', 'STUDENT,TEACHER', 'DELAYED', 'ACTIVE', 20),
('缺勤通知', 'ATTENDANCE_ABSENT_NOTIFY', '学员缺勤后通知顾问', 'ATTENDANCE_ABSENT', NULL, 'SYSTEM', 'ADVISOR', 'IMMEDIATE', 'ACTIVE', 10),
('课时不足提醒', 'CLASS_HOUR_LOW_NOTIFY', '剩余课时不足5节时提醒学员和顾问', 'CLASS_HOUR_LOW', '{"type":"AND","conditions":[{"field":"remainingClassHours","operator":"<=","value":5}]}', 'SYSTEM,SMS', 'STUDENT,ADVISOR', 'IMMEDIATE', 'ACTIVE', 15),
('试听预约通知', 'TRIAL_LESSON_NOTIFY', '试听预约成功后通知学员和教师', 'TRIAL_LESSON', NULL, 'SYSTEM,SMS', 'STUDENT,TEACHER', 'IMMEDIATE', 'ACTIVE', 10),
('合同到期提醒', 'CONTRACT_EXPIRE_NOTIFY', '合同到期前7天提醒学员和顾问', 'CONTRACT_EXPIRE', '{"type":"DELAYED","delay":7,"unit":"DAYS","beforeEvent":true}', 'SYSTEM,SMS', 'STUDENT,ADVISOR', 'DELAYED', 'ACTIVE', 15);
