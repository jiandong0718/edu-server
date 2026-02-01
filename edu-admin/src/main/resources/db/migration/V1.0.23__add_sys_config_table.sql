-- 系统参数配置表
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    config_key VARCHAR(100) NOT NULL COMMENT '参数键',
    config_value TEXT COMMENT '参数值',
    config_type VARCHAR(20) DEFAULT 'string' COMMENT '参数类型：string/number/boolean/json',
    config_group VARCHAR(50) COMMENT '参数分组',
    description VARCHAR(500) COMMENT '参数说明',
    is_system TINYINT(1) DEFAULT 0 COMMENT '是否系统内置：0-否，1-是',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数配置表';

-- 插入默认系统配置
INSERT INTO sys_config (config_key, config_value, config_type, config_group, description, is_system, sort, status, create_time, update_time, deleted) VALUES
-- 基础配置
('system.name', '教育机构管理系统', 'string', 'basic', '系统名称', 1, 1, 1, NOW(), NOW(), 0),
('system.version', '1.0.0', 'string', 'basic', '系统版本', 1, 2, 1, NOW(), NOW(), 0),
('system.copyright', 'Copyright © 2026 教育机构', 'string', 'basic', '版权信息', 1, 3, 1, NOW(), NOW(), 0),
('system.logo', '', 'string', 'basic', '系统Logo URL', 0, 4, 1, NOW(), NOW(), 0),
('system.favicon', '', 'string', 'basic', '系统Favicon URL', 0, 5, 1, NOW(), NOW(), 0),

-- 安全配置
('security.password.min.length', '6', 'number', 'security', '密码最小长度', 1, 1, 1, NOW(), NOW(), 0),
('security.password.max.length', '20', 'number', 'security', '密码最大长度', 1, 2, 1, NOW(), NOW(), 0),
('security.password.require.special', 'false', 'boolean', 'security', '密码是否需要特殊字符', 0, 3, 1, NOW(), NOW(), 0),
('security.password.require.number', 'true', 'boolean', 'security', '密码是否需要数字', 0, 4, 1, NOW(), NOW(), 0),
('security.password.require.uppercase', 'false', 'boolean', 'security', '密码是否需要大写字母', 0, 5, 1, NOW(), NOW(), 0),
('security.login.max.fail.count', '5', 'number', 'security', '登录最大失败次数', 1, 6, 1, NOW(), NOW(), 0),
('security.login.lock.duration', '30', 'number', 'security', '账号锁定时长（分钟）', 1, 7, 1, NOW(), NOW(), 0),
('security.session.timeout', '120', 'number', 'security', '会话超时时间（分钟）', 1, 8, 1, NOW(), NOW(), 0),
('security.jwt.expire.time', '7200', 'number', 'security', 'JWT过期时间（秒）', 1, 9, 1, NOW(), NOW(), 0),
('login.force.change.password', 'true', 'boolean', 'security', '首次登录是否强制修改密码', 1, 10, 1, NOW(), NOW(), 0),

-- 业务配置
('business.student.id.prefix', 'STU', 'string', 'business', '学员编号前缀', 0, 1, 1, NOW(), NOW(), 0),
('business.contract.id.prefix', 'CON', 'string', 'business', '合同编号前缀', 0, 2, 1, NOW(), NOW(), 0),
('business.class.max.students', '30', 'number', 'business', '班级最大学员数', 0, 3, 1, NOW(), NOW(), 0),
('business.course.hour.unit', '45', 'number', 'business', '课时单位时长（分钟）', 0, 4, 1, NOW(), NOW(), 0),
('business.refund.max.days', '7', 'number', 'business', '退费最大天数', 0, 5, 1, NOW(), NOW(), 0),
('business.contract.auto.approve', 'false', 'boolean', 'business', '合同是否自动审批', 0, 6, 1, NOW(), NOW(), 0),
('business.attendance.advance.days', '7', 'number', 'business', '考勤提前天数', 0, 7, 1, NOW(), NOW(), 0),

-- 通知配置
('notification.sms.enabled', 'false', 'boolean', 'notification', '是否启用短信通知', 0, 1, 1, NOW(), NOW(), 0),
('notification.email.enabled', 'false', 'boolean', 'notification', '是否启用邮件通知', 0, 2, 1, NOW(), NOW(), 0),
('notification.wechat.enabled', 'false', 'boolean', 'notification', '是否启用微信通知', 0, 3, 1, NOW(), NOW(), 0),
('notification.system.enabled', 'true', 'boolean', 'notification', '是否启用系统通知', 1, 4, 1, NOW(), NOW(), 0),

-- 文件配置
('file.upload.max.size', '10', 'number', 'file', '文件上传最大大小（MB）', 0, 1, 1, NOW(), NOW(), 0),
('file.upload.allowed.types', '["jpg","jpeg","png","gif","pdf","doc","docx","xls","xlsx"]', 'json', 'file', '允许上传的文件类型', 0, 2, 1, NOW(), NOW(), 0),
('file.storage.type', 'local', 'string', 'file', '文件存储类型：local/oss', 0, 3, 1, NOW(), NOW(), 0),
('file.storage.path', '/data/files', 'string', 'file', '本地存储路径', 0, 4, 1, NOW(), NOW(), 0);
