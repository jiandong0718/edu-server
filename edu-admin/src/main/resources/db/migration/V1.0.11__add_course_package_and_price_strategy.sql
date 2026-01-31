-- 课程包和价格策略表
-- V1.0.11__add_course_package_and_price_strategy.sql

-- 课程包表
CREATE TABLE IF NOT EXISTS tch_course_package (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '课程包名称',
    description TEXT COMMENT '课程包描述',
    price DECIMAL(10, 2) NOT NULL COMMENT '课程包价格（优惠价）',
    original_price DECIMAL(10, 2) NOT NULL COMMENT '原价',
    valid_days INT NOT NULL DEFAULT 365 COMMENT '有效天数',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-下架，1-上架',
    campus_id BIGINT COMMENT '校区ID（null表示全部校区可用）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
    INDEX idx_name (name),
    INDEX idx_status (status),
    INDEX idx_campus_id (campus_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程包表';

-- 课程包明细表
CREATE TABLE IF NOT EXISTS tch_course_package_item (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    package_id BIGINT NOT NULL COMMENT '课程包ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    course_count INT NOT NULL DEFAULT 1 COMMENT '课程数量（课时数）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
    INDEX idx_package_id (package_id),
    INDEX idx_course_id (course_id),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (package_id) REFERENCES tch_course_package(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tch_course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程包明细表';

-- 价格策略表
CREATE TABLE IF NOT EXISTS tch_price_strategy (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '策略名称',
    type VARCHAR(20) NOT NULL COMMENT '策略类型：time_period-时间段，student_type-学员类型',
    target_id BIGINT NOT NULL COMMENT '目标ID（课程ID或课程包ID）',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型：course-课程，package-课程包',
    discount_type VARCHAR(20) NOT NULL COMMENT '折扣类型：percentage-百分比，fixed-固定金额',
    discount_value DECIMAL(10, 2) NOT NULL COMMENT '折扣值（百分比：0-100，固定金额：具体金额）',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    student_type VARCHAR(20) COMMENT '学员类型：new-新生，old-老生（仅当type为student_type时有效）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    campus_id BIGINT COMMENT '校区ID（null表示全部校区可用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
    INDEX idx_name (name),
    INDEX idx_type (type),
    INDEX idx_target (target_id, target_type),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_status (status),
    INDEX idx_campus_id (campus_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格策略表';
