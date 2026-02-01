-- 教育机构学生管理系统 - 登录安全增强
-- V1.0.7__add_login_security.sql

-- 添加用户表的登录安全字段
ALTER TABLE sys_user
ADD COLUMN login_fail_count INT DEFAULT 0 COMMENT '登录失败次数' AFTER last_login_ip,
ADD COLUMN lock_time DATETIME COMMENT '锁定时间' AFTER login_fail_count,
ADD COLUMN is_first_login TINYINT DEFAULT 1 COMMENT '是否首次登录：0-否，1-是' AFTER lock_time,
ADD COLUMN password_update_time DATETIME COMMENT '密码修改时间' AFTER is_first_login;

-- 注意：sys_config 表已移至 V1.0.23__add_sys_config_table.sql 统一创建
