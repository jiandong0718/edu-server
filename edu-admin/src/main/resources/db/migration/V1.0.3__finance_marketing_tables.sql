-- 教育机构学生管理系统 - 财务和营销模块表结构
-- V1.0.3__finance_marketing_tables.sql

-- =============================================
-- 财务模块表 (fin_)
-- =============================================

-- 合同表
CREATE TABLE IF NOT EXISTS fin_contract (
    id BIGINT NOT NULL COMMENT '合同ID',
    contract_no VARCHAR(50) NOT NULL COMMENT '合同编号',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    campus_id BIGINT NOT NULL COMMENT '校区ID',
    type VARCHAR(20) DEFAULT 'new' COMMENT '合同类型：new-新签，renew-续费，upgrade-升级',
    amount DECIMAL(10,2) NOT NULL COMMENT '合同金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    paid_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    received_amount DECIMAL(10,2) DEFAULT 0 COMMENT '已付金额',
    total_hours INT DEFAULT 0 COMMENT '总课时数',
    sign_date DATE COMMENT '签约日期',
    effective_date DATE COMMENT '生效日期',
    expire_date DATE COMMENT '到期日期',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待签署，signed-已签署，completed-已完成，refunded-已退费，cancelled-已作废',
    sales_id BIGINT COMMENT '销售顾问ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_contract_no (contract_no),
    KEY idx_student_id (student_id),
    KEY idx_campus_id (campus_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同表';

-- 合同明细表
CREATE TABLE IF NOT EXISTS fin_contract_item (
    id BIGINT NOT NULL COMMENT 'ID',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    course_name VARCHAR(100) COMMENT '课程名称',
    unit_price DECIMAL(10,2) COMMENT '单价',
    quantity INT DEFAULT 1 COMMENT '数量',
    hours INT DEFAULT 0 COMMENT '课时数',
    amount DECIMAL(10,2) COMMENT '金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同明细表';

-- 收款记录表
CREATE TABLE IF NOT EXISTS fin_payment (
    id BIGINT NOT NULL COMMENT 'ID',
    payment_no VARCHAR(50) NOT NULL COMMENT '收款单号',
    contract_id BIGINT COMMENT '合同ID',
    student_id BIGINT COMMENT '学员ID',
    campus_id BIGINT COMMENT '校区ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '收款金额',
    payment_method VARCHAR(20) COMMENT '支付方式：wechat-微信，alipay-支付宝，unionpay-银联，cash-现金，pos-POS机',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '支付状态：pending-待支付，paid-已支付，failed-支付失败，refunded-已退款',
    pay_time DATETIME COMMENT '支付时间',
    transaction_no VARCHAR(100) COMMENT '第三方交易号',
    receiver_id BIGINT COMMENT '收款人ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_no (payment_no),
    KEY idx_contract_id (contract_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收款记录表';

-- 退费记录表
CREATE TABLE IF NOT EXISTS fin_refund (
    id BIGINT NOT NULL COMMENT 'ID',
    refund_no VARCHAR(50) NOT NULL COMMENT '退费单号',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    campus_id BIGINT COMMENT '校区ID',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退费金额',
    refund_hours INT DEFAULT 0 COMMENT '退费课时',
    refund_reason VARCHAR(500) COMMENT '退费原因',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审批，approved-已审批，rejected-已拒绝，completed-已完成',
    apply_time DATETIME COMMENT '申请时间',
    approve_time DATETIME COMMENT '审批时间',
    approver_id BIGINT COMMENT '审批人ID',
    complete_time DATETIME COMMENT '完成时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_refund_no (refund_no),
    KEY idx_contract_id (contract_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退费记录表';

-- 课时账户表
CREATE TABLE IF NOT EXISTS fin_class_hour_account (
    id BIGINT NOT NULL COMMENT 'ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    contract_id BIGINT COMMENT '合同ID',
    course_id BIGINT COMMENT '课程ID',
    campus_id BIGINT COMMENT '校区ID',
    total_hours DECIMAL(10,2) DEFAULT 0 COMMENT '总课时',
    used_hours DECIMAL(10,2) DEFAULT 0 COMMENT '已消耗课时',
    remaining_hours DECIMAL(10,2) DEFAULT 0 COMMENT '剩余课时',
    gift_hours DECIMAL(10,2) DEFAULT 0 COMMENT '赠送课时',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-正常，frozen-冻结，exhausted-已用完',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课时账户表';

-- 课时消耗记录表
CREATE TABLE IF NOT EXISTS fin_class_hour_record (
    id BIGINT NOT NULL COMMENT 'ID',
    account_id BIGINT NOT NULL COMMENT '课时账户ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    schedule_id BIGINT COMMENT '排课ID',
    type VARCHAR(20) NOT NULL COMMENT '类型：consume-消耗，gift-赠送，adjust-调整，refund-退费',
    hours DECIMAL(10,2) NOT NULL COMMENT '课时数（正数增加，负数减少）',
    balance DECIMAL(10,2) COMMENT '变动后余额',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT COMMENT '创建人',
    PRIMARY KEY (id),
    KEY idx_account_id (account_id),
    KEY idx_student_id (student_id),
    KEY idx_schedule_id (schedule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课时消耗记录表';

-- =============================================
-- 营销模块表 (mkt_)
-- =============================================

-- 线索表
CREATE TABLE IF NOT EXISTS mkt_lead (
    id BIGINT NOT NULL COMMENT '线索ID',
    lead_no VARCHAR(50) NOT NULL COMMENT '线索编号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    age INT COMMENT '年龄',
    source VARCHAR(20) COMMENT '来源：offline-地推，referral-转介绍，online_ad-线上广告，walk_in-自然到访，phone-电话咨询',
    source_detail VARCHAR(200) COMMENT '来源详情',
    intent_course_id BIGINT COMMENT '意向课程ID',
    intent_level VARCHAR(20) DEFAULT 'medium' COMMENT '意向程度：high-高，medium-中，low-低',
    status VARCHAR(20) DEFAULT 'new' COMMENT '状态：new-新线索，following-跟进中，appointed-已预约，trialed-已试听，converted-已成交，lost-已流失',
    advisor_id BIGINT COMMENT '跟进顾问ID',
    campus_id BIGINT COMMENT '校区ID',
    next_follow_time DATETIME COMMENT '下次跟进时间',
    last_follow_time DATETIME COMMENT '最后跟进时间',
    follow_count INT DEFAULT 0 COMMENT '跟进次数',
    lost_reason VARCHAR(500) COMMENT '流失原因',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_lead_no (lead_no),
    KEY idx_phone (phone),
    KEY idx_advisor_id (advisor_id),
    KEY idx_campus_id (campus_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='线索表';

-- 跟进记录表
CREATE TABLE IF NOT EXISTS mkt_follow_up (
    id BIGINT NOT NULL COMMENT 'ID',
    lead_id BIGINT NOT NULL COMMENT '线索ID',
    method VARCHAR(20) COMMENT '跟进方式：phone-电话，wechat-微信，visit-到访，other-其他',
    content TEXT COMMENT '跟进内容',
    result VARCHAR(500) COMMENT '跟进结果',
    next_follow_time DATETIME COMMENT '下次跟进时间',
    follower_id BIGINT COMMENT '跟进人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_lead_id (lead_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='跟进记录表';

-- 试听记录表
CREATE TABLE IF NOT EXISTS mkt_trial_lesson (
    id BIGINT NOT NULL COMMENT 'ID',
    lead_id BIGINT COMMENT '线索ID',
    student_id BIGINT COMMENT '学员ID',
    course_id BIGINT COMMENT '课程ID',
    class_id BIGINT COMMENT '班级ID',
    schedule_id BIGINT COMMENT '排课ID',
    campus_id BIGINT COMMENT '校区ID',
    trial_date DATE COMMENT '试听日期',
    trial_time TIME COMMENT '试听时间',
    status VARCHAR(20) DEFAULT 'appointed' COMMENT '状态：appointed-已预约，attended-已到场，absent-未到场，converted-已转化',
    feedback TEXT COMMENT '试听反馈',
    rating INT COMMENT '评分（1-5）',
    advisor_id BIGINT COMMENT '顾问ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_lead_id (lead_id),
    KEY idx_student_id (student_id),
    KEY idx_campus_id (campus_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试听记录表';
