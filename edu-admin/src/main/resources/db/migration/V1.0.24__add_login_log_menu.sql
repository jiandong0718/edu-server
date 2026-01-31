-- 教育机构学生管理系统 - 添加登录日志菜单
-- V1.0.24__add_login_log_menu.sql

-- 添加登录日志菜单（假设系统管理的parent_id为8）
INSERT INTO sys_menu (id, parent_id, name, path, component, permission, icon, type, visible, status, sort_order, remark, create_time, update_time, deleted)
VALUES
(8007, 8, '登录日志', '/system/login-log', 'system/LoginLog', 'system:loginLog:list', 'SafetyOutlined', 2, 1, 1, 7, '登录日志管理', NOW(), NOW(), 0);

-- 添加登录日志按钮权限
INSERT INTO sys_menu (id, parent_id, name, path, component, permission, icon, type, visible, status, sort_order, remark, create_time, update_time, deleted)
VALUES
(80071, 8007, '查询', NULL, NULL, 'system:loginLog:query', NULL, 3, 1, 1, 1, '登录日志查询', NOW(), NOW(), 0),
(80072, 8007, '删除', NULL, NULL, 'system:loginLog:delete', NULL, 3, 1, 1, 2, '登录日志删除', NOW(), NOW(), 0),
(80073, 8007, '清空', NULL, NULL, 'system:loginLog:clear', NULL, 3, 1, 1, 3, '登录日志清空', NOW(), NOW(), 0);
