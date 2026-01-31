-- 为课程分类表添加缺失字段
-- 添加图标字段
ALTER TABLE tch_course_category ADD COLUMN IF NOT EXISTS icon VARCHAR(100) COMMENT '图标' AFTER sort_order;

-- 添加描述字段
ALTER TABLE tch_course_category ADD COLUMN IF NOT EXISTS description VARCHAR(500) COMMENT '描述' AFTER icon;

-- 添加校区ID字段
ALTER TABLE tch_course_category ADD COLUMN IF NOT EXISTS campus_id BIGINT COMMENT '校区ID（null表示全部校区可用）' AFTER description;

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_parent_id ON tch_course_category(parent_id);
CREATE INDEX IF NOT EXISTS idx_campus_id ON tch_course_category(campus_id);
CREATE INDEX IF NOT EXISTS idx_status ON tch_course_category(status);
