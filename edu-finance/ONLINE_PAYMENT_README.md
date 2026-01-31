# 在线支付模块文档

## 概述

本模块实现了教育机构学生管理系统的在线支付功能，支持微信支付、支付宝支付和银联支付三种主流支付方式。

## 功能特性

### 1. 多渠道支付支持
- **微信支付**: 支持APP支付、H5支付、扫码支付(Native)、公众号/小程序支付(JSAPI)
- **支付宝支付**: 支持APP支付、手机网站支付(WAP)、电脑网站支付(Page)、扫码支付(Native)
- **银联支付**: 支持PC支付、移动支付、扫码支付

### 2. 统一支付接口
- 统一的支付网关接口设计
- 支持动态切换支付渠道
- 自动路由到对应的支付网关

### 3. 支付回调处理
- 异步回调通知处理
- 签名验证确保安全性
- 自动更新订单状态
- 触发后续业务流程

### 4. 订单管理
- 支付订单创建
- 订单状态查询
- 订单取消/关闭
- 支付状态同步

## 架构设计

### 核心组件

```
finance/
├── payment/                          # 支付模块
│   ├── PaymentGateway.java          # 支付网关接口
│   ├── config/                       # 支付配置
│   │   ├── WechatPayConfig.java     # 微信支付配置
│   │   ├── AlipayConfig.java        # 支付宝配置
│   │   └── UnionPayConfig.java      # 银联配置
│   └── gateway/                      # 网关实现
│       ├── WechatPaymentGateway.java    # 微信支付网关
│       ├── AlipayPaymentGateway.java    # 支付宝网关
│       └── UnionPaymentGateway.java     # 银联网关
├── service/
│   ├── OnlinePaymentService.java         # 在线支付服务接口
│   └── impl/
│       └── OnlinePaymentServiceImpl.java # 在线支付服务实现
├── controller/
│   └── OnlinePaymentController.java      # 在线支付控制器
└── domain/
    ├── dto/
    │   ├── OnlinePaymentRequest.java     # 支付请求DTO
    │   ├── OnlinePaymentResponse.java    # 支付响应DTO
    │   └── PaymentNotification.java      # 支付通知DTO
    └── enums/
        ├── PaymentChannel.java           # 支付渠道枚举
        └── PaymentStatus.java            # 支付状态枚举
```

### 接口设计

#### PaymentGateway 接口
所有支付网关实现统一的接口:
- `createPayment()`: 创建支付订单
- `queryPayment()`: 查询支付状态
- `closePayment()`: 关闭支付订单
- `parseNotification()`: 解析回调通知
- `verifyNotification()`: 验证回调签名
- `generateNotificationResponse()`: 生成回调响应

## API 接口

### 1. 创建支付订单

**接口**: `POST /finance/online-payment/create`

**请求参数**:
```json
{
  "paymentId": 123,
  "contractId": 456,
  "studentId": 789,
  "amount": 5000.00,
  "paymentChannel": "wechat",
  "paymentScene": "native",
  "userId": "openid_xxx",
  "clientIp": "192.168.1.1",
  "notifyUrl": "https://your-domain.com/notify",
  "returnUrl": "https://your-domain.com/return",
  "remark": "学费缴纳"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentId": 123,
    "paymentNo": "SK20260131001",
    "channelOrderNo": "wx_order_123",
    "paymentChannel": "wechat",
    "paymentScene": "native",
    "paymentCredential": "weixin://wxpay/bizpayurl?pr=xxx",
    "credentialType": "qrcode",
    "expireSeconds": 7200
  }
}
```

### 2. 查询支付状态

**接口**: `GET /finance/online-payment/query/{paymentId}`

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentChannel": "wechat",
    "outTradeNo": "SK20260131001",
    "transactionNo": "wx_trans_123",
    "amount": 5000.00,
    "status": "success",
    "payTime": "2026-01-31T10:30:00",
    "buyerId": "openid_xxx"
  }
}
```

### 3. 取消支付订单

**接口**: `POST /finance/online-payment/cancel/{paymentId}`

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 4. 同步支付状态

**接口**: `POST /finance/online-payment/sync/{paymentId}`

**响应参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 5. 支付回调通知

**微信支付回调**: `POST /finance/online-payment/notify/wechat`

**支付宝回调**: `POST /finance/online-payment/notify/alipay`

**银联回调**: `POST /finance/online-payment/notify/unionpay`

## 配置说明

### 1. 微信支付配置

在 `application.yml` 中添加:

```yaml
payment:
  wechat:
    enabled: true
    app-id: wx1234567890abcdef
    mch-id: 1234567890
    api-key: your-api-key-32-characters-long
    api-v3-key: your-api-v3-key-32-characters
    serial-no: your-certificate-serial-number
    private-key-path: /path/to/apiclient_key.pem
    notify-url: https://your-domain.com/finance/online-payment/notify/wechat
```

### 2. 支付宝配置

```yaml
payment:
  alipay:
    enabled: true
    app-id: 2021001234567890
    private-key: your-private-key
    alipay-public-key: alipay-public-key
    sign-type: RSA2
    notify-url: https://your-domain.com/finance/online-payment/notify/alipay
    return-url: https://your-domain.com/payment/return
```

### 3. 银联配置

```yaml
payment:
  unionpay:
    enabled: true
    mer-id: 123456789012345
    cert-path: /path/to/cert.pfx
    cert-password: your-cert-password
    notify-url: https://your-domain.com/finance/online-payment/notify/unionpay
```

## 使用示例

### 1. 创建微信扫码支付

```java
OnlinePaymentRequest request = new OnlinePaymentRequest();
request.setContractId(456L);
request.setStudentId(789L);
request.setAmount(new BigDecimal("5000.00"));
request.setPaymentChannel("wechat");
request.setPaymentScene("native");
request.setRemark("学费缴纳");

OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

// 返回二维码URL给前端展示
String qrcodeUrl = response.getPaymentCredential();
```

### 2. 创建支付宝网页支付

```java
OnlinePaymentRequest request = new OnlinePaymentRequest();
request.setContractId(456L);
request.setStudentId(789L);
request.setAmount(new BigDecimal("5000.00"));
request.setPaymentChannel("alipay");
request.setPaymentScene("page");
request.setReturnUrl("https://your-domain.com/payment/return");

OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

// 跳转到支付宝支付页面
String paymentUrl = response.getPaymentCredential();
```

### 3. 查询支付状态

```java
PaymentNotification notification = onlinePaymentService.queryPayment(paymentId);

if ("success".equals(notification.getStatus())) {
    // 支付成功
    System.out.println("支付成功，交易号: " + notification.getTransactionNo());
} else {
    // 支付失败或未支付
    System.out.println("支付状态: " + notification.getStatus());
}
```

## 支付流程

### 标准支付流程

```
1. 用户选择支付方式
   ↓
2. 前端调用创建支付订单接口
   ↓
3. 后端创建收款记录
   ↓
4. 调用支付网关创建支付订单
   ↓
5. 返回支付凭证(二维码/跳转URL等)
   ↓
6. 用户完成支付
   ↓
7. 支付平台发送异步回调通知
   ↓
8. 后端验证签名并处理回调
   ↓
9. 更新收款记录状态
   ↓
10. 触发后续业务流程(更新合同、发送通知等)
```

### 回调处理流程

```
1. 接收支付平台回调请求
   ↓
2. 验证回调签名
   ↓
3. 解析回调数据
   ↓
4. 查找对应的收款记录
   ↓
5. 检查是否已处理(防重复)
   ↓
6. 更新收款记录状态
   ↓
7. 调用确认收款方法
   ↓
8. 触发业务事件(合同支付完成等)
   ↓
9. 返回成功响应给支付平台
```

## 安全性

### 1. 签名验证
- 所有回调通知都进行签名验证
- 使用支付平台提供的公钥验签
- 防止伪造回调请求

### 2. 防重复处理
- 检查订单状态，避免重复处理
- 使用数据库事务确保一致性

### 3. 金额校验
- 验证回调金额与订单金额是否一致
- 防止金额篡改

### 4. HTTPS通信
- 所有支付接口使用HTTPS
- 确保数据传输安全

## 注意事项

### 1. 生产环境部署

**重要**: 当前实现是简化版本，用于演示架构设计。生产环境部署时必须:

- 使用官方SDK替代当前的简化实现
  - 微信支付: [WxJava](https://github.com/Wechat-Group/WxJava)
  - 支付宝: [alipay-sdk-java](https://github.com/alipay/alipay-sdk-java-all)
  - 银联: [银联官方SDK](https://open.unionpay.com/)

- 正确配置证书和密钥
  - 妥善保管私钥文件
  - 定期更新证书

- 配置正确的回调URL
  - 必须是公网可访问的HTTPS地址
  - 配置白名单IP

### 2. 测试环境

- 使用沙箱环境进行测试
- 微信支付沙箱: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=23_1
- 支付宝沙箱: https://openhome.alipay.com/develop/sandbox/app
- 银联测试环境: https://open.unionpay.com/

### 3. 异常处理

- 支付超时处理: 定时任务查询未支付订单
- 回调失败重试: 支付平台会多次重试回调
- 订单状态同步: 提供手动同步接口

### 4. 日志记录

- 记录所有支付请求和响应
- 记录回调通知详情
- 便于问题排查和对账

## 扩展性

### 添加新的支付渠道

1. 创建配置类继承 `@ConfigurationProperties`
2. 实现 `PaymentGateway` 接口
3. 添加 `@Component` 注解自动注册
4. 在配置文件中添加相应配置

示例:
```java
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "payment.newchannel", name = "enabled", havingValue = "true")
public class NewChannelPaymentGateway implements PaymentGateway {

    private final NewChannelConfig config;

    @Override
    public String getChannel() {
        return "newchannel";
    }

    // 实现其他接口方法...
}
```

## 依赖说明

当前实现使用的依赖:
- Hutool: 工具类库
- Spring Boot: 框架基础
- MyBatis-Plus: 数据库操作

生产环境建议添加:
```xml
<!-- 微信支付 -->
<dependency>
    <groupId>com.github.binarywang</groupId>
    <artifactId>weixin-java-pay</artifactId>
    <version>4.5.0</version>
</dependency>

<!-- 支付宝 -->
<dependency>
    <groupId>com.alipay.sdk</groupId>
    <artifactId>alipay-sdk-java</artifactId>
    <version>4.38.10.ALL</version>
</dependency>
```

## 常见问题

### Q1: 回调通知收不到？
A: 检查以下几点:
- 回调URL是否配置正确
- 服务器是否可以从公网访问
- 防火墙是否开放端口
- 是否使用HTTPS

### Q2: 签名验证失败？
A: 检查以下几点:
- 密钥配置是否正确
- 证书是否过期
- 参数编码是否一致
- 签名算法是否正确

### Q3: 支付成功但订单未更新？
A: 可能原因:
- 回调处理异常
- 数据库事务回滚
- 查看日志排查具体错误
- 使用同步状态接口手动同步

## 联系支持

如有问题，请联系技术支持团队。
