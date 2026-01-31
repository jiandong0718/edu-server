-- 添加课程上下架状态字段
-- V1.0.26__add_course_sale_status.sql

-- 修改课程表，添加上下架相关字段
ALTER TABLE tch_course
    MODIFY COLUMN status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，ON_SALE-在售，OFF_SALE-已下架',
    ADD COLUMN on_sale_time DATETIME COMMENT '上架时间',
    ADD COLUMN off_sale_time DATETIME COMMENT '下架时间';

-- 更新现有数据：将 status=1 的改为 ON_SALE，status=0 的改为 OFF_SALE
UPDATE tch_course SET status = 'ON_SALE' WHERE status = '1';
UPDATE tch_course SET status = 'OFF_SALE' WHERE status = '0';

-- 添加索引以提高查询性能
CREATE INDEX idx_status_on_sale_time ON tch_course(status, on_sale_time);
