# 短信服务API文档

## 接口概述

短信服务提供了发送单条短信、批量发送短信、发送模板短信、查询发送状态等功能。

**基础路径**: `/notification/sms`

## 接口列表

### 1. 发送单条短信

**接口地址**: `POST /notification/sms/send`

**接口描述**: 发送单条短信到指定手机号

**请求参数**:

```json
{
  "phone": "13800138000",
  "content": "您的验证码是123456，5分钟内有效。",
  "receiverName": "张三",
  "receiverId": 1001,
  "campusId": 1,
  "bizType": "verify_code",
  "bizId": 12345,
  "remark": "登录验证码"
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | String | 是 | 接收人手机号，格式：1[3-9]xxxxxxxxx |
| content | String | 是 | 短信内容 |
| receiverName | String | 否 | 接收人姓名 |
| receiverId | Long | 否 | 接收人ID |
| campusId | Long | 否 | 校区ID |
| bizType | String | 否 | 业务类型 |
| bizId | Long | 否 | 业务ID |
| remark | String | 否 | 备注 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "logId": 1001,
    "phone": "13800138000",
    "status": "success",
    "thirdPartyId": "ALIYUN-12345678",
    "failReason": null,
    "sendTime": "2026-01-31T10:30:00",
    "cost": "0.05"
  }
}
```

---

### 2. 批量发送短信

**接口地址**: `POST /notification/sms/send-batch`

**接口描述**: 批量发送短信到多个手机号

**请求参数**:

```json
{
  "phones": [
    "13800138000",
    "13800138001",
    "13800138002"
  ],
  "content": "尊敬的家长，您的孩子今天表现优秀！",
  "campusId": 1,
  "bizType": "notify",
  "bizId": 12345,
  "remark": "学生表现通知"
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phones | Array | 是 | 手机号列表，最多1000个 |
| content | String | 是 | 短信内容 |
| campusId | Long | 否 | 校区ID |
| bizType | String | 否 | 业务类型 |
| bizId | Long | 否 | 业务ID |
| remark | String | 否 | 备注 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 3,
    "successCount": 3,
    "failCount": 0,
    "details": [
      {
        "logId": 1001,
        "phone": "13800138000",
        "status": "success",
        "thirdPartyId": "ALIYUN-12345678",
        "failReason": null,
        "sendTime": "2026-01-31T10:30:00",
        "cost": "0.05"
      },
      {
        "logId": 1002,
        "phone": "13800138001",
        "status": "success",
        "thirdPartyId": "ALIYUN-12345679",
        "failReason": null,
        "sendTime": "2026-01-31T10:30:01",
        "cost": "0.05"
      },
      {
        "logId": 1003,
        "phone": "13800138002",
        "status": "success",
        "thirdPartyId": "ALIYUN-12345680",
        "failReason": null,
        "sendTime": "2026-01-31T10:30:02",
        "cost": "0.05"
      }
    ]
  }
}
```

---

### 3. 发送模板短信

**接口地址**: `POST /notification/sms/send-template`

**接口描述**: 使用模板发送短信，支持单个或批量

**请求参数（单个）**:

```json
{
  "phone": "13800138000",
  "templateCode": "VERIFY_CODE",
  "params": {
    "code": "123456",
    "time": "5"
  },
  "receiverName": "张三",
  "receiverId": 1001,
  "campusId": 1,
  "bizType": "verify_code",
  "bizId": 12345,
  "remark": "登录验证码"
}
```

**请求参数（批量）**:

```json
{
  "phones": [
    "13800138000",
    "13800138001"
  ],
  "templateCode": "NOTIFY",
  "params": {
    "content": "您有新的课程安排，请及时查看。"
  },
  "campusId": 1,
  "bizType": "notify",
  "bizId": 12345,
  "remark": "课程通知"
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | String | 否 | 单个手机号（与phones二选一） |
| phones | Array | 否 | 手机号列表（与phone二选一） |
| templateCode | String | 是 | 模板编码 |
| params | Object | 否 | 模板参数 |
| receiverName | String | 否 | 接收人姓名 |
| receiverId | Long | 否 | 接收人ID |
| campusId | Long | 否 | 校区ID |
| bizType | String | 否 | 业务类型 |
| bizId | Long | 否 | 业务ID |
| remark | String | 否 | 备注 |

**常用模板**:

| 模板编码 | 说明 | 参数 |
|----------|------|------|
| VERIFY_CODE | 验证码 | code: 验证码, time: 有效时间 |
| NOTIFY | 通知 | content: 通知内容 |

**响应示例（单个）**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "logId": 1001,
    "phone": "13800138000",
    "status": "success",
    "thirdPartyId": "ALIYUN-12345678",
    "failReason": null,
    "sendTime": "2026-01-31T10:30:00",
    "cost": "0.05"
  }
}
```

**响应示例（批量）**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 2,
    "successCount": 2,
    "failCount": 0,
    "details": [...]
  }
}
```

---

### 4. 查询发送状态

**接口地址**: `GET /notification/sms/status/{id}`

**接口描述**: 根据发送记录ID查询短信发送状态

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 发送记录ID |

**请求示例**:

```
GET /notification/sms/status/1001
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "logId": 1001,
    "phone": "13800138000",
    "status": "success",
    "sendTime": "2026-01-31T10:30:00",
    "failReason": null,
    "retryCount": 0,
    "thirdPartyId": "ALIYUN-12345678",
    "thirdPartyStatus": "success"
  }
}
```

---

### 5. 重试发送

**接口地址**: `POST /notification/sms/retry/{id}`

**接口描述**: 重试发送失败的短信

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 发送记录ID |

**请求示例**:

```
POST /notification/sms/retry/1001
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "logId": 1001,
    "phone": "13800138000",
    "status": "success",
    "thirdPartyId": "ALIYUN-12345679",
    "failReason": null,
    "sendTime": "2026-01-31T10:35:00",
    "cost": "0.05"
  }
}
```

---

## 状态码说明

### 发送状态

| 状态 | 说明 |
|------|------|
| pending | 待发送 |
| sending | 发送中 |
| success | 发送成功 |
| failed | 发送失败 |

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |
| 1001 | 手机号格式不正确 |
| 1002 | 短信内容不能为空 |
| 1003 | 发送记录不存在 |
| 1004 | 只能重试失败的记录 |
| 1005 | 已达到最大重试次数 |

---

## 使用示例

### cURL示例

```bash
# 发送单条短信
curl -X POST http://localhost:8080/notification/sms/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "phone": "13800138000",
    "content": "您的验证码是123456，5分钟内有效。"
  }'

# 批量发送短信
curl -X POST http://localhost:8080/notification/sms/send-batch \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "phones": ["13800138000", "13800138001"],
    "content": "尊敬的家长，您的孩子今天表现优秀！"
  }'

# 发送模板短信
curl -X POST http://localhost:8080/notification/sms/send-template \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "phone": "13800138000",
    "templateCode": "VERIFY_CODE",
    "params": {
      "code": "123456",
      "time": "5"
    }
  }'

# 查询发送状态
curl -X GET http://localhost:8080/notification/sms/status/1001 \
  -H "Authorization: Bearer your-token"

# 重试发送
curl -X POST http://localhost:8080/notification/sms/retry/1001 \
  -H "Authorization: Bearer your-token"
```

---

## 注意事项

1. **认证**: 所有接口都需要在请求头中携带有效的JWT Token
2. **权限**: 需要有短信发送权限才能调用这些接口
3. **频率限制**: 建议控制发送频率，避免被平台限流
4. **内容规范**: 短信内容需要符合平台规范，避免包含敏感词
5. **费用**: 短信发送会产生费用，请合理使用
6. **重试机制**: 发送失败会自动重试，最多重试3次
7. **异步处理**: 批量发送采用异步处理，可能需要一定时间完成
