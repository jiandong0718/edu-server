# 通知发送记录API快速参考

## API端点列表

### 1. 分页查询发送记录
```
GET /notification/log/page
```
**查询参数**:
- `page`: 页码（默认1）
- `pageSize`: 每页数量（默认10）
- `type`: 通知类型（sms/site/email/wechat/push）
- `status`: 发送状态（pending/sending/success/failed）
- `receiver`: 接收人（支持模糊查询）
- `startDate`: 开始日期（格式: yyyy-MM-dd HH:mm:ss）
- `endDate`: 结束日期（格式: yyyy-MM-dd HH:mm:ss）
- `campusId`: 校区ID
- `bizType`: 业务类型
- `templateCode`: 模板编码

**示例**:
```bash
GET /notification/log/page?page=1&pageSize=10&status=success&type=sms
```

---

### 2. 查询发送记录详情
```
GET /notification/log/{id}
```
**路径参数**:
- `id`: 记录ID

**示例**:
```bash
GET /notification/log/123
```

---

### 3. 发送统计
```
GET /notification/log/statistics
```
**查询参数**:
- `startDate`: 开始日期（可选，默认最近30天）
- `endDate`: 结束日期（可选，默认当前时间）
- `campusId`: 校区ID（可选）

**示例**:
```bash
GET /notification/log/statistics?startDate=2024-01-01 00:00:00&endDate=2024-01-31 23:59:59
```

**返回数据包含**:
- 总发送数、成功数、失败数、待发送数、发送中数
- 成功率
- 按类型统计（sms/site/email/wechat/push）
- 按日期统计（每天的发送情况）

---

### 4. 重发失败通知
```
POST /notification/log/{id}/resend
```
**路径参数**:
- `id`: 记录ID

**业务规则**:
- 只能重发状态为"failed"的通知
- 最多重试3次
- 异步执行

**示例**:
```bash
POST /notification/log/123/resend
```

---

### 5. 批量重发
```
POST /notification/log/batch-resend
```
**请求体**:
```json
{
  "ids": [1, 2, 3, 4, 5]
}
```

**返回数据**:
```json
{
  "total": 5,
  "successCount": 3,
  "failedCount": 2,
  "successIds": [1, 2, 3],
  "failedIds": [4, 5],
  "failedItems": [
    {
      "id": 4,
      "reason": "只有发送失败的通知才能重发"
    },
    {
      "id": 5,
      "reason": "已达到最大重试次数限制"
    }
  ]
}
```

---

## 通知类型说明

| 类型 | 说明 |
|------|------|
| sms | 短信 |
| site | 站内信 |
| email | 邮件 |
| wechat | 微信 |
| push | APP推送 |

## 发送状态说明

| 状态 | 说明 |
|------|------|
| pending | 待发送 |
| sending | 发送中 |
| success | 发送成功 |
| failed | 发送失败 |

## 常见业务类型

| 业务类型 | 说明 |
|----------|------|
| class | 上课提醒 |
| homework | 作业通知 |
| payment | 缴费提醒 |
| attendance | 考勤通知 |
| system | 系统通知 |

## 测试示例

### 使用curl测试

#### 1. 查询最近的发送记录
```bash
curl -X GET "http://localhost:8080/notification/log/page?page=1&pageSize=10"
```

#### 2. 查询失败的短信记录
```bash
curl -X GET "http://localhost:8080/notification/log/page?type=sms&status=failed"
```

#### 3. 查询统计数据
```bash
curl -X GET "http://localhost:8080/notification/log/statistics"
```

#### 4. 重发单条失败通知
```bash
curl -X POST "http://localhost:8080/notification/log/123/resend"
```

#### 5. 批量重发
```bash
curl -X POST "http://localhost:8080/notification/log/batch-resend" \
  -H "Content-Type: application/json" \
  -d '{"ids": [1, 2, 3]}'
```

### 使用Knife4j测试

1. 启动应用后访问: http://localhost:8080/doc.html
2. 找到"通知发送记录管理"分组
3. 选择要测试的接口
4. 填写参数
5. 点击"执行"按钮

## 注意事项

1. **日期格式**: 所有日期参数使用格式 `yyyy-MM-dd HH:mm:ss`
2. **分页**: 默认每页10条，最大建议不超过100条
3. **重发限制**: 每条记录最多重试3次
4. **异步执行**: 重发操作是异步的，不会立即返回发送结果
5. **批量操作**: 批量重发支持部分成功，不会因个别失败而全部失败
