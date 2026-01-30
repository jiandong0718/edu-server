-- 教育机构学生管理系统 - 初始化数据
-- V1.0.1__init_data.sql

-- 初始化超级管理员角色
INSERT INTO sys_role (id, name, code, data_scope, status, sort_order, remark) VALUES
(1, '超级管理员', 'super_admin', 1, 1, 1, '拥有所有权限'),
(2, '校区管理员', 'campus_admin', 2, 1, 2, '管理本校区数据'),
(3, '教务老师', 'teacher', 2, 1, 3, '教务管理权限'),
(4, '销售顾问', 'sales', 2, 1, 4, '招生销售权限');

-- 初始化默认校区
INSERT INTO sys_campus (id, name, code, address, status, sort_order) VALUES
(1, '总部校区', 'HQ', '默认地址', 1, 1);

-- 初始化超级管理员用户 (密码: admin123，使用BCrypt加密)
INSERT INTO sys_user (id, username, password, real_name, phone, status, campus_id) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', '13800000000', 1, 1);

-- 关联超级管理员角色
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(1, 1, 1);

-- 初始化菜单
INSERT INTO sys_menu (id, parent_id, name, path, component, permission, icon, type, visible, status, sort_order) VALUES
-- 系统管理
(1, 0, '系统管理', '/system', NULL, NULL, 'setting', 1, 1, 1, 1),
(101, 1, '用户管理', '/system/user', 'system/user/index', 'system:user:list', 'user', 2, 1, 1, 1),
(102, 1, '角色管理', '/system/role', 'system/role/index', 'system:role:list', 'peoples', 2, 1, 1, 2),
(103, 1, '菜单管理', '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 2, 1, 1, 3),
(104, 1, '校区管理', '/system/campus', 'system/campus/index', 'system:campus:list', 'office-building', 2, 1, 1, 4),
(105, 1, '字典管理', '/system/dict', 'system/dict/index', 'system:dict:list', 'dict', 2, 1, 1, 5),
(106, 1, '操作日志', '/system/log', 'system/log/index', 'system:log:list', 'log', 2, 1, 1, 6),

-- 学生管理
(2, 0, '学生管理', '/student', NULL, NULL, 'peoples', 1, 1, 1, 2),
(201, 2, '学生列表', '/student/list', 'student/list/index', 'student:list', 'user', 2, 1, 1, 1),
(202, 2, '学生档案', '/student/profile', 'student/profile/index', 'student:profile:list', 'documentation', 2, 1, 1, 2),

-- 教学管理
(3, 0, '教学管理', '/teaching', NULL, NULL, 'education', 1, 1, 1, 3),
(301, 3, '课程管理', '/teaching/course', 'teaching/course/index', 'teaching:course:list', 'skill', 2, 1, 1, 1),
(302, 3, '班级管理', '/teaching/class', 'teaching/class/index', 'teaching:class:list', 'tree', 2, 1, 1, 2),
(303, 3, '排课管理', '/teaching/schedule', 'teaching/schedule/index', 'teaching:schedule:list', 'date', 2, 1, 1, 3),
(304, 3, '考勤管理', '/teaching/attendance', 'teaching/attendance/index', 'teaching:attendance:list', 'checkbox', 2, 1, 1, 4),

-- 财务管理
(4, 0, '财务管理', '/finance', NULL, NULL, 'money', 1, 1, 1, 4),
(401, 4, '合同管理', '/finance/contract', 'finance/contract/index', 'finance:contract:list', 'form', 2, 1, 1, 1),
(402, 4, '收费管理', '/finance/payment', 'finance/payment/index', 'finance:payment:list', 'shopping', 2, 1, 1, 2),
(403, 4, '课时消耗', '/finance/consumption', 'finance/consumption/index', 'finance:consumption:list', 'chart', 2, 1, 1, 3),

-- 招生管理
(5, 0, '招生管理', '/marketing', NULL, NULL, 'guide', 1, 1, 1, 5),
(501, 5, '线索管理', '/marketing/lead', 'marketing/lead/index', 'marketing:lead:list', 'phone', 2, 1, 1, 1),
(502, 5, '跟进记录', '/marketing/follow', 'marketing/follow/index', 'marketing:follow:list', 'message', 2, 1, 1, 2),
(503, 5, '试听管理', '/marketing/trial', 'marketing/trial/index', 'marketing:trial:list', 'star-off', 2, 1, 1, 3);

-- 关联超级管理员所有菜单权限
INSERT INTO sys_role_menu (id, role_id, menu_id)
SELECT id, 1, id FROM sys_menu;

-- 初始化字典类型
INSERT INTO sys_dict_type (id, name, code, status) VALUES
(1, '性别', 'gender', 1),
(2, '状态', 'status', 1),
(3, '学生状态', 'student_status', 1),
(4, '线索来源', 'lead_source', 1),
(5, '线索状态', 'lead_status', 1),
(6, '合同状态', 'contract_status', 1),
(7, '支付方式', 'payment_method', 1),
(8, '课程类型', 'course_type', 1);

-- 初始化字典数据
INSERT INTO sys_dict_data (id, dict_type_id, label, value, sort_order) VALUES
-- 性别
(101, 1, '未知', '0', 1),
(102, 1, '男', '1', 2),
(103, 1, '女', '2', 3),
-- 状态
(201, 2, '禁用', '0', 1),
(202, 2, '启用', '1', 2),
-- 学生状态
(301, 3, '潜在', 'potential', 1),
(302, 3, '试听', 'trial', 2),
(303, 3, '在读', 'enrolled', 3),
(304, 3, '休学', 'suspended', 4),
(305, 3, '结业', 'graduated', 5),
(306, 3, '退费', 'refunded', 6),
-- 线索来源
(401, 4, '地推', 'offline', 1),
(402, 4, '转介绍', 'referral', 2),
(403, 4, '线上广告', 'online_ad', 3),
(404, 4, '自然到访', 'walk_in', 4),
(405, 4, '电话咨询', 'phone', 5),
-- 线索状态
(501, 5, '新线索', 'new', 1),
(502, 5, '跟进中', 'following', 2),
(503, 5, '已预约', 'appointed', 3),
(504, 5, '已试听', 'trialed', 4),
(505, 5, '已成交', 'converted', 5),
(506, 5, '已流失', 'lost', 6),
-- 合同状态
(601, 6, '待签署', 'pending', 1),
(602, 6, '已签署', 'signed', 2),
(603, 6, '已完成', 'completed', 3),
(604, 6, '已退费', 'refunded', 4),
(605, 6, '已作废', 'cancelled', 5),
-- 支付方式
(701, 7, '微信支付', 'wechat', 1),
(702, 7, '支付宝', 'alipay', 2),
(703, 7, '银联', 'unionpay', 3),
(704, 7, '现金', 'cash', 4),
(705, 7, 'POS机', 'pos', 5),
-- 课程类型
(801, 8, '一对一', 'one_to_one', 1),
(802, 8, '小班课', 'small_class', 2),
(803, 8, '大班课', 'large_class', 3);
