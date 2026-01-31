# 在线支付接口实现文档

## 任务概述

完成任务 18.3 和 18.4：实现在线支付接口和支付回调处理

## 实现内容

### 1. 在线支付接口（任务 18.3）

#### 1.1 核心功能

- ✅ 创建支付订单
- ✅ 生成支付参数
- ✅ 返回支付URL或参数
- ✅ 支持多种支付方式（微信、支付宝、银联、模拟支付）

#### 1.2 已实现的文件

**Controller 层：**
- `/edu-finance/src/main/java/com/edu/finance/controller/OnlinePaymentController.java`
  - `POST /finance/online-payment/create` - 创建在线支付订单
  - `GET /finance/online-payment/query/{paymentId}` - 查询支付订单状态
  - `POST /finance/online-payment/cancel/{paymentId}` - 取消支付订单
  - `POST /finance/online-payment/sync/{paymentId}` - 同步支付状态

**Service 层：**
- `/edu-finance/src/main/java/com/edu/finance/service/OnlinePaymentService.java` - 接口定义
- `/edu-finance/src/main/java/com/edu/finance/service/impl/OnlinePaymentServiceImpl.java` - 实现类

**支付网关接口：**
- `/edu-finance/src/main/java/com/edu/finance/payment/PaymentGateway.java` - 统一支付网关接口

**支付网关实现：**
- `/edu-finance/src/main/java/com/edu/finance/payment/gateway/WechatPaymentGateway.java` - 微信支付
- `/edu-finance/src/main/java/com/edu/finance/payment/gateway/AlipayPaymentGateway.java` - 支付宝支付
- `/edu-finance/src/main/java/com/edu/finance/payment/gateway/UnionPaymentGateway.java` - 银联支付
- `/edu-finance/src/main/java/com/edu/finance/payment/gateway/MockPaymentGateway.java` - 模拟支付（新增）

**配置类：**
- `/edu-finance/src/main/java/com/edu/finance/payment/config/WechatPayConfig.java`
- `/edu-finance/src/main/java/com/edu/finance/payment/config/AlipayConfig.java`
- `/edu-finance/src/main/java/com/edu/finance/payment/config/UnionPayConfig.java`

**DTO 类：**
- `/edu-finance/src/main/java/com/edu/finance/domain/dto/OnlinePaymentRequest.java` - 支付请求
- `/edu-finance/src/main/java/com/edu/finance/domain/dto/OnlinePaymentResponse.java` - 支付响应
- `/edu-finance/src/main/java/com/edu/finance/domain/dto/PaymentNotification.java` - 支付通知

### 2. 支付回调处理（任务 18.4）

#### 2.1 核心功能

- ✅ 接收支付平台回调
- ✅ 验证签名
- ✅ 更新支付状态
- ✅ 更新合同支付状态
- ✅ 触发课时账户创建事件

#### 2.2 已实现的文件

**回调接口：**
- `POST /finance/online-payment/notify/wechat` - 微信支付回调
- `POST /finance/online-payment/notify/alipay` - 支付宝支付回调
- `POST /finance/online-payment/notify/unionpay` - 银联支付回调
- `POST /finance/online-payment/notify/mock` - 模拟支付回调（新增）

**事件处理：**
- `/edu-finance/src/main/java/com/edu/finance/event/ContractPaidEvent.java` - 合同支付完成事件
- `/edu-finance/src/main/java/com/edu/finance/listener/ContractPaidEventListener.java` - 事件监听器

**支付服务：**
- `/edu-finance/src/main/java/com/edu/finance/service/impl/PaymentServiceImpl.java`
  - `confirmPayment()` - 确认收款，更新合同状态，发布事件

## 支付流程说明

### 完整支付流程

```
1. 前端调用创建支付订单接口
   POST /finance/online-payment/create
   {
     "contractId": 1,
     "studentId": 100,
     "amount": 5000.00,
     "paymentChannel": "wechat",  // wechat/alipay/unionpay/mock
     "paymentScene": "native",     // native/h5/app/jsapi
     "clientIp": "192.168.1.1",
     "remark": "学费缴纳"
   }

2. 后端创建收款记录，调用支付网关
   - 生成收款单号（SK + paymentId）
   - 调用对应支付渠道的网关
   - 返回支付凭证（二维码/URL/参数）

3. 前端展示支付界面
   - 二维码支付：展示二维码供用户扫描
   - H5支付：跳转到支付页面
   - APP支付：调用APP支付SDK
   - JSAPI支付：调用微信/支付宝JSAPI

4. 用户完成支付

5. 支付平台回调通知
   POST /finance/online-payment/notify/{channel}
   - 验证签名
   - 解析通知数据
   - 更新收款记录状态
   - 更新合同已收金额
   - 发布合同支付完成事件

6. 事件监听器处理
   - 创建课时账户
   - 发送通知消息
   - 其他业务逻辑

7. 前端轮询或主动查询支付状态
   GET /finance/online-payment/query/{paymentId}
```

### 支付状态流转

```
pending (待支付)
  ↓
paying (支付中)
  ↓
paid (已支付) / failed (支付失败) / cancelled (已取消)
```

## 支付渠道配置

### 1. 微信支付配置

在 `application.yml` 或 `application-dev.yml` 中添加：

```yaml
payment:
  wechat:
    enabled: true
    app-id: wx1234567890abcdef
    mch-id: 1234567890
    api-key: your_api_key_32_characters_long
    api-v3-key: your_api_v3_key_32_characters
    serial-no: your_certificate_serial_number
    private-key-path: classpath:cert/wechat/apiclient_key.pem
    notify-url: https://your-domain.com/finance/online-payment/notify/wechat
    api-url: https://api.mch.weixin.qq.com
```

### 2. 支付宝配置

```yaml
payment:
  alipay:
    enabled: true
    app-id: 2021001234567890
    private-key: your_rsa_private_key
    alipay-public-key: alipay_rsa_public_key
    sign-type: RSA2
    charset: UTF-8
    notify-url: https://your-domain.com/finance/online-payment/notify/alipay
    return-url: https://your-domain.com/payment/return
    gateway-url: https://openapi.alipay.com/gateway.do
```

### 3. 银联支付配置

```yaml
payment:
  unionpay:
    enabled: true
    mer-id: 123456789012345
    cert-path: classpath:cert/unionpay/acp_test_sign.pfx
    cert-password: 000000
    cert-id: your_cert_serial_number
    validate-cert-path: classpath:cert/unionpay/acp_test_verify_sign.cer
    middle-cert-path: classpath:cert/unionpay/acp_test_middle.cer
    root-cert-path: classpath:cert/unionpay/acp_test_root.cer
    notify-url: https://your-domain.com/finance/online-payment/notify/unionpay
    return-url: https://your-domain.com/payment/return
    front-url: https://gateway.95516.com/gateway/api/frontTransReq.do
    back-url: https://gateway.95516.com/gateway/api/backTransReq.do
    single-query-url: https://gateway.95516.com/gateway/api/queryTrans.do
```

### 4. 模拟支付配置（开发测试）

```yaml
payment:
  mock:
    enabled: true  # 默认启用，生产环境设置为 false
```

## API 接口文档

### 1. 创建在线支付订单

**接口：** `POST /finance/online-payment/create`

**请求参数：**
```json
{
  "paymentId": 123,           // 可选，已有收款记录ID
  "contractId": 1,            // 合同ID
  "studentId": 100,           // 学员ID
  "amount": 5000.00,          // 支付金额
  "paymentChannel": "wechat", // 支付渠道：wechat/alipay/unionpay/mock
  "paymentScene": "native",   // 支付场景：native/h5/app/jsapi/page/wap
  "userId": "openid_xxx",     // 用户标识（JSAPI支付必填）
  "clientIp": "192.168.1.1",  // 客户端IP
  "notifyUrl": "https://...", // 回调URL（可选）
  "returnUrl": "https://...", // 前端回跳URL（可选）
  "remark": "学费缴纳"         // 备注
}
```

**响应参数：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "paymentId": 123,
    "paymentNo": "SK123",
    "channelOrderNo": "MOCK1234567890",
    "paymentChannel": "wechat",
    "paymentScene": "native",
    "paymentCredential": "weixin://wxpay/bizpayurl?pr=xxx",
    "credentialType": "qrcode",  // qrcode/url/jsapi/form
    "expireSeconds": 1800,
    "errorMsg": null
  }
}
```

### 2. 查询支付订单状态

**接口：** `GET /finance/online-payment/query/{paymentId}`

**响应参数：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "paymentChannel": "wechat",
    "outTradeNo": "SK123",
    "transactionNo": "4200001234567890",
    "amount": 5000.00,
    "status": "success",        // success/failed/pending
    "payTime": "2026-01-31T10:30:00",
    "buyerAccount": "user@example.com",
    "buyerId": "openid_xxx",
    "errorCode": null,
    "errorMsg": null
  }
}
```

### 3. 取消支付订单

**接口：** `POST /finance/online-payment/cancel/{paymentId}`

**响应参数：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

### 4. 同步支付状态

**接口：** `POST /finance/online-payment/sync/{paymentId}`

**说明：** 主动查询支付平台订单状态并同步到本地

**响应参数：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

### 5. 支付回调通知

**接口：** `POST /finance/online-payment/notify/{channel}`

**说明：**
- 由支付平台调用，不需要前端调用
- 支持的 channel：wechat/alipay/unionpay/mock
- 返回格式根据不同支付平台要求返回

**模拟支付回调示例：**
```bash
curl -X POST "http://localhost:8080/finance/online-payment/notify/mock?outTradeNo=SK123&status=success"
```

## 使用示例

### 示例 1：微信扫码支付

```java
// 1. 创建支付订单
OnlinePaymentRequest request = new OnlinePaymentRequest();
request.setContractId(1L);
request.setStudentId(100L);
request.setAmount(new BigDecimal("5000.00"));
request.setPaymentChannel("wechat");
request.setPaymentScene("native");
request.setClientIp("192.168.1.1");
request.setRemark("学费缴纳");

OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

// 2. 前端展示二维码
String qrcodeUrl = response.getPaymentCredential();
// 生成二维码图片供用户扫描

// 3. 等待支付回调或轮询查询状态
PaymentNotification notification = onlinePaymentService.queryPayment(response.getPaymentId());
if ("success".equals(notification.getStatus())) {
    // 支付成功
}
```

### 示例 2：支付宝网页支付

```java
OnlinePaymentRequest request = new OnlinePaymentRequest();
request.setContractId(1L);
request.setStudentId(100L);
request.setAmount(new BigDecimal("5000.00"));
request.setPaymentChannel("alipay");
request.setPaymentScene("page");
request.setReturnUrl("https://your-domain.com/payment/return");
request.setRemark("学费缴纳");

OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

// 前端跳转到支付宝支付页面
String paymentUrl = response.getPaymentCredential();
// window.location.href = paymentUrl;
```

### 示例 3：模拟支付（开发测试）

```java
// 1. 创建模拟支付订单
OnlinePaymentRequest request = new OnlinePaymentRequest();
request.setContractId(1L);
request.setStudentId(100L);
request.setAmount(new BigDecimal("5000.00"));
request.setPaymentChannel("mock");
request.setPaymentScene("native");
request.setRemark("学费缴纳");

OnlinePaymentResponse response = onlinePaymentService.createPayment(request);
String outTradeNo = response.getPaymentNo();

// 2. 模拟支付回调（使用 curl 或 Postman）
// POST http://localhost:8080/finance/online-payment/notify/mock?outTradeNo=SK123&status=success

// 3. 查询支付状态
PaymentNotification notification = onlinePaymentService.queryPayment(response.getPaymentId());
// status 应该为 "success"
```

## 测试步骤

### 1. 使用模拟支付测试

```bash
# 1. 启动应用
mvn spring-boot:run -pl edu-admin

# 2. 创建支付订单
curl -X POST http://localhost:8080/finance/online-payment/create \
  -H "Content-Type: application/json" \
  -d '{
    "contractId": 1,
    "studentId": 100,
    "amount": 5000.00,
    "paymentChannel": "mock",
    "paymentScene": "native",
    "remark": "学费缴纳"
  }'

# 响应示例：
# {
#   "code": 200,
#   "data": {
#     "paymentId": 123,
#     "paymentNo": "SK123",
#     "paymentCredential": "mock://pay?outTradeNo=SK123&amount=5000.00"
#   }
# }

# 3. 模拟支付回调
curl -X POST "http://localhost:8080/finance/online-payment/notify/mock?outTradeNo=SK123&status=success"

# 响应：SUCCESS

# 4. 查询支付状态
curl http://localhost:8080/finance/online-payment/query/123

# 响应示例：
# {
#   "code": 200,
#   "data": {
#     "status": "success",
#     "transactionNo": "MOCK1234567890",
#     "amount": 5000.00
#   }
# }
```

### 2. 使用 Knife4j 测试

1. 访问 http://localhost:8080/doc.html
2. 找到"在线支付管理"分组
3. 测试各个接口

### 3. 验证事件触发

查看日志，确认以下流程：

```
1. 创建支付订单成功
2. 收到支付回调通知
3. 验证签名通过
4. 更新收款记录状态为 paid
5. 更新合同已收金额
6. 发布合同支付完成事件
7. 事件监听器创建课时账户
```

## 注意事项

### 1. 生产环境配置

- **禁用模拟支付：** 设置 `payment.mock.enabled=false`
- **配置真实支付参数：** 填写真实的商户号、密钥、证书等
- **使用 HTTPS：** 回调 URL 必须使用 HTTPS
- **配置域名白名单：** 在支付平台配置回调域名白名单

### 2. 安全建议

- **签名验证：** 所有回调必须验证签名
- **幂等性处理：** 支付回调可能重复，需要做幂等性处理
- **金额校验：** 回调金额必须与订单金额一致
- **状态检查：** 只有待支付状态才能确认收款
- **日志记录：** 记录所有支付操作和回调日志

### 3. 异常处理

- **超时订单：** 定时任务扫描超时订单并关闭
- **回调失败：** 支持主动同步支付状态
- **网络异常：** 支付网关调用需要重试机制
- **并发控制：** 使用分布式锁防止重复处理

### 4. 性能优化

- **异步处理：** 事件监听器使用 @Async 异步处理
- **缓存支付网关：** 使用 ConcurrentHashMap 缓存网关实例
- **批量查询：** 定时任务批量查询支付状态

## 扩展功能

### 1. 支付退款

可以参考 `RefundCompletedEvent` 实现退款功能：

```java
public interface OnlinePaymentService {
    /**
     * 申请退款
     */
    RefundResponse applyRefund(RefundRequest request);

    /**
     * 查询退款状态
     */
    RefundNotification queryRefund(String refundNo);
}
```

### 2. 分账功能

支持多商户分账：

```java
public interface OnlinePaymentService {
    /**
     * 创建分账订单
     */
    ProfitSharingResponse createProfitSharing(ProfitSharingRequest request);
}
```

### 3. 支付限额

添加支付限额控制：

```java
// 单笔限额
if (amount.compareTo(MAX_AMOUNT) > 0) {
    throw new BusinessException("单笔支付金额不能超过" + MAX_AMOUNT);
}

// 日累计限额
BigDecimal todayAmount = getTodayPaymentAmount(studentId);
if (todayAmount.add(amount).compareTo(DAILY_LIMIT) > 0) {
    throw new BusinessException("今日支付金额已达上限");
}
```

### 4. 支付风控

添加风控规则：

```java
// IP 限制
if (isBlacklistIp(clientIp)) {
    throw new BusinessException("该IP已被限制支付");
}

// 频率限制
if (getPaymentCountInMinute(studentId) > 5) {
    throw new BusinessException("支付过于频繁，请稍后再试");
}
```

## 相关文档

- [微信支付官方文档](https://pay.weixin.qq.com/wiki/doc/apiv3/index.shtml)
- [支付宝开放平台](https://opendocs.alipay.com/open/270/105898)
- [银联在线支付](https://open.unionpay.com/tjweb/acproduct/APIList)

## 总结

本次实现完成了以下内容：

1. ✅ 在线支付接口（任务 18.3）
   - 创建支付订单
   - 生成支付参数
   - 返回支付凭证
   - 支持多种支付方式

2. ✅ 支付回调处理（任务 18.4）
   - 接收支付平台回调
   - 验证签名
   - 更新支付状态
   - 更新合同状态
   - 触发课时账户创建事件

3. ✅ 新增模拟支付网关
   - 用于开发测试
   - 无需真实支付平台配置
   - 支持完整支付流程

4. ✅ 完善文档和示例
   - API 接口文档
   - 使用示例
   - 测试步骤
   - 注意事项

所有代码已完成，可以直接使用模拟支付进行测试，生产环境配置真实支付参数即可。
