-- 节假日管理测试数据
-- 用于测试节假日管理功能

-- 清空测试数据（如果需要）
-- DELETE FROM sys_holiday WHERE id > 100;

-- 插入更多测试数据

-- 2026年调休工作日示例
INSERT INTO sys_holiday (id, name, type, start_date, end_date, description, campus_id, is_workday, status) VALUES
(101, '春节调休上班', 2, '2026-02-15', '2026-02-15', '春节前调休，周日上班', NULL, 1, 1),
(102, '春节调休上班', 2, '2026-02-28', '2026-02-28', '春节后调休，周六上班', NULL, 1, 1);

-- 校区特定假期示例（假设校区ID为1）
INSERT INTO sys_holiday (id, name, type, start_date, end_date, description, campus_id, is_workday, status) VALUES
(201, '校区周年庆', 3, '2026-03-15', '2026-03-15', '某校区成立周年纪念日', 1, 0, 1),
(202, '校区培训日', 3, '2026-06-20', '2026-06-20', '全体教师培训日', 1, 0, 1);

-- 2027年法定节假日（提前规划）
INSERT INTO sys_holiday (id, name, type, start_date, end_date, description, campus_id, is_workday, status) VALUES
(301, '2027年元旦', 1, '2027-01-01', '2027-01-03', '2027年元旦假期', NULL, 0, 1),
(302, '2027年春节', 1, '2027-02-06', '2027-02-12', '2027年春节假期', NULL, 0, 1),
(303, '2027年清明节', 1, '2027-04-03', '2027-04-05', '2027年清明节假期', NULL, 0, 1),
(304, '2027年劳动节', 1, '2027-05-01', '2027-05-03', '2027年劳动节假期', NULL, 0, 1),
(305, '2027年端午节', 1, '2027-06-14', '2027-06-16', '2027年端午节假期', NULL, 0, 1),
(306, '2027年中秋节', 1, '2027-09-21', '2027-09-23', '2027年中秋节假期', NULL, 0, 1),
(307, '2027年国庆节', 1, '2027-10-01', '2027-10-07', '2027年国庆节假期', NULL, 0, 1);

-- 查询验证
-- 查询所有节假日
SELECT * FROM sys_holiday WHERE deleted = 0 ORDER BY start_date;

-- 查询2026年的节假日
SELECT * FROM sys_holiday
WHERE deleted = 0
  AND start_date >= '2026-01-01'
  AND end_date <= '2026-12-31'
ORDER BY start_date;

-- 查询法定节假日
SELECT * FROM sys_holiday
WHERE deleted = 0
  AND type = 1
ORDER BY start_date;

-- 查询调休工作日
SELECT * FROM sys_holiday
WHERE deleted = 0
  AND type = 2
  AND is_workday = 1
ORDER BY start_date;

-- 查询某个校区的特定假期
SELECT * FROM sys_holiday
WHERE deleted = 0
  AND campus_id = 1
ORDER BY start_date;

-- 查询某个日期范围内的节假日（例如：2026年2月）
SELECT * FROM sys_holiday
WHERE deleted = 0
  AND status = 1
  AND start_date <= '2026-02-28'
  AND end_date >= '2026-02-01'
ORDER BY start_date;
