-- 添加学员表字段：身份证号和地址
-- V1.0.10__add_student_fields.sql

-- 添加身份证号字段
ALTER TABLE stu_student ADD COLUMN id_card VARCHAR(18) COMMENT '身份证号' AFTER phone;

-- 添加地址字段
ALTER TABLE stu_student ADD COLUMN address VARCHAR(255) COMMENT '地址' AFTER advisor_id;

-- 为身份证号添加索引（用于查重）
CREATE INDEX idx_id_card ON stu_student(id_card);
