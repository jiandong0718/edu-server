-- 增强排课表：添加代课和停课相关字段
-- V1.0.14__enhance_schedule_for_substitute_and_cancel.sql

-- 添加代课相关字段
ALTER TABLE tch_schedule
    ADD COLUMN original_teacher_id BIGINT COMMENT '原教师ID（代课时记录）' AFTER teacher_id,
    ADD COLUMN substitute_reason VARCHAR(500) COMMENT '代课原因' AFTER original_teacher_id;

-- 添加停课相关字段
ALTER TABLE tch_schedule
    ADD COLUMN cancel_reason VARCHAR(500) COMMENT '停课原因' AFTER substitute_reason,
    ADD COLUMN need_makeup TINYINT DEFAULT 0 COMMENT '是否需要补课' AFTER cancel_reason,
    ADD COLUMN makeup_date DATE COMMENT '补课日期' AFTER need_makeup,
    ADD COLUMN makeup_start_time TIME COMMENT '补课开始时间' AFTER makeup_date,
    ADD COLUMN makeup_end_time TIME COMMENT '补课结束时间' AFTER makeup_start_time;

-- 添加索引以提高查询性能
ALTER TABLE tch_schedule
    ADD INDEX idx_original_teacher_id (original_teacher_id),
    ADD INDEX idx_makeup_date (makeup_date);
