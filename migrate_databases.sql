-- =============================================
-- 数据库拆分迁移脚本
-- 将 edu_admin 中的表按模块迁移到对应的数据库
-- =============================================

-- 使用方法：
-- mysql -h localhost -P 3306 -u root -p'123456dg' < migrate_databases.sql

-- =============================================
-- 1. 迁移系统模块表到 edu_system
-- =============================================

-- 系统表列表 (14张)
-- sys_campus, sys_user, sys_role, sys_menu, sys_user_role, sys_role_menu
-- sys_classroom, sys_dict_type, sys_dict_data, sys_config, sys_holiday
-- sys_login_log, sys_operation_log, sys_warning_config

CREATE TABLE IF NOT EXISTS edu_system.sys_campus LIKE edu_admin.sys_campus;
INSERT IGNORE INTO edu_system.sys_campus SELECT * FROM edu_admin.sys_campus;

CREATE TABLE IF NOT EXISTS edu_system.sys_user LIKE edu_admin.sys_user;
INSERT IGNORE INTO edu_system.sys_user SELECT * FROM edu_admin.sys_user;

CREATE TABLE IF NOT EXISTS edu_system.sys_role LIKE edu_admin.sys_role;
INSERT IGNORE INTO edu_system.sys_role SELECT * FROM edu_admin.sys_role;

CREATE TABLE IF NOT EXISTS edu_system.sys_menu LIKE edu_admin.sys_menu;
INSERT IGNORE INTO edu_system.sys_menu SELECT * FROM edu_admin.sys_menu;

CREATE TABLE IF NOT EXISTS edu_system.sys_user_role LIKE edu_admin.sys_user_role;
INSERT IGNORE INTO edu_system.sys_user_role SELECT * FROM edu_admin.sys_user_role;

CREATE TABLE IF NOT EXISTS edu_system.sys_role_menu LIKE edu_admin.sys_role_menu;
INSERT IGNORE INTO edu_system.sys_role_menu SELECT * FROM edu_admin.sys_role_menu;

CREATE TABLE IF NOT EXISTS edu_system.sys_classroom LIKE edu_admin.sys_classroom;
INSERT IGNORE INTO edu_system.sys_classroom SELECT * FROM edu_admin.sys_classroom;

CREATE TABLE IF NOT EXISTS edu_system.sys_dict_type LIKE edu_admin.sys_dict_type;
INSERT IGNORE INTO edu_system.sys_dict_type SELECT * FROM edu_admin.sys_dict_type;

CREATE TABLE IF NOT EXISTS edu_system.sys_dict_data LIKE edu_admin.sys_dict_data;
INSERT IGNORE INTO edu_system.sys_dict_data SELECT * FROM edu_admin.sys_dict_data;

CREATE TABLE IF NOT EXISTS edu_system.sys_config LIKE edu_admin.sys_config;
INSERT IGNORE INTO edu_system.sys_config SELECT * FROM edu_admin.sys_config;

CREATE TABLE IF NOT EXISTS edu_system.sys_holiday LIKE edu_admin.sys_holiday;
INSERT IGNORE INTO edu_system.sys_holiday SELECT * FROM edu_admin.sys_holiday;

CREATE TABLE IF NOT EXISTS edu_system.sys_login_log LIKE edu_admin.sys_login_log;
INSERT IGNORE INTO edu_system.sys_login_log SELECT * FROM edu_admin.sys_login_log;

CREATE TABLE IF NOT EXISTS edu_system.sys_operation_log LIKE edu_admin.sys_operation_log;
INSERT IGNORE INTO edu_system.sys_operation_log SELECT * FROM edu_admin.sys_operation_log;

CREATE TABLE IF NOT EXISTS edu_system.sys_warning_config LIKE edu_admin.sys_warning_config;
INSERT IGNORE INTO edu_system.sys_warning_config SELECT * FROM edu_admin.sys_warning_config;

-- =============================================
-- 2. 迁移学员模块表到 edu_student
-- =============================================

-- 学员表列表 (4张)
-- stu_student, stu_contact, stu_tag, stu_student_tag

CREATE TABLE IF NOT EXISTS edu_student.stu_student LIKE edu_admin.stu_student;
INSERT IGNORE INTO edu_student.stu_student SELECT * FROM edu_admin.stu_student;

CREATE TABLE IF NOT EXISTS edu_student.stu_contact LIKE edu_admin.stu_contact;
INSERT IGNORE INTO edu_student.stu_contact SELECT * FROM edu_admin.stu_contact;

CREATE TABLE IF NOT EXISTS edu_student.stu_tag LIKE edu_admin.stu_tag;
INSERT IGNORE INTO edu_student.stu_tag SELECT * FROM edu_admin.stu_tag;

CREATE TABLE IF NOT EXISTS edu_student.stu_student_tag LIKE edu_admin.stu_student_tag;
INSERT IGNORE INTO edu_student.stu_student_tag SELECT * FROM edu_admin.stu_student_tag;

-- =============================================
-- 3. 迁移教学模块表到 edu_teaching
-- =============================================

-- 教学表列表 (23张)
CREATE TABLE IF NOT EXISTS edu_teaching.tch_teacher LIKE edu_admin.tch_teacher;
INSERT IGNORE INTO edu_teaching.tch_teacher SELECT * FROM edu_admin.tch_teacher;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_teacher_certificate LIKE edu_admin.tch_teacher_certificate;
INSERT IGNORE INTO edu_teaching.tch_teacher_certificate SELECT * FROM edu_admin.tch_teacher_certificate;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_teacher_available_time LIKE edu_admin.tch_teacher_available_time;
INSERT IGNORE INTO edu_teaching.tch_teacher_available_time SELECT * FROM edu_admin.tch_teacher_available_time;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_teacher_salary LIKE edu_admin.tch_teacher_salary;
INSERT IGNORE INTO edu_teaching.tch_teacher_salary SELECT * FROM edu_admin.tch_teacher_salary;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_teacher_salary_config LIKE edu_admin.tch_teacher_salary_config;
INSERT IGNORE INTO edu_teaching.tch_teacher_salary_config SELECT * FROM edu_admin.tch_teacher_salary_config;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_teacher_status_log LIKE edu_admin.tch_teacher_status_log;
INSERT IGNORE INTO edu_teaching.tch_teacher_status_log SELECT * FROM edu_admin.tch_teacher_status_log;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_teacher_attendance LIKE edu_admin.tch_teacher_attendance;
INSERT IGNORE INTO edu_teaching.tch_teacher_attendance SELECT * FROM edu_admin.tch_teacher_attendance;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_course LIKE edu_admin.tch_course;
INSERT IGNORE INTO edu_teaching.tch_course SELECT * FROM edu_admin.tch_course;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_course_category LIKE edu_admin.tch_course_category;
INSERT IGNORE INTO edu_teaching.tch_course_category SELECT * FROM edu_admin.tch_course_category;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_course_package LIKE edu_admin.tch_course_package;
INSERT IGNORE INTO edu_teaching.tch_course_package SELECT * FROM edu_admin.tch_course_package;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_course_package_item LIKE edu_admin.tch_course_package_item;
INSERT IGNORE INTO edu_teaching.tch_course_package_item SELECT * FROM edu_admin.tch_course_package_item;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_class LIKE edu_admin.tch_class;
INSERT IGNORE INTO edu_teaching.tch_class SELECT * FROM edu_admin.tch_class;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_class_student LIKE edu_admin.tch_class_student;
INSERT IGNORE INTO edu_teaching.tch_class_student SELECT * FROM edu_admin.tch_class_student;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_schedule LIKE edu_admin.tch_schedule;
INSERT IGNORE INTO edu_teaching.tch_schedule SELECT * FROM edu_admin.tch_schedule;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_attendance LIKE edu_admin.tch_attendance;
INSERT IGNORE INTO edu_teaching.tch_attendance SELECT * FROM edu_admin.tch_attendance;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_homework LIKE edu_admin.tch_homework;
INSERT IGNORE INTO edu_teaching.tch_homework SELECT * FROM edu_admin.tch_homework;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_homework_submit LIKE edu_admin.tch_homework_submit;
INSERT IGNORE INTO edu_teaching.tch_homework_submit SELECT * FROM edu_admin.tch_homework_submit;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_homework_comment LIKE edu_admin.tch_homework_comment;
INSERT IGNORE INTO edu_teaching.tch_homework_comment SELECT * FROM edu_admin.tch_homework_comment;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_leave_request LIKE edu_admin.tch_leave_request;
INSERT IGNORE INTO edu_teaching.tch_leave_request SELECT * FROM edu_admin.tch_leave_request;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_makeup_lesson LIKE edu_admin.tch_makeup_lesson;
INSERT IGNORE INTO edu_teaching.tch_makeup_lesson SELECT * FROM edu_admin.tch_makeup_lesson;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_price_strategy LIKE edu_admin.tch_price_strategy;
INSERT IGNORE INTO edu_teaching.tch_price_strategy SELECT * FROM edu_admin.tch_price_strategy;

CREATE TABLE IF NOT EXISTS edu_teaching.tch_price_strategy_rule LIKE edu_admin.tch_price_strategy_rule;
INSERT IGNORE INTO edu_teaching.tch_price_strategy_rule SELECT * FROM edu_admin.tch_price_strategy_rule;

-- =============================================
-- 4. 迁移财务模块表到 edu_finance
-- =============================================

-- 财务表列表 (12张)
CREATE TABLE IF NOT EXISTS edu_finance.fin_contract LIKE edu_admin.fin_contract;
INSERT IGNORE INTO edu_finance.fin_contract SELECT * FROM edu_admin.fin_contract;

CREATE TABLE IF NOT EXISTS edu_finance.fin_contract_item LIKE edu_admin.fin_contract_item;
INSERT IGNORE INTO edu_finance.fin_contract_item SELECT * FROM edu_admin.fin_contract_item;

CREATE TABLE IF NOT EXISTS edu_finance.fin_contract_approval LIKE edu_admin.fin_contract_approval;
INSERT IGNORE INTO edu_finance.fin_contract_approval SELECT * FROM edu_admin.fin_contract_approval;

CREATE TABLE IF NOT EXISTS edu_finance.fin_contract_approval_config LIKE edu_admin.fin_contract_approval_config;
INSERT IGNORE INTO edu_finance.fin_contract_approval_config SELECT * FROM edu_admin.fin_contract_approval_config;

CREATE TABLE IF NOT EXISTS edu_finance.fin_contract_approval_flow LIKE edu_admin.fin_contract_approval_flow;
INSERT IGNORE INTO edu_finance.fin_contract_approval_flow SELECT * FROM edu_admin.fin_contract_approval_flow;

CREATE TABLE IF NOT EXISTS edu_finance.fin_contract_print_record LIKE edu_admin.fin_contract_print_record;
INSERT IGNORE INTO edu_finance.fin_contract_print_record SELECT * FROM edu_admin.fin_contract_print_record;

CREATE TABLE IF NOT EXISTS edu_finance.fin_contract_print_template LIKE edu_admin.fin_contract_print_template;
INSERT IGNORE INTO edu_finance.fin_contract_print_template SELECT * FROM edu_admin.fin_contract_print_template;

CREATE TABLE IF NOT EXISTS edu_finance.fin_payment LIKE edu_admin.fin_payment;
INSERT IGNORE INTO edu_finance.fin_payment SELECT * FROM edu_admin.fin_payment;

CREATE TABLE IF NOT EXISTS edu_finance.fin_refund LIKE edu_admin.fin_refund;
INSERT IGNORE INTO edu_finance.fin_refund SELECT * FROM edu_admin.fin_refund;

CREATE TABLE IF NOT EXISTS edu_finance.fin_class_hour_account LIKE edu_admin.fin_class_hour_account;
INSERT IGNORE INTO edu_finance.fin_class_hour_account SELECT * FROM edu_admin.fin_class_hour_account;

CREATE TABLE IF NOT EXISTS edu_finance.fin_class_hour_record LIKE edu_admin.fin_class_hour_record;
INSERT IGNORE INTO edu_finance.fin_class_hour_record SELECT * FROM edu_admin.fin_class_hour_record;

CREATE TABLE IF NOT EXISTS edu_finance.fin_class_hour_rule LIKE edu_admin.fin_class_hour_rule;
INSERT IGNORE INTO edu_finance.fin_class_hour_rule SELECT * FROM edu_admin.fin_class_hour_rule;

-- =============================================
-- 5. 迁移营销和通知模块表到 edu_marketing
-- =============================================

-- 营销表列表 (7张)
CREATE TABLE IF NOT EXISTS edu_marketing.mkt_lead LIKE edu_admin.mkt_lead;
INSERT IGNORE INTO edu_marketing.mkt_lead SELECT * FROM edu_admin.mkt_lead;

CREATE TABLE IF NOT EXISTS edu_marketing.mkt_follow_up LIKE edu_admin.mkt_follow_up;
INSERT IGNORE INTO edu_marketing.mkt_follow_up SELECT * FROM edu_admin.mkt_follow_up;

CREATE TABLE IF NOT EXISTS edu_marketing.mkt_trial_lesson LIKE edu_admin.mkt_trial_lesson;
INSERT IGNORE INTO edu_marketing.mkt_trial_lesson SELECT * FROM edu_admin.mkt_trial_lesson;

CREATE TABLE IF NOT EXISTS edu_marketing.mkt_coupon LIKE edu_admin.mkt_coupon;
INSERT IGNORE INTO edu_marketing.mkt_coupon SELECT * FROM edu_admin.mkt_coupon;

CREATE TABLE IF NOT EXISTS edu_marketing.mkt_coupon_record LIKE edu_admin.mkt_coupon_record;
INSERT IGNORE INTO edu_marketing.mkt_coupon_record SELECT * FROM edu_admin.mkt_coupon_record;

CREATE TABLE IF NOT EXISTS edu_marketing.mkt_coupon_rule LIKE edu_admin.mkt_coupon_rule;
INSERT IGNORE INTO edu_marketing.mkt_coupon_rule SELECT * FROM edu_admin.mkt_coupon_rule;

-- 通知表列表 (5张)
CREATE TABLE IF NOT EXISTS edu_marketing.msg_template LIKE edu_admin.msg_template;
INSERT IGNORE INTO edu_marketing.msg_template SELECT * FROM edu_admin.msg_template;

CREATE TABLE IF NOT EXISTS edu_marketing.msg_notification LIKE edu_admin.msg_notification;
INSERT IGNORE INTO edu_marketing.msg_notification SELECT * FROM edu_admin.msg_notification;

CREATE TABLE IF NOT EXISTS edu_marketing.msg_user_message LIKE edu_admin.msg_user_message;
INSERT IGNORE INTO edu_marketing.msg_user_message SELECT * FROM edu_admin.msg_user_message;

CREATE TABLE IF NOT EXISTS edu_marketing.sys_notification_log LIKE edu_admin.sys_notification_log;
INSERT IGNORE INTO edu_marketing.sys_notification_log SELECT * FROM edu_admin.sys_notification_log;

CREATE TABLE IF NOT EXISTS edu_marketing.sys_notification_rule LIKE edu_admin.sys_notification_rule;
INSERT IGNORE INTO edu_marketing.sys_notification_rule SELECT * FROM edu_admin.sys_notification_rule;

-- =============================================
-- 迁移完成提示
-- =============================================
SELECT '数据迁移完成！' AS status;
SELECT 'edu_system 表数量:' AS info, COUNT(*) AS count FROM information_schema.tables WHERE table_schema = 'edu_system';
SELECT 'edu_student 表数量:' AS info, COUNT(*) AS count FROM information_schema.tables WHERE table_schema = 'edu_student';
SELECT 'edu_teaching 表数量:' AS info, COUNT(*) AS count FROM information_schema.tables WHERE table_schema = 'edu_teaching';
SELECT 'edu_finance 表数量:' AS info, COUNT(*) AS count FROM information_schema.tables WHERE table_schema = 'edu_finance';
SELECT 'edu_marketing 表数量:' AS info, COUNT(*) AS count FROM information_schema.tables WHERE table_schema = 'edu_marketing';
