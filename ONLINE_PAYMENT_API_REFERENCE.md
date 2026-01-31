# 在线支付 API 快速参考

## 基础信息

**Base URL:** `http://localhost:8080`

**认证方式:** JWT Token (在 Header 中添加 `Authorization: Bearer {token}`)

## API 列表

### 1. 创建在线支付订单

创建支付订单并获取支付凭证（二维码/URL/参数）

**接口:** `POST /finance/online-payment/create`

**请求示例:**
```json
{
  "contractId": 1,
  "studentId": 100,
  "amount": 5000.00,
  "paymentChannel": "mock",
  "paymentScene": "native",
  "clientIp": "192.168.1.1",
  "remark": "学费缴纳"
}
```

**参数说明:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paymentId | Long | 否 | 已有收款记录ID |
| contractId | Long | 是 | 合同ID |
| studentId | Long | 是 | 学员ID |
| amount | BigDecimal | 是 | 支付金额 |
| paymentChannel | String | 是 | 支付渠道：wechat/alipay/unionpay/mock |
| paymentScene | String | 是 | 支付场景：native/h5/app/jsapi/page/wap |
| userId | String | 否 | 用户标识（JSAPI支付必填） |
| clientIp | String | 否 | 客户端IP |
| notifyUrl | String | 否 | 回调URL（可选） |
| returnUrl | String | 否 | 前端回跳URL（可选） |
| remark | String | 否 | 备注 |

**支付渠道说明:**

| 渠道 | 代码 | 支持场景 |
|------|------|----------|
| 微信支付 | wechat | native(扫码), h5, app, jsapi |
| 支付宝 | alipay | native(扫码), page(网页), wap(手机), app |
| 银联支付 | unionpay | pc, mobile, qrcode |
| 模拟支付 | mock | 所有场景 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "paymentId": 123,
    "paymentNo": "SK123",
    "channelOrderNo": "MOCK1234567890",
    "paymentChannel": "mock",
    "paymentScene": "native",
    "paymentCredential": "mock://pay?outTradeNo=SK123&amount=5000.00",
    "credentialType": "qrcode",
    "expireSeconds": 1800,
    "errorMsg": null
  }
}
```

**凭证类型说明:**

| 类型 | 说明 | 使用方式 |
|------|------|----------|
| qrcode | 二维码内容 | 生成二维码图片供用户扫描 |
| url | 跳转链接 | 前端跳转到该URL |
| jsapi | JSAPI参数 | 调用微信/支付宝JSAPI |
| form | 表单数据 | 提交表单到支付页面 |

---

### 2. 查询支付订单状态

查询支付订单的当前状态

**接口:** `GET /finance/online-payment/query/{paymentId}`

**路径参数:**
- `paymentId`: 收款记录ID

**响应示例:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "paymentChannel": "mock",
    "outTradeNo": "SK123",
    "transactionNo": "MOCK1234567890",
    "amount": 5000.00,
    "status": "success",
    "payTime": "2026-01-31T10:30:00",
    "buyerAccount": "mock@example.com",
    "buyerId": "mock_buyer_001",
    "errorCode": null,
    "errorMsg": null
  }
}
```

**状态说明:**

| 状态 | 说明 |
|------|------|
| success | 支付成功 |
| failed | 支付失败 |
| pending | 待支付 |
| paying | 支付中 |

---

### 3. 取消支付订单

取消待支付或支付中的订单

**接口:** `POST /finance/online-payment/cancel/{paymentId}`

**路径参数:**
- `paymentId`: 收款记录ID

**响应示例:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

---

### 4. 同步支付状态

主动查询支付平台订单状态并同步到本地

**接口:** `POST /finance/online-payment/sync/{paymentId}`

**路径参数:**
- `paymentId`: 收款记录ID

**使用场景:**
- 长时间未收到支付回调
- 回调处理失败
- 需要确认最新支付状态

**响应示例:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

---

### 5. 支付回调通知

由支付平台调用，不需要前端调用

**接口:** `POST /finance/online-payment/notify/{channel}`

**路径参数:**
- `channel`: 支付渠道（wechat/alipay/unionpay/mock）

**说明:**
- 由支付平台自动调用
- 需要在支付平台配置回调URL白名单
- 返回格式根据不同支付平台要求返回

**模拟支付回调示例:**
```bash
curl -X POST "http://localhost:8080/finance/online-payment/notify/mock?outTradeNo=SK123&status=success"
```

---

## 完整使用流程

### 流程 1: 微信扫码支付

```bash
# 1. 创建支付订单
curl -X POST http://localhost:8080/finance/online-payment/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "contractId": 1,
    "studentId": 100,
    "amount": 5000.00,
    "paymentChannel": "wechat",
    "paymentScene": "native",
    "clientIp": "192.168.1.1",
    "remark": "学费缴纳"
  }'

# 响应：
# {
#   "code": 200,
#   "data": {
#     "paymentId": 123,
#     "paymentCredential": "weixin://wxpay/bizpayurl?pr=xxx",
#     "credentialType": "qrcode"
#   }
# }

# 2. 前端生成二维码供用户扫描

# 3. 用户扫码支付

# 4. 微信支付平台回调（自动）
# POST /finance/online-payment/notify/wechat

# 5. 查询支付状态（可选）
curl http://localhost:8080/finance/online-payment/query/123 \
  -H "Authorization: Bearer {token}"
```

### 流程 2: 支付宝网页支付

```bash
# 1. 创建支付订单
curl -X POST http://localhost:8080/finance/online-payment/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "contractId": 1,
    "studentId": 100,
    "amount": 5000.00,
    "paymentChannel": "alipay",
    "paymentScene": "page",
    "returnUrl": "https://your-domain.com/payment/return",
    "remark": "学费缴纳"
  }'

# 响应：
# {
#   "code": 200,
#   "data": {
#     "paymentId": 123,
#     "paymentCredential": "https://openapi.alipay.com/gateway.do?...",
#     "credentialType": "url"
#   }
# }

# 2. 前端跳转到支付宝支付页面
# window.location.href = paymentCredential

# 3. 用户完成支付

# 4. 支付宝回调（自动）
# POST /finance/online-payment/notify/alipay

# 5. 前端回跳到 returnUrl
```

### 流程 3: 模拟支付（开发测试）

```bash
# 1. 创建模拟支付订单
curl -X POST http://localhost:8080/finance/online-payment/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "contractId": 1,
    "studentId": 100,
    "amount": 5000.00,
    "paymentChannel": "mock",
    "paymentScene": "native",
    "remark": "学费缴纳"
  }'

# 响应：
# {
#   "code": 200,
#   "data": {
#     "paymentId": 123,
#     "paymentNo": "SK123",
#     "paymentCredential": "mock://pay?outTradeNo=SK123&amount=5000.00"
#   }
# }

# 2. 模拟支付回调（手动触发）
curl -X POST "http://localhost:8080/finance/online-payment/notify/mock?outTradeNo=SK123&status=success"

# 响应：SUCCESS

# 3. 查询支付状态
curl http://localhost:8080/finance/online-payment/query/123 \
  -H "Authorization: Bearer {token}"

# 响应：
# {
#   "code": 200,
#   "data": {
#     "status": "success",
#     "transactionNo": "MOCK1234567890",
#     "amount": 5000.00
#   }
# }
```

---

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 参数错误 | 检查请求参数是否正确 |
| 401 | 未授权 | 检查 Token 是否有效 |
| 500 | 服务器错误 | 查看服务器日志 |
| 1001 | 支付金额必须大于0 | 检查金额参数 |
| 1002 | 支付渠道不能为空 | 检查 paymentChannel 参数 |
| 1003 | 学员ID不能为空 | 检查 studentId 参数 |
| 1004 | 不支持的支付渠道 | 检查支付渠道配置 |
| 1005 | 收款记录不存在 | 检查 paymentId 是否正确 |
| 1006 | 只有待支付或支付中的订单才能取消 | 检查订单状态 |

---

## 测试工具

### Postman 集合

导入以下 JSON 到 Postman：

```json
{
  "info": {
    "name": "在线支付 API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "创建支付订单",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"contractId\": 1,\n  \"studentId\": 100,\n  \"amount\": 5000.00,\n  \"paymentChannel\": \"mock\",\n  \"paymentScene\": \"native\",\n  \"remark\": \"学费缴纳\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/finance/online-payment/create",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["finance", "online-payment", "create"]
        }
      }
    },
    {
      "name": "查询支付状态",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:8080/finance/online-payment/query/123",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["finance", "online-payment", "query", "123"]
        }
      }
    },
    {
      "name": "模拟支付回调",
      "request": {
        "method": "POST",
        "url": {
          "raw": "http://localhost:8080/finance/online-payment/notify/mock?outTradeNo=SK123&status=success",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["finance", "online-payment", "notify", "mock"],
          "query": [
            {
              "key": "outTradeNo",
              "value": "SK123"
            },
            {
              "key": "status",
              "value": "success"
            }
          ]
        }
      }
    }
  ]
}
```

### Knife4j 文档

访问 http://localhost:8080/doc.html 查看完整 API 文档

---

## 常见问题

### Q1: 如何测试支付功能？

A: 使用模拟支付（mock）进行测试：
1. 确保配置 `payment.mock.enabled=true`
2. 创建支付订单时使用 `paymentChannel: "mock"`
3. 手动调用回调接口模拟支付成功

### Q2: 支付回调没有收到怎么办？

A: 可以使用同步支付状态接口：
```bash
POST /finance/online-payment/sync/{paymentId}
```

### Q3: 如何切换到真实支付？

A:
1. 配置真实支付参数（微信/支付宝/银联）
2. 设置 `payment.{channel}.enabled=true`
3. 创建订单时使用对应的 paymentChannel
4. 配置回调 URL 白名单

### Q4: 支付金额单位是什么？

A: 金额单位是元（人民币），支持两位小数

### Q5: 支付订单有效期多久？

A:
- 微信支付：2小时
- 支付宝：30分钟
- 银联支付：15分钟
- 模拟支付：30分钟

---

## 相关文档

- [完整实现文档](./ONLINE_PAYMENT_IMPLEMENTATION.md)
- [配置示例](./payment-config-example.yml)
- [Knife4j API 文档](http://localhost:8080/doc.html)
