-- 教育机构学生管理系统 - 登录安全增强
-- V1.0.7__add_login_security.sql

-- 添加用户表的登录安全字段
ALTER TABLE sys_user
ADD COLUMN login_fail_count INT DEFAULT 0 COMMENT '登录失败次数' AFTER last_login_ip,
ADD COLUMN lock_time DATETIME COMMENT '锁定时间' AFTER login_fail_count,
ADD COLUMN is_first_login TINYINT DEFAULT 1 COMMENT '是否首次登录：0-否，1-是' AFTER lock_time,
ADD COLUMN password_update_time DATETIME COMMENT '密码修改时间' AFTER is_first_login;

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT NOT NULL COMMENT '配置ID',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value VARCHAR(500) NOT NULL COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'string' COMMENT '配置类型：string-字符串，number-数字，boolean-布尔，json-JSON',
    category VARCHAR(50) COMMENT '配置分类',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统内置：0-否，1-是',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 插入默认系统配置
INSERT INTO sys_config (id, config_name, config_key, config_value, config_type, category, is_system, remark) VALUES
(1, '登录失败锁定次数', 'login.fail.lock.count', '5', 'number', 'security', 1, '登录失败多少次后锁定账号'),
(2, '登录锁定时长（分钟）', 'login.lock.duration', '30', 'number', 'security', 1, '账号锁定时长，单位：分钟'),
(3, '密码最小长度', 'password.min.length', '6', 'number', 'security', 1, '密码最小长度要求'),
(4, '是否强制首次登录修改密码', 'login.force.change.password', 'true', 'boolean', 'security', 1, '首次登录是否强制修改密码'),
(5, '系统名称', 'system.name', '教育机构学生管理系统', 'string', 'basic', 1, '系统名称'),
(6, '系统版本', 'system.version', '1.0.0', 'string', 'basic', 1, '系统版本号');
