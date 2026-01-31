-- 教师课酬配置测试数据
-- test_teacher_salary_config_data.sql

-- 清空测试数据
DELETE FROM tch_teacher_salary_config WHERE id >= 100;

-- 插入测试数据
-- 教师1的课酬配置
INSERT INTO tch_teacher_salary_config (id, teacher_id, course_id, class_type, salary_type, amount, effective_date, expiry_date, status, remark)
VALUES
    -- 通用配置（按班级类型）
    (100, 1, NULL, 'one_to_one', 'per_hour', 200.00, '2024-01-01', NULL, 1, '一对一课程默认课酬'),
    (101, 1, NULL, 'small_class', 'per_hour', 150.00, '2024-01-01', NULL, 1, '小班课默认课酬'),
    (102, 1, NULL, 'large_class', 'per_hour', 100.00, '2024-01-01', NULL, 1, '大班课默认课酬'),

    -- 特定课程配置（假设课程ID为1的是数学课）
    (103, 1, 1, 'one_to_one', 'per_hour', 250.00, '2024-01-01', NULL, 1, '数学一对一课程特殊课酬'),
    (104, 1, 1, 'small_class', 'per_hour', 180.00, '2024-01-01', NULL, 1, '数学小班课特殊课酬'),

    -- 历史配置（已失效）
    (105, 1, NULL, 'one_to_one', 'per_hour', 180.00, '2023-01-01', '2023-12-31', 1, '2023年一对一课程课酬'),
    (106, 1, NULL, 'small_class', 'per_hour', 130.00, '2023-01-01', '2023-12-31', 1, '2023年小班课课酬');

-- 教师2的课酬配置
INSERT INTO tch_teacher_salary_config (id, teacher_id, course_id, class_type, salary_type, amount, effective_date, expiry_date, status, remark)
VALUES
    -- 通用配置
    (110, 2, NULL, 'one_to_one', 'per_hour', 180.00, '2024-01-01', NULL, 1, '一对一课程默认课酬'),
    (111, 2, NULL, 'small_class', 'per_hour', 130.00, '2024-01-01', NULL, 1, '小班课默认课酬'),
    (112, 2, NULL, 'large_class', 'per_hour', 90.00, '2024-01-01', NULL, 1, '大班课默认课酬'),

    -- 特定课程配置（假设课程ID为2的是英语课）
    (113, 2, 2, 'one_to_one', 'per_hour', 220.00, '2024-01-01', NULL, 1, '英语一对一课程特殊课酬'),
    (114, 2, 2, 'small_class', 'per_hour', 160.00, '2024-01-01', NULL, 1, '英语小班课特殊课酬');

-- 教师3的课酬配置（按课次计费）
INSERT INTO tch_teacher_salary_config (id, teacher_id, course_id, class_type, salary_type, amount, effective_date, expiry_date, status, remark)
VALUES
    (120, 3, NULL, 'one_to_one', 'per_class', 300.00, '2024-01-01', NULL, 1, '一对一课程按课次计费'),
    (121, 3, NULL, 'small_class', 'per_class', 200.00, '2024-01-01', NULL, 1, '小班课按课次计费'),
    (122, 3, NULL, 'large_class', 'per_class', 150.00, '2024-01-01', NULL, 1, '大班课按课次计费');

-- 教师4的课酬配置（固定薪资）
INSERT INTO tch_teacher_salary_config (id, teacher_id, course_id, class_type, salary_type, amount, effective_date, expiry_date, status, remark)
VALUES
    (130, 4, NULL, NULL, 'fixed', 8000.00, '2024-01-01', NULL, 1, '固定月薪');

-- 查询验证
SELECT
    tsc.id,
    t.name AS teacher_name,
    c.name AS course_name,
    tsc.class_type,
    tsc.salary_type,
    tsc.amount,
    tsc.effective_date,
    tsc.expiry_date,
    tsc.status,
    tsc.remark
FROM tch_teacher_salary_config tsc
LEFT JOIN tch_teacher t ON tsc.teacher_id = t.id
LEFT JOIN tch_course c ON tsc.course_id = c.id
WHERE tsc.id >= 100
ORDER BY tsc.teacher_id, tsc.effective_date DESC;
