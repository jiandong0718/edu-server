-- 合同审批和打印功能表
-- V1.0.18__add_contract_approval_print_tables.sql

-- 合同审批记录表
CREATE TABLE IF NOT EXISTS fin_contract_approval (
    id BIGINT NOT NULL COMMENT '审批ID',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    approval_no VARCHAR(50) NOT NULL COMMENT '审批单号',
    approval_type VARCHAR(20) DEFAULT 'contract' COMMENT '审批类型：contract-合同审批，change-变更审批，cancel-作废审批',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '审批状态：pending-待审批，approved-已通过，rejected-已拒绝，cancelled-已撤销',
    submitter_id BIGINT NOT NULL COMMENT '提交人ID',
    submit_time DATETIME NOT NULL COMMENT '提交时间',
    submit_reason VARCHAR(500) COMMENT '提交原因',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    current_step INT DEFAULT 1 COMMENT '当前审批步骤',
    total_steps INT DEFAULT 1 COMMENT '总审批步骤',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_approval_no (approval_no),
    KEY idx_contract_id (contract_id),
    KEY idx_status (status),
    KEY idx_submitter_id (submitter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同审批记录表';

-- 合同审批流程表
CREATE TABLE IF NOT EXISTS fin_contract_approval_flow (
    id BIGINT NOT NULL COMMENT 'ID',
    approval_id BIGINT NOT NULL COMMENT '审批ID',
    step_no INT NOT NULL COMMENT '步骤序号',
    approver_id BIGINT NOT NULL COMMENT '审批人ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审批，approved-已通过，rejected-已拒绝，skipped-已跳过',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_approval_id (approval_id),
    KEY idx_approver_id (approver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同审批流程表';

-- 合同打印记录表
CREATE TABLE IF NOT EXISTS fin_contract_print_record (
    id BIGINT NOT NULL COMMENT 'ID',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    print_no VARCHAR(50) NOT NULL COMMENT '打印单号',
    template_id BIGINT COMMENT '打印模板ID',
    template_name VARCHAR(100) COMMENT '模板名称',
    print_type VARCHAR(20) DEFAULT 'pdf' COMMENT '打印类型：pdf-PDF打印，paper-纸质打印',
    print_count INT DEFAULT 1 COMMENT '打印份数',
    file_url VARCHAR(500) COMMENT '文件URL（PDF打印）',
    printer_id BIGINT COMMENT '打印人ID',
    printer_name VARCHAR(50) COMMENT '打印人姓名',
    print_time DATETIME NOT NULL COMMENT '打印时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_print_no (print_no),
    KEY idx_contract_id (contract_id),
    KEY idx_printer_id (printer_id),
    KEY idx_print_time (print_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同打印记录表';

-- 合同打印模板表
CREATE TABLE IF NOT EXISTS fin_contract_print_template (
    id BIGINT NOT NULL COMMENT '模板ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    template_type VARCHAR(20) DEFAULT 'default' COMMENT '模板类型：default-默认模板，custom-自定义模板',
    template_content TEXT COMMENT '模板内容（HTML）',
    page_size VARCHAR(20) DEFAULT 'A4' COMMENT '纸张大小：A4，A5，Letter',
    page_orientation VARCHAR(20) DEFAULT 'portrait' COMMENT '页面方向：portrait-纵向，landscape-横向',
    margin_top INT DEFAULT 20 COMMENT '上边距（mm）',
    margin_bottom INT DEFAULT 20 COMMENT '下边距（mm）',
    margin_left INT DEFAULT 20 COMMENT '左边距（mm）',
    margin_right INT DEFAULT 20 COMMENT '右边距（mm）',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认模板',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code (template_code),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同打印模板表';

-- 插入默认打印模板
INSERT INTO fin_contract_print_template (id, template_name, template_code, template_type, is_default, status, sort_order, create_time)
VALUES (1, '标准合同模板', 'standard', 'default', 1, 'active', 1, NOW());
