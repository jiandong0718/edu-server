-- 增强价格策略表和添加价格策略规则表
-- V1.0.25__add_price_strategy_tables.sql

-- 删除旧的价格策略表（如果存在）
DROP TABLE IF EXISTS tch_price_strategy;

-- 创建新的价格策略表
CREATE TABLE IF NOT EXISTS tch_price_strategy (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    strategy_name VARCHAR(100) NOT NULL COMMENT '策略名称',
    strategy_code VARCHAR(50) NOT NULL COMMENT '策略编码（唯一）',
    description TEXT COMMENT '策略描述',
    course_id BIGINT COMMENT '关联课程ID（可为空表示通用策略）',
    strategy_type VARCHAR(20) NOT NULL COMMENT '策略类型：TIERED-阶梯价格, MEMBER-会员价, PROMOTION-促销价, CUSTOM-自定义',
    priority INT NOT NULL DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
    start_date DATE COMMENT '有效期开始日期',
    end_date DATE COMMENT '有效期结束日期',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用, INACTIVE-禁用',
    campus_id BIGINT COMMENT '校区ID（null表示全部校区可用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
    UNIQUE KEY uk_strategy_code (strategy_code, deleted),
    INDEX idx_strategy_name (strategy_name),
    INDEX idx_strategy_type (strategy_type),
    INDEX idx_course_id (course_id),
    INDEX idx_priority (priority),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_status (status),
    INDEX idx_campus_id (campus_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格策略表';

-- 创建价格策略规则表
CREATE TABLE IF NOT EXISTS tch_price_strategy_rule (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    strategy_id BIGINT NOT NULL COMMENT '策略ID',
    condition_type VARCHAR(30) NOT NULL COMMENT '条件类型：CLASS_HOURS-课时数, AMOUNT-金额, MEMBER_LEVEL-会员等级',
    condition_value TEXT NOT NULL COMMENT '条件值（JSON格式）',
    discount_type VARCHAR(20) NOT NULL COMMENT '折扣类型：PERCENTAGE-百分比, FIXED-固定金额, PRICE-直接定价',
    discount_value DECIMAL(10, 2) NOT NULL COMMENT '折扣值',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
    INDEX idx_strategy_id (strategy_id),
    INDEX idx_condition_type (condition_type),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (strategy_id) REFERENCES tch_price_strategy(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格策略规则表';

-- 插入示例数据
INSERT INTO tch_price_strategy (id, strategy_name, strategy_code, description, strategy_type, priority, start_date, end_date, status, campus_id, deleted)
VALUES
(1, '阶梯价格策略', 'TIERED_001', '根据购买课时数量提供不同折扣', 'TIERED', 10, '2026-01-01', '2026-12-31', 'ACTIVE', NULL, 0),
(2, '会员价策略', 'MEMBER_001', '根据会员等级提供不同折扣', 'MEMBER', 20, '2026-01-01', '2026-12-31', 'ACTIVE', NULL, 0),
(3, '春季促销', 'PROMOTION_001', '春季限时促销活动', 'PROMOTION', 30, '2026-03-01', '2026-05-31', 'ACTIVE', NULL, 0);

-- 插入阶梯价格规则
INSERT INTO tch_price_strategy_rule (id, strategy_id, condition_type, condition_value, discount_type, discount_value, deleted)
VALUES
(1, 1, 'CLASS_HOURS', '{"min": 10, "max": 20}', 'PERCENTAGE', 90.00, 0),
(2, 1, 'CLASS_HOURS', '{"min": 21, "max": 50}', 'PERCENTAGE', 85.00, 0),
(3, 1, 'CLASS_HOURS', '{"min": 51, "max": null}', 'PERCENTAGE', 80.00, 0);

-- 插入会员价规则
INSERT INTO tch_price_strategy_rule (id, strategy_id, condition_type, condition_value, discount_type, discount_value, deleted)
VALUES
(4, 2, 'MEMBER_LEVEL', '{"level": "NORMAL"}', 'PERCENTAGE', 95.00, 0),
(5, 2, 'MEMBER_LEVEL', '{"level": "SILVER"}', 'PERCENTAGE', 90.00, 0),
(6, 2, 'MEMBER_LEVEL', '{"level": "GOLD"}', 'PERCENTAGE', 85.00, 0),
(7, 2, 'MEMBER_LEVEL', '{"level": "DIAMOND"}', 'PERCENTAGE', 80.00, 0);

-- 插入促销价规则
INSERT INTO tch_price_strategy_rule (id, strategy_id, condition_type, condition_value, discount_type, discount_value, deleted)
VALUES
(8, 3, 'AMOUNT', '{"min": 0, "max": null}', 'PERCENTAGE', 70.00, 0);
