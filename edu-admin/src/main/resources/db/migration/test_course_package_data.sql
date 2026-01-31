-- 测试数据：课程包和价格策略
-- test_course_package_data.sql

-- 插入测试课程包数据
INSERT INTO tch_course_package (id, name, description, price, original_price, valid_days, status, campus_id, sort_order, create_time, update_time, create_by, update_by, deleted) VALUES
(1, '数学基础课程包', '包含小学数学基础课程，适合1-3年级学生', 2800.00, 3500.00, 365, 1, NULL, 1, NOW(), NOW(), 1, 1, 0),
(2, '英语启蒙课程包', '包含英语启蒙课程，适合幼儿园至小学低年级', 3200.00, 4000.00, 365, 1, NULL, 2, NOW(), NOW(), 1, 1, 0),
(3, '暑期特训营', '暑期数学+英语综合课程包', 4500.00, 6000.00, 90, 1, NULL, 3, NOW(), NOW(), 1, 1, 0);

-- 插入课程包明细数据（假设课程ID 1-5已存在）
INSERT INTO tch_course_package_item (id, package_id, course_id, course_count, sort_order, create_time, update_time, create_by, update_by, deleted) VALUES
-- 数学基础课程包明细
(1, 1, 1, 20, 1, NOW(), NOW(), 1, 1, 0),
(2, 1, 2, 15, 2, NOW(), NOW(), 1, 1, 0),
-- 英语启蒙课程包明细
(3, 2, 3, 25, 1, NOW(), NOW(), 1, 1, 0),
(4, 2, 4, 20, 2, NOW(), NOW(), 1, 1, 0),
-- 暑期特训营明细
(5, 3, 1, 15, 1, NOW(), NOW(), 1, 1, 0),
(6, 3, 3, 15, 2, NOW(), NOW(), 1, 1, 0),
(7, 3, 5, 10, 3, NOW(), NOW(), 1, 1, 0);

-- 插入价格策略数据
INSERT INTO tch_price_strategy (id, name, type, target_id, target_type, discount_type, discount_value, start_date, end_date, student_type, status, campus_id, create_time, update_time, create_by, update_by, deleted) VALUES
-- 暑期促销策略（时间段策略）
(1, '暑期促销8折', 'time_period', 1, 'package', 'percentage', 80.00, '2026-07-01', '2026-08-31', NULL, 1, NULL, NOW(), NOW(), 1, 1, 0),
(2, '暑期课程9折', 'time_period', 1, 'course', 'percentage', 90.00, '2026-07-01', '2026-08-31', NULL, 1, NULL, NOW(), NOW(), 1, 1, 0),
-- 新生优惠策略（学员类型策略）
(3, '新生专享9折', 'student_type', 2, 'package', 'percentage', 90.00, '2026-01-01', '2026-12-31', 'new', 1, NULL, NOW(), NOW(), 1, 1, 0),
(4, '老生续费95折', 'student_type', 2, 'package', 'percentage', 95.00, '2026-01-01', '2026-12-31', 'old', 1, NULL, NOW(), NOW(), 1, 1, 0),
-- 固定金额优惠策略
(5, '开学季立减500', 'time_period', 3, 'package', 'fixed', 500.00, '2026-09-01', '2026-09-30', NULL, 1, NULL, NOW(), NOW(), 1, 1, 0);
