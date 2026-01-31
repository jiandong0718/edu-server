-- 教育机构学生管理系统 - 节假日管理
-- V1.0.8__add_holiday_table.sql

-- 节假日表
CREATE TABLE IF NOT EXISTS sys_holiday (
    id BIGINT NOT NULL COMMENT '节假日ID',
    name VARCHAR(100) NOT NULL COMMENT '节假日名称',
    type TINYINT NOT NULL COMMENT '类型：1-法定节假日，2-调休，3-公司假期',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    description VARCHAR(500) COMMENT '描述',
    campus_id BIGINT COMMENT '校区ID（NULL表示全局）',
    is_workday TINYINT DEFAULT 0 COMMENT '是否工作日：0-否（休息），1-是（调休上班）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_campus_id (campus_id),
    KEY idx_date_range (start_date, end_date),
    KEY idx_type (type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='节假日表';

-- 插入2026年法定节假日示例数据
INSERT INTO sys_holiday (id, name, type, start_date, end_date, description, campus_id, is_workday, status) VALUES
(1, '元旦', 1, '2026-01-01', '2026-01-01', '2026年元旦假期', NULL, 0, 1),
(2, '春节', 1, '2026-02-17', '2026-02-23', '2026年春节假期', NULL, 0, 1),
(3, '清明节', 1, '2026-04-05', '2026-04-07', '2026年清明节假期', NULL, 0, 1),
(4, '劳动节', 1, '2026-05-01', '2026-05-05', '2026年劳动节假期', NULL, 0, 1),
(5, '端午节', 1, '2026-06-25', '2026-06-27', '2026年端午节假期', NULL, 0, 1),
(6, '中秋节', 1, '2026-10-01', '2026-10-03', '2026年中秋节假期', NULL, 0, 1),
(7, '国庆节', 1, '2026-10-01', '2026-10-08', '2026年国庆节假期', NULL, 0, 1);
