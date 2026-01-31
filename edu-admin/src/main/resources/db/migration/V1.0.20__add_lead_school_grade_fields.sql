-- 为线索表添加学校和年级字段
ALTER TABLE mkt_lead
    ADD COLUMN school VARCHAR(100) COMMENT '就读学校' AFTER age,
    ADD COLUMN grade VARCHAR(50) COMMENT '年级' AFTER school;
