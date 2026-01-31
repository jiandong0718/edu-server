-- 教师资质证书表
-- V1.0.9__add_teacher_certificate_table.sql

-- 教师资质证书表
CREATE TABLE IF NOT EXISTS tch_teacher_certificate (
    id BIGINT NOT NULL COMMENT '证书ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    cert_name VARCHAR(100) NOT NULL COMMENT '证书名称',
    cert_no VARCHAR(100) COMMENT '证书编号',
    cert_type VARCHAR(50) NOT NULL COMMENT '证书类型：teacher_qualification-教师资格证，degree-学历证书，skill-技能证书，other-其他',
    issue_org VARCHAR(200) COMMENT '颁发机构',
    issue_date DATE COMMENT '颁发日期',
    expire_date DATE COMMENT '有效期至（永久有效可为空）',
    file_url VARCHAR(500) NOT NULL COMMENT '证书文件URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    campus_id BIGINT COMMENT '校区ID（用于数据隔离）',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_cert_type (cert_type),
    KEY idx_campus_id (campus_id),
    KEY idx_expire_date (expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师资质证书表';

-- 教师可用时间表（用于排课参考）
CREATE TABLE IF NOT EXISTS tch_teacher_available_time (
    id BIGINT NOT NULL COMMENT 'ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    day_of_week TINYINT NOT NULL COMMENT '星期几：1-周一，2-周二，...，7-周日',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_day_of_week (day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师可用时间表';

-- 教师薪资表
CREATE TABLE IF NOT EXISTS tch_teacher_salary (
    id BIGINT NOT NULL COMMENT 'ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    salary_month VARCHAR(7) NOT NULL COMMENT '薪资月份（格式：YYYY-MM）',
    base_salary DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
    class_hours INT DEFAULT 0 COMMENT '课时数',
    hour_rate DECIMAL(10,2) DEFAULT 0 COMMENT '课时费单价',
    hour_salary DECIMAL(10,2) DEFAULT 0 COMMENT '课时费',
    bonus DECIMAL(10,2) DEFAULT 0 COMMENT '奖金',
    deduction DECIMAL(10,2) DEFAULT 0 COMMENT '扣款',
    total_salary DECIMAL(10,2) DEFAULT 0 COMMENT '应发工资',
    actual_salary DECIMAL(10,2) DEFAULT 0 COMMENT '实发工资',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审核，approved-已审核，paid-已发放',
    pay_date DATE COMMENT '发放日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_teacher_month (teacher_id, salary_month),
    KEY idx_salary_month (salary_month),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师薪资表';
