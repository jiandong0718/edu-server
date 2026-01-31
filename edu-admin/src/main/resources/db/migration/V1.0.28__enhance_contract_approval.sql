-- 合同审批配置增强
-- V1.0.28__enhance_contract_approval.sql

-- 合同审批配置表
CREATE TABLE IF NOT EXISTS fin_contract_approval_config (
    id BIGINT NOT NULL COMMENT '配置ID',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    approval_type VARCHAR(20) DEFAULT 'contract' COMMENT '审批类型：contract-合同审批，change-变更审批，cancel-作废审批',
    amount_min DECIMAL(10,2) DEFAULT 0 COMMENT '金额下限',
    amount_max DECIMAL(10,2) COMMENT '金额上限（NULL表示无上限）',
    approval_levels INT DEFAULT 1 COMMENT '审批级数',
    approver_config TEXT COMMENT '审批人配置（JSON格式）',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    priority INT DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_approval_type (approval_type),
    KEY idx_enabled (is_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同审批配置表';

-- 审批流程表增加退回状态支持
ALTER TABLE fin_contract_approval_flow
MODIFY COLUMN status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审批，approved-已通过，rejected-已拒绝，skipped-已跳过，returned-已退回';

-- 审批记录表增加退回状态支持
ALTER TABLE fin_contract_approval
MODIFY COLUMN status VARCHAR(20) DEFAULT 'pending' COMMENT '审批状态：pending-待审批，approved-已通过，rejected-已拒绝，cancelled-已撤销，returned-已退回';

-- 插入默认审批配置
INSERT INTO fin_contract_approval_config (id, config_name, approval_type, amount_min, amount_max, approval_levels, approver_config, is_enabled, priority, remark, create_time)
VALUES
(1, '小额合同审批（0-5000元）', 'contract', 0, 5000, 1,
'[{"level":1,"roleName":"财务主管","roleCode":"finance_manager"}]',
1, 1, '5000元以下合同只需财务主管审批', NOW()),

(2, '中额合同审批（5000-20000元）', 'contract', 5000, 20000, 2,
'[{"level":1,"roleName":"财务主管","roleCode":"finance_manager"},{"level":2,"roleName":"校区主任","roleCode":"campus_director"}]',
1, 2, '5000-20000元合同需要财务主管和校区主任两级审批', NOW()),

(3, '大额合同审批（20000元以上）', 'contract', 20000, NULL, 3,
'[{"level":1,"roleName":"财务主管","roleCode":"finance_manager"},{"level":2,"roleName":"校区主任","roleCode":"campus_director"},{"level":3,"roleName":"总经理","roleCode":"general_manager"}]',
1, 3, '20000元以上合同需要三级审批', NOW()),

(4, '合同变更审批', 'change', 0, NULL, 2,
'[{"level":1,"roleName":"财务主管","roleCode":"finance_manager"},{"level":2,"roleName":"校区主任","roleCode":"campus_director"}]',
1, 1, '合同变更需要两级审批', NOW()),

(5, '合同作废审批', 'cancel', 0, NULL, 2,
'[{"level":1,"roleName":"财务主管","roleCode":"finance_manager"},{"level":2,"roleName":"校区主任","roleCode":"campus_director"}]',
1, 1, '合同作废需要两级审批', NOW());
