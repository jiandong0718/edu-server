-- 更新退费表结构，添加更详细的退费字段
ALTER TABLE fin_refund
    ADD COLUMN IF NOT EXISTS apply_amount DECIMAL(10,2) DEFAULT 0 COMMENT '申请退费金额' AFTER campus_id,
    ADD COLUMN IF NOT EXISTS actual_amount DECIMAL(10,2) DEFAULT 0 COMMENT '实际退费金额' AFTER apply_amount,
    ADD COLUMN IF NOT EXISTS penalty_amount DECIMAL(10,2) DEFAULT 0 COMMENT '违约金' AFTER actual_amount,
    ADD COLUMN IF NOT EXISTS reason VARCHAR(200) COMMENT '退费原因' AFTER penalty_amount,
    ADD COLUMN IF NOT EXISTS description VARCHAR(1000) COMMENT '退费说明' AFTER reason,
    ADD COLUMN IF NOT EXISTS approve_remark VARCHAR(500) COMMENT '审批备注' AFTER approver_id,
    ADD COLUMN IF NOT EXISTS refund_time DATETIME COMMENT '退款时间' AFTER approve_remark,
    ADD COLUMN IF NOT EXISTS refund_method VARCHAR(20) COMMENT '退款方式' AFTER refund_time,
    ADD COLUMN IF NOT EXISTS refund_transaction_no VARCHAR(100) COMMENT '退款交易号' AFTER refund_method;

-- 更新状态字段注释
ALTER TABLE fin_refund
    MODIFY COLUMN status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审批，approved-已通过，rejected-已拒绝，refunded-已退款';

-- 如果存在旧字段，可以选择保留或删除
-- ALTER TABLE fin_refund DROP COLUMN IF EXISTS refund_amount;
-- ALTER TABLE fin_refund DROP COLUMN IF EXISTS refund_hours;
-- ALTER TABLE fin_refund DROP COLUMN IF EXISTS refund_reason;
-- ALTER TABLE fin_refund DROP COLUMN IF EXISTS complete_time;
