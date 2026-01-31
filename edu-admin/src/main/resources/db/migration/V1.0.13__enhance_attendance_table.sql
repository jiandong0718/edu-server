-- 教育机构学生管理系统 - 考勤表增强
-- V1.0.13__enhance_attendance_table.sql

-- 为考勤表添加班级ID字段，方便查询和统计
ALTER TABLE tch_attendance ADD COLUMN class_id BIGINT COMMENT '班级ID' AFTER student_id;

-- 添加索引以提升查询性能
ALTER TABLE tch_attendance ADD INDEX idx_class_id (class_id);

-- 从排课表同步班级ID数据到考勤表
UPDATE tch_attendance a
INNER JOIN tch_schedule s ON a.schedule_id = s.id
SET a.class_id = s.class_id
WHERE a.class_id IS NULL;
