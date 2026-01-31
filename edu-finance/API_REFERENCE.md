# 在线支付 API 快速参考

## 基础信息

**Base URL**: `/finance/online-payment`

**认证方式**: JWT Token (在请求头中添加 `Authorization: Bearer {token}`)

## API 端点

### 1. 创建支付订单

创建在线支付订单，支持微信、支付宝、银联等多种支付方式。

**端点**: `POST /finance/online-payment/create`

**请求体**:
```json
{
  "paymentId": 123,              // 可选，已存在的收款记录ID
  "contractId": 456,             // 可选，合同ID
  "studentId": 789,              // 必填，学员ID
  "amount": 5000.00,             // 必填，支付金额
  "paymentChannel": "wechat",    // 必填，支付渠道: wechat/alipay/unionpay
  "paymentScene": "native",      // 必填，支付场景
  "userId": "openid_xxx",        // 可选，用户标识(微信openid等)
  "clientIp": "192.168.1.1",     // 可选，客户端IP
  "notifyUrl": "https://...",    // 可选，回调URL(默认使用配置)
  "returnUrl": "https://...",    // 可选，前端回跳URL
  "remark": "学费缴纳"            // 可选，备注
}
```

**支付场景说明**:
- 微信支付: `app`, `h5`, `native`, `jsapi`
- 支付宝: `app`, `wap`, `page`, `native`
- 银联: `pc`, `mobile`, `qrcode`

**响应**:
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

**凭证类型说明**:
- `qrcode`: 二维码URL，需要生成二维码图片
- `url`: 跳转链接，直接跳转
- `form`: 表单数据，需要提交表单
- `jsapi`: JSAPI参数，用于调用支付SDK

---

### 2. 查询支付状态

主动查询支付订单的当前状态。

**端点**: `GET /finance/online-payment/query/{paymentId}`

**路径参数**:
- `paymentId`: 收款记录ID

**响应**:
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
    "buyerId": "openid_xxx",
    "buyerAccount": "user@example.com"
  }
}
```

**状态说明**:
- `success`: 支付成功
- `failed`: 支付失败
- 其他: 支付进行中或未支付

---

### 3. 取消支付订单

取消待支付或支付中的订单。

**端点**: `POST /finance/online-payment/cancel/{paymentId}`

**路径参数**:
- `paymentId`: 收款记录ID

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**注意事项**:
- 只能取消状态为 `pending` 或 `paying` 的订单
- 已支付的订单不能取消，需要走退款流程

---

### 4. 同步支付状态

当回调通知未收到时，手动同步支付状态。

**端点**: `POST /finance/online-payment/sync/{paymentId}`

**路径参数**:
- `paymentId`: 收款记录ID

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**使用场景**:
- 回调通知延迟或丢失
- 支付完成但订单状态未更新
- 定时任务批量同步订单状态

---

### 5. 微信支付回调

接收微信支付平台的异步通知。

**端点**: `POST /finance/online-payment/notify/wechat`

**说明**:
- 此接口由微信支付平台调用，不需要手动调用
- 需要在微信支付后台配置回调URL
- 系统会自动验证签名并处理回调

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "成功"
}
```

---

### 6. 支付宝回调

接收支付宝的异步通知。

**端点**: `POST /finance/online-payment/notify/alipay`

**说明**:
- 此接口由支付宝调用，不需要手动调用
- 需要在支付宝后台配置回调URL
- 系统会自动验证签名并处理回调

**响应**:
```
success
```

---

### 7. 银联支付回调

接收银联的异步通知。

**端点**: `POST /finance/online-payment/notify/unionpay`

**说明**:
- 此接口由银联调用，不需要手动调用
- 需要在银联后台配置回调URL
- 系统会自动验证签名并处理回调

**响应**:
```
ok
```

---

## 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 使用流程

### 标准支付流程

```
1. 前端调用创建支付订单接口
   POST /finance/online-payment/create

2. 后端返回支付凭证
   - 二维码: 前端生成二维码图片展示
   - URL: 前端跳转到支付页面
   - JSAPI: 前端调用支付SDK

3. 用户完成支付

4. 支付平台发送异步回调
   POST /finance/online-payment/notify/{channel}

5. 系统自动处理回调
   - 验证签名
   - 更新订单状态
   - 触发业务流程

6. 前端轮询查询支付状态(可选)
   GET /finance/online-payment/query/{paymentId}
```

### 异常处理流程

```
1. 回调未收到
   → 调用同步状态接口
   POST /finance/online-payment/sync/{paymentId}

2. 用户取消支付
   → 调用取消订单接口
   POST /finance/online-payment/cancel/{paymentId}

3. 支付超时
   → 定时任务自动关闭订单
   或手动调用取消接口
```

## 前端集成示例

### 微信扫码支付

```javascript
// 1. 创建支付订单
const response = await axios.post('/finance/online-payment/create', {
  studentId: 789,
  amount: 5000.00,
  paymentChannel: 'wechat',
  paymentScene: 'native',
  remark: '学费缴纳'
});

// 2. 生成二维码
const qrcodeUrl = response.data.data.paymentCredential;
QRCode.toCanvas(canvas, qrcodeUrl);

// 3. 轮询查询支付状态
const paymentId = response.data.data.paymentId;
const timer = setInterval(async () => {
  const result = await axios.get(`/finance/online-payment/query/${paymentId}`);
  if (result.data.data.status === 'success') {
    clearInterval(timer);
    // 支付成功，跳转到成功页面
    router.push('/payment/success');
  }
}, 2000);
```

### 支付宝网页支付

```javascript
// 1. 创建支付订单
const response = await axios.post('/finance/online-payment/create', {
  studentId: 789,
  amount: 5000.00,
  paymentChannel: 'alipay',
  paymentScene: 'page',
  returnUrl: 'https://your-domain.com/payment/return',
  remark: '学费缴纳'
});

// 2. 跳转到支付宝支付页面
const paymentUrl = response.data.data.paymentCredential;
window.location.href = paymentUrl;
```

### 微信JSAPI支付

```javascript
// 1. 创建支付订单
const response = await axios.post('/finance/online-payment/create', {
  studentId: 789,
  amount: 5000.00,
  paymentChannel: 'wechat',
  paymentScene: 'jsapi',
  userId: 'openid_xxx', // 用户的openid
  remark: '学费缴纳'
});

// 2. 调用微信支付JSAPI
const prepayId = response.data.data.paymentCredential;
wx.chooseWXPay({
  timestamp: timestamp,
  nonceStr: nonceStr,
  package: `prepay_id=${prepayId}`,
  signType: 'RSA',
  paySign: paySign,
  success: function(res) {
    // 支付成功
    router.push('/payment/success');
  },
  fail: function(res) {
    // 支付失败
    console.error('支付失败', res);
  }
});
```

## 测试建议

### 使用沙箱环境

1. **微信支付沙箱**
   - 文档: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=23_1
   - 配置沙箱密钥和商户号

2. **支付宝沙箱**
   - 文档: https://openhome.alipay.com/develop/sandbox/app
   - 使用沙箱账号进行测试

3. **银联测试环境**
   - 文档: https://open.unionpay.com/
   - 申请测试商户号

### 测试用例

- [ ] 创建支付订单
- [ ] 扫码支付流程
- [ ] 网页支付流程
- [ ] 支付成功回调
- [ ] 支付失败回调
- [ ] 查询支付状态
- [ ] 取消支付订单
- [ ] 同步支付状态
- [ ] 支付超时处理
- [ ] 重复回调处理

## 常见问题

### Q: 回调通知收不到？
A: 检查以下几点:
- 回调URL是否配置正确
- 服务器是否可以从公网访问
- 防火墙是否开放端口
- 是否使用HTTPS

### Q: 如何处理支付超时？
A: 可以通过以下方式:
- 定时任务扫描超时订单
- 调用查询接口确认支付状态
- 未支付的订单自动关闭

### Q: 支付成功但订单未更新？
A: 可能原因:
- 回调处理异常
- 使用同步状态接口手动同步
- 查看日志排查具体错误

## 联系支持

如有问题，请联系技术支持团队。
