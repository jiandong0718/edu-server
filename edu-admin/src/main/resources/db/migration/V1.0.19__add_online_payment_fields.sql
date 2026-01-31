-- ============================================================================
-- 在线支付功能增强
-- 版本: V1.0.19
-- 描述: 为支付表添加在线支付相关字段
-- ============================================================================

-- 修改收款表，添加在线支付相关字段
ALTER TABLE fin_payment
    ADD COLUMN channel_order_no VARCHAR(100) COMMENT '支付渠道订单号' AFTER transaction_no,
    ADD COLUMN payment_scene VARCHAR(20) COMMENT '支付场景: app-APP支付, h5-H5支付, native-扫码支付, jsapi-公众号/小程序支付, page-网页支付, wap-手机网站支付' AFTER payment_method,
    ADD COLUMN buyer_id VARCHAR(100) COMMENT '买家用户ID(openid/buyer_id等)' AFTER receiver_id,
    ADD COLUMN buyer_account VARCHAR(100) COMMENT '买家账号' AFTER buyer_id,
    ADD COLUMN notify_time DATETIME COMMENT '回调通知时间' AFTER pay_time,
    ADD COLUMN error_code VARCHAR(50) COMMENT '错误码' AFTER remark,
    ADD COLUMN error_msg VARCHAR(500) COMMENT '错误信息' AFTER error_code,
    ADD INDEX idx_channel_order_no (channel_order_no),
    ADD INDEX idx_transaction_no (transaction_no),
    ADD INDEX idx_status (status);

-- 更新支付状态枚举注释
ALTER TABLE fin_payment
    MODIFY COLUMN status VARCHAR(20) COMMENT '支付状态：pending-待支付，paying-支付中，paid-已支付，failed-支付失败，cancelled-已取消，refunded-已退款';

-- 更新支付方式枚举注释
ALTER TABLE fin_payment
    MODIFY COLUMN payment_method VARCHAR(20) COMMENT '支付方式：wechat-微信，alipay-支付宝，unionpay-银联，cash-现金，pos-POS机';
