-- 测试课程包数据
-- 注意：这是测试数据，生产环境请勿执行

-- 插入测试课程包数据
INSERT INTO tch_course_package (id, name, package_code, description, cover_image, price, original_price, discount, valid_days, total_class_hours, status, campus_id, sort_order, create_time, update_time, deleted)
VALUES
(1, '数学基础套餐', 'PKG000001', '包含小学数学基础课程，适合1-3年级学生', 'https://example.com/math-basic.jpg', 2980.00, 3500.00, 8.5, 365, 48, 1, NULL, 1, NOW(), NOW(), 0),
(2, '英语启蒙套餐', 'PKG000002', '英语启蒙课程包，包含自然拼读和基础口语', 'https://example.com/english-starter.jpg', 3980.00, 4800.00, 8.3, 365, 60, 1, NULL, 2, NOW(), NOW(), 0),
(3, '编程入门套餐', 'PKG000003', 'Scratch编程入门，培养逻辑思维', 'https://example.com/coding-intro.jpg', 4980.00, 6000.00, 8.3, 180, 40, 1, NULL, 3, NOW(), NOW(), 0),
(4, '艺术培养套餐', 'PKG000004', '美术+音乐综合艺术课程', 'https://example.com/art-package.jpg', 5980.00, 7200.00, 8.3, 365, 72, 0, NULL, 4, NOW(), NOW(), 0);

-- 插入课程包明细数据（假设课程ID 1-10已存在）
INSERT INTO tch_course_package_item (id, package_id, course_id, course_count, sort_order, create_time, update_time, deleted)
VALUES
-- 数学基础套餐
(1, 1, 1, 24, 1, NOW(), NOW(), 0),
(2, 1, 2, 24, 2, NOW(), NOW(), 0),
-- 英语启蒙套餐
(3, 2, 3, 30, 1, NOW(), NOW(), 0),
(4, 2, 4, 30, 2, NOW(), NOW(), 0),
-- 编程入门套餐
(5, 3, 5, 40, 1, NOW(), NOW(), 0),
-- 艺术培养套餐
(6, 4, 6, 36, 1, NOW(), NOW(), 0),
(7, 4, 7, 36, 2, NOW(), NOW(), 0);
