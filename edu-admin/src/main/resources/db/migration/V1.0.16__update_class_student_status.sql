-- 更新班级学员状态字段，添加 graduated 状态
-- 用于支持班级升班和结业功能

-- 修改 tch_class_student 表的 status 字段注释
ALTER TABLE tch_class_student MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active-在读，left-已退出，graduated-已结业';

-- 添加索引以提高查询性能
CREATE INDEX idx_class_student_status ON tch_class_student(class_id, status);
CREATE INDEX idx_student_class_status ON tch_class_student(student_id, status);
