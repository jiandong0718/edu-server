# 课时查询和预警API快速参考

## 接口概览

### 课时查询接口（ClassHourQueryController）

基础路径：`/finance/class-hour`

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 查询学员课时余额 | GET | `/balance/{studentId}` | 根据学员ID查询课时余额 |
| 查询账户详情 | GET | `/balance/detail/{accountId}` | 根据账户ID查询详情 |
| 分页查询课时账户 | GET | `/balance/page` | 支持多条件筛选的分页查询 |

### 课时预警接口（ClassHourWarningController）

基础路径：`/finance/class-hour/warning`

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取预警列表 | GET | `/list` | 查询课时预警列表 |
| 手动触发预警检查 | POST | `/check` | 手动触发预警检查并发送通知 |

### 课时统计接口（ClassHourStatisticsController）

基础路径：`/finance/class-hour/statistics`

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 课时汇总统计 | GET | `/summary` | 统计总课时、使用率、预警数等 |
| 按课程统计 | GET | `/by-course` | 按课程维度统计课时使用情况 |
| 按学员统计 | GET | `/by-student` | 按学员维度统计课时使用情况 |
| 消课统计 | GET | `/consumption` | 统计指定时间段的消课情况 |

## 快速测试命令

### 1. 查询学员课时余额

```bash
curl -X GET "http://localhost:8080/finance/class-hour/balance/1001"
```

### 2. 分页查询课时账户

```bash
curl -X GET "http://localhost:8080/finance/class-hour/balance/page?current=1&size=10&campusId=1&status=active"
```

### 3. 获取预警列表

```bash
curl -X GET "http://localhost:8080/finance/class-hour/warning/list?warningLevel=urgent"
```

### 4. 手动触发预警检查

```bash
curl -X POST "http://localhost:8080/finance/class-hour/warning/check"
```

### 5. 课时汇总统计

```bash
curl -X GET "http://localhost:8080/finance/class-hour/statistics/summary?campusId=1"
```

### 6. 按课程统计

```bash
curl -X GET "http://localhost:8080/finance/class-hour/statistics/by-course?campusId=1"
```

### 7. 按学员统计

```bash
curl -X GET "http://localhost:8080/finance/class-hour/statistics/by-student?campusId=1"
```

### 8. 消课统计

```bash
curl -X GET "http://localhost:8080/finance/class-hour/statistics/consumption?startDate=2024-01-01&endDate=2024-01-31&campusId=1"
```

## 查询参数说明

### ClassHourBalanceQueryDTO（课时余额查询）

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| studentId | Long | 否 | 学员ID |
| courseId | Long | 否 | 课程ID |
| campusId | Long | 否 | 校区ID |
| status | String | 否 | 状态：active-正常，frozen-冻结，exhausted-已用完 |

### ClassHourWarningQueryDTO（课时预警查询）

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| warningType | String | 否 | 预警类型：low_balance-余额不足，expiring-即将过期，expired-已过期 |
| warningLevel | String | 否 | 预警级别：normal-正常，warning-警告，urgent-紧急 |
| studentId | Long | 否 | 学员ID |
| courseId | Long | 否 | 课程ID |
| campusId | Long | 否 | 校区ID |
| lowBalanceThreshold | BigDecimal | 否 | 余额不足阈值（默认5课时） |
| expiringDaysThreshold | Integer | 否 | 即将过期天数阈值（默认30天） |

### 统计接口参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| campusId | Long | 否 | 校区ID（不传则统计全部） |
| startDate | LocalDate | 是* | 开始日期（仅消课统计需要） |
| endDate | LocalDate | 是* | 结束日期（仅消课统计需要） |

## 响应数据结构

### ClassHourBalanceVO（课时余额）

```json
{
  "accountId": 1,
  "studentId": 1001,
  "studentName": "张三",
  "courseId": 2001,
  "courseName": "数学课程",
  "totalHours": 100,
  "usedHours": 45,
  "remainingHours": 55,
  "giftHours": 10,
  "frozenHours": 0,
  "availableHours": 55,
  "status": "active",
  "statusDesc": "正常",
  "isWarning": false,
  "warningReason": null,
  "createTime": "2024-01-01T10:00:00",
  "updateTime": "2024-01-31T15:00:00"
}
```

### ClassHourWarningVO（课时预警）

```json
{
  "accountId": 5,
  "studentId": 1002,
  "studentName": "李四",
  "courseId": 2001,
  "courseName": "数学课程",
  "campusId": 1,
  "campusName": "总部校区",
  "totalHours": 50,
  "usedHours": 48,
  "remainingHours": 2,
  "frozenHours": 0,
  "expiryDate": null,
  "daysToExpiry": null,
  "warningType": "low_balance",
  "warningLevel": "urgent",
  "warningMessage": "课时严重不足，仅剩2课时",
  "createTime": "2024-01-01T10:00:00"
}
```

### ClassHourSummaryVO（课时汇总统计）

```json
{
  "totalAccounts": 150,
  "activeAccounts": 120,
  "frozenAccounts": 10,
  "exhaustedAccounts": 20,
  "totalHours": 15000,
  "usedHours": 9000,
  "remainingHours": 6000,
  "giftHours": 1500,
  "frozenHours": 0,
  "usageRate": 60.00,
  "warningAccounts": 15,
  "lowBalanceWarnings": 15,
  "expiringWarnings": 0,
  "expiredWarnings": 0
}
```

### ClassHourStatisticsVO（课时统计）

```json
{
  "dimension": "course",
  "dimensionId": 2001,
  "dimensionName": "数学课程",
  "accountCount": 50,
  "totalHours": 5000,
  "usedHours": 3000,
  "remainingHours": 2000,
  "giftHours": 500,
  "usageRate": 60.00,
  "warningAccountCount": 5
}
```

### ClassHourConsumptionVO（消课统计）

```json
{
  "statisticsDate": "2024-01-31",
  "consumptionCount": 25,
  "consumptionHours": 50,
  "studentCount": 20,
  "courseCount": 5,
  "avgHoursPerConsumption": 2.00
}
```

## 预警规则

### 预警级别判断

- **urgent（紧急）**：剩余课时 ≤ 2
- **warning（警告）**：2 < 剩余课时 ≤ 5（默认阈值）
- **normal（正常）**：剩余课时 > 5

### 预警消息

- **紧急**：课时严重不足，仅剩X课时
- **警告**：课时余额不足，剩余X课时

## 注意事项

1. 所有接口都需要认证（JWT Token）
2. 分页查询默认每页10条，最大100条
3. 日期格式统一使用：yyyy-MM-dd
4. 课时数值保留2位小数
5. 使用率以百分比形式返回（0-100）
6. 预警阈值可以自定义，默认值为5课时和30天

## Swagger文档

访问地址：http://localhost:8080/doc.html

在Swagger文档中可以：
- 查看完整的接口文档
- 在线测试接口
- 查看请求/响应示例
- 下载API定义文件
