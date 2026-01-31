-- 增强课程包表
-- V1.0.24__enhance_course_package_table.sql

-- 添加课程包编码和封面图片字段
ALTER TABLE tch_course_package
ADD COLUMN package_code VARCHAR(50) COMMENT '课程包编码' AFTER name,
ADD COLUMN cover_image VARCHAR(500) COMMENT '封面图片URL' AFTER description,
ADD COLUMN total_class_hours INT DEFAULT 0 COMMENT '总课时数' AFTER valid_days,
ADD COLUMN discount DECIMAL(5, 2) COMMENT '折扣（如8.5表示8.5折）' AFTER original_price;

-- 添加唯一索引
ALTER TABLE tch_course_package
ADD UNIQUE INDEX uk_package_code (package_code);

-- 更新现有数据的课程包编码（如果有数据的话）
UPDATE tch_course_package
SET package_code = CONCAT('PKG', LPAD(id, 6, '0'))
WHERE package_code IS NULL OR package_code = '';
