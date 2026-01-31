-- 教育机构学生管理系统 - 营销优惠券模块表结构
-- V1.0.28__add_coupon_tables.sql

-- =============================================
-- 优惠券模块表 (mkt_coupon_*)
-- =============================================

-- 优惠券模板表
CREATE TABLE IF NOT EXISTS mkt_coupon (
    id BIGINT NOT NULL COMMENT '优惠券ID',
    coupon_no VARCHAR(50) NOT NULL COMMENT '优惠券编号',
    name VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    type VARCHAR(20) NOT NULL COMMENT '优惠券类型：full_reduction-满减券，discount-折扣券，cash-代金券',
    discount_type VARCHAR(20) COMMENT '折扣类型：amount-金额，percent-百分比',
    discount_value DECIMAL(10,2) NOT NULL COMMENT '优惠值（金额或折扣百分比）',
    min_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额（满减条件）',
    max_discount_amount DECIMAL(10,2) COMMENT '最大优惠金额（折扣券封顶）',
    total_quantity INT NOT NULL COMMENT '发行总量',
    issued_quantity INT DEFAULT 0 COMMENT '已发放数量',
    used_quantity INT DEFAULT 0 COMMENT '已使用数量',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿，active-生效中，paused-已暂停，expired-已过期',
    valid_type VARCHAR(20) NOT NULL COMMENT '有效期类型：fixed-固定时间，relative-相对天数',
    valid_start_time DATETIME COMMENT '有效开始时间（固定时间）',
    valid_end_time DATETIME COMMENT '有效结束时间（固定时间）',
    valid_days INT COMMENT '有效天数（相对天数，从领取日起）',
    receive_type VARCHAR(20) DEFAULT 'manual' COMMENT '领取方式：manual-手动发放，auto-自动领取',
    receive_limit INT DEFAULT 1 COMMENT '每人限领数量',
    use_limit INT DEFAULT 1 COMMENT '每人限用数量',
    campus_ids VARCHAR(500) COMMENT '适用校区ID列表（逗号分隔，空表示全部）',
    description VARCHAR(500) COMMENT '优惠券描述',
    usage_rules TEXT COMMENT '使用规则说明',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_coupon_no (coupon_no),
    KEY idx_status (status),
    KEY idx_type (type),
    KEY idx_valid_time (valid_start_time, valid_end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券模板表';

-- 优惠券发放记录表
CREATE TABLE IF NOT EXISTS mkt_coupon_record (
    id BIGINT NOT NULL COMMENT 'ID',
    record_no VARCHAR(50) NOT NULL COMMENT '记录编号',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    coupon_no VARCHAR(50) COMMENT '优惠券编号',
    coupon_name VARCHAR(100) COMMENT '优惠券名称',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    student_name VARCHAR(50) COMMENT '学员姓名',
    campus_id BIGINT COMMENT '校区ID',
    status VARCHAR(20) DEFAULT 'unused' COMMENT '状态：unused-未使用，used-已使用，expired-已过期，invalid-已失效',
    receive_time DATETIME COMMENT '领取时间',
    receive_type VARCHAR(20) COMMENT '领取方式：manual-手动发放，auto-自动领取，system-系统赠送',
    valid_start_time DATETIME COMMENT '有效开始时间',
    valid_end_time DATETIME COMMENT '有效结束时间',
    use_time DATETIME COMMENT '使用时间',
    use_contract_id BIGINT COMMENT '使用合同ID',
    use_payment_id BIGINT COMMENT '使用收款ID',
    discount_amount DECIMAL(10,2) COMMENT '实际优惠金额',
    invalid_reason VARCHAR(200) COMMENT '失效原因',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_record_no (record_no),
    KEY idx_coupon_id (coupon_id),
    KEY idx_student_id (student_id),
    KEY idx_status (status),
    KEY idx_valid_time (valid_start_time, valid_end_time),
    KEY idx_use_contract (use_contract_id),
    KEY idx_use_payment (use_payment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券发放记录表';

-- 优惠券使用规则表
CREATE TABLE IF NOT EXISTS mkt_coupon_rule (
    id BIGINT NOT NULL COMMENT 'ID',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    rule_type VARCHAR(20) NOT NULL COMMENT '规则类型：course-适用课程，student_tag-适用学员标签，contract_type-适用合同类型',
    rule_value VARCHAR(500) NOT NULL COMMENT '规则值（课程ID、标签ID、合同类型等，多个用逗号分隔）',
    rule_name VARCHAR(200) COMMENT '规则名称（用于显示）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT COMMENT '创建人',
    PRIMARY KEY (id),
    KEY idx_coupon_id (coupon_id),
    KEY idx_rule_type (rule_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券使用规则表';

-- 为收款表添加优惠券字段
ALTER TABLE fin_payment
ADD COLUMN coupon_record_id BIGINT COMMENT '使用的优惠券记录ID' AFTER discount_amount,
ADD COLUMN coupon_discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠券优惠金额' AFTER coupon_record_id,
ADD KEY idx_coupon_record (coupon_record_id);

-- 为合同表添加优惠券字段
ALTER TABLE fin_contract
ADD COLUMN coupon_record_id BIGINT COMMENT '使用的优惠券记录ID' AFTER discount_amount,
ADD COLUMN coupon_discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠券优惠金额' AFTER coupon_record_id,
ADD KEY idx_coupon_record (coupon_record_id);
