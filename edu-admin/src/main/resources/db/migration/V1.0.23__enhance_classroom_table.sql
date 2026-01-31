-- 增强教室表字段
-- V1.0.23__enhance_classroom_table.sql

-- 添加教室表缺失字段
ALTER TABLE sys_classroom
    ADD COLUMN IF NOT EXISTS `code` VARCHAR(50) COMMENT '教室编码' AFTER `name`,
    ADD COLUMN IF NOT EXISTS `building` VARCHAR(50) COMMENT '所属楼栋' AFTER `campus_id`,
    ADD COLUMN IF NOT EXISTS `floor` INT COMMENT '楼层' AFTER `building`,
    ADD COLUMN IF NOT EXISTS `room_no` VARCHAR(20) COMMENT '房间号' AFTER `floor`,
    ADD COLUMN IF NOT EXISTS `area` DECIMAL(10,2) COMMENT '面积(平方米)' AFTER `capacity`,
    ADD COLUMN IF NOT EXISTS `facilities` VARCHAR(500) COMMENT '设施配置(JSON数组)' AFTER `area`;

-- 更新 equipment 字段为 facilities (如果数据存在)
UPDATE sys_classroom SET facilities = equipment WHERE equipment IS NOT NULL AND facilities IS NULL;

-- 为教室编码添加唯一索引
ALTER TABLE sys_classroom ADD UNIQUE INDEX uk_code (code);

-- 为楼层添加索引
ALTER TABLE sys_classroom ADD INDEX idx_floor (floor);
