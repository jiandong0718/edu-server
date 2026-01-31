# 试听管理 API 接口文档

## 基础信息

**模块**: 招生管理 - 试听管理
**基础路径**: `/marketing/trial`
**认证方式**: JWT Token

## 接口列表

### 1. 创建试听预约

创建新的试听预约记录。

**接口地址**: `POST /marketing/trial/appointment`

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
    "leadId": 1,
    "studentId": null,
    "courseId": 1,
    "classId": 1,
    "scheduleId": null,
    "campusId": 1,
    "trialDate": "2024-02-01",
    "trialTime": "10:00:00",
    "advisorId": 1,
    "remark": "首次试听"
}
```

**请求参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| leadId | Long | 否 | 线索ID（leadId和studentId至少填一个） |
| studentId | Long | 否 | 学员ID（leadId和studentId至少填一个） |
| courseId | Long | 是 | 课程ID |
| classId | Long | 否 | 班级ID |
| scheduleId | Long | 否 | 排课ID |
| campusId | Long | 是 | 校区ID |
| trialDate | Date | 是 | 试听日期（格式：yyyy-MM-dd） |
| trialTime | Time | 是 | 试听时间（格式：HH:mm:ss） |
| advisorId | Long | 否 | 顾问ID |
| remark | String | 否 | 备注信息 |

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": 1234567890
}
```

**业务规则**:
1. leadId 和 studentId 至少提供一个
2. 如果提供 leadId，会自动更新线索状态为"已预约"
3. 试听记录初始状态为"appointed"（已预约）

---

### 2. 分页查询试听记录列表（含关联信息）

查询试听记录列表，包含线索、学员、课程等关联信息。

**接口地址**: `GET /marketing/trial/page-vo`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| pageNum | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页数量（默认10） |
| leadId | Long | 否 | 线索ID |
| studentId | Long | 否 | 学员ID |
| courseId | Long | 否 | 课程ID |
| classId | Long | 否 | 班级ID |
| campusId | Long | 否 | 校区ID |
| status | String | 否 | 状态（appointed/attended/absent/converted） |
| advisorId | Long | 否 | 顾问ID |
| trialDateStart | Date | 否 | 试听日期开始（格式：yyyy-MM-dd） |
| trialDateEnd | Date | 否 | 试听日期结束（格式：yyyy-MM-dd） |
| leadName | String | 否 | 线索姓名（模糊查询） |
| studentName | String | 否 | 学员姓名（模糊查询） |
| phone | String | 否 | 手机号（模糊查询） |

**请求示例**:
```
GET /marketing/trial/page-vo?pageNum=1&pageSize=10&campusId=1&status=appointed
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "records": [
            {
                "id": 1,
                "leadId": 1,
                "leadName": "张三",
                "leadPhone": "13800138000",
                "studentId": null,
                "studentName": null,
                "studentPhone": null,
                "courseId": 1,
                "courseName": "数学课程",
                "classId": 1,
                "className": "一年级A班",
                "scheduleId": null,
                "campusId": 1,
                "campusName": "总部校区",
                "trialDate": "2024-02-01",
                "trialTime": "10:00:00",
                "status": "appointed",
                "statusDesc": "已预约",
                "feedback": null,
                "rating": null,
                "advisorId": 1,
                "advisorName": "李老师",
                "remark": "首次试听",
                "createTime": "2024-01-30T10:00:00",
                "updateTime": "2024-01-30T10:00:00"
            }
        ],
        "total": 1,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

---

### 3. 获取试听记录详情

根据ID获取试听记录详细信息。

**接口地址**: `GET /marketing/trial/{id}`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| id | Long | 是 | 试听记录ID |

**请求示例**:
```
GET /marketing/trial/1234567890
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "id": 1234567890,
        "leadId": 1,
        "studentId": null,
        "courseId": 1,
        "classId": 1,
        "scheduleId": null,
        "campusId": 1,
        "trialDate": "2024-02-01",
        "trialTime": "10:00:00",
        "status": "appointed",
        "feedback": null,
        "rating": null,
        "advisorId": 1,
        "remark": "首次试听",
        "createTime": "2024-01-30T10:00:00",
        "updateTime": "2024-01-30T10:00:00"
    }
}
```

---

### 4. 试听签到

对已预约的试听记录进行签到操作。

**接口地址**: `POST /marketing/trial/sign-in`

**请求体**:
```json
{
    "trialId": 1234567890,
    "status": "attended",
    "remark": "学员准时到场"
}
```

**请求参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| trialId | Long | 是 | 试听记录ID |
| status | String | 是 | 签到状态（attended-已到场，absent-未到场） |
| remark | String | 否 | 备注信息 |

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": true
}
```

**业务规则**:
1. 只有状态为"appointed"（已预约）的记录才能签到
2. 签到成功（attended）会自动更新关联线索状态为"已试听"
3. status 只能是 "attended" 或 "absent"

---

### 5. 提交试听反馈

对已到场的试听记录提交反馈和评分。

**接口地址**: `POST /marketing/trial/feedback`

**请求体**:
```json
{
    "trialId": 1234567890,
    "feedback": "学员表现良好，对课程内容很感兴趣，建议报名正式课程。",
    "rating": 5,
    "remark": "建议报名"
}
```

**请求参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| trialId | Long | 是 | 试听记录ID |
| feedback | String | 是 | 试听反馈内容 |
| rating | Integer | 是 | 评分（1-5） |
| remark | String | 否 | 备注信息 |

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": true
}
```

**业务规则**:
1. 只有状态为"attended"（已到场）的记录才能提交反馈
2. 评分必须在 1-5 之间
3. 反馈内容不能为空

**评分标准**:
- 1分: 非常不满意
- 2分: 不满意
- 3分: 一般
- 4分: 满意
- 5分: 非常满意

---

### 6. 取消试听预约

取消已预约的试听记录。

**接口地址**: `DELETE /marketing/trial/{id}/cancel`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| id | Long | 是 | 试听记录ID |

**请求示例**:
```
DELETE /marketing/trial/1234567890/cancel
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": true
}
```

**业务规则**:
1. 只有状态为"appointed"（已预约）的记录才能取消
2. 取消后会逻辑删除试听记录
3. 如果有关联线索，会恢复线索状态为"跟进中"

---

### 7. 获取线索的试听记录列表

查询指定线索的所有试听记录。

**接口地址**: `GET /marketing/trial/lead/{leadId}`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| leadId | Long | 是 | 线索ID |

**请求示例**:
```
GET /marketing/trial/lead/1
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": [
        {
            "id": 1,
            "leadId": 1,
            "studentId": null,
            "courseId": 1,
            "classId": 1,
            "campusId": 1,
            "trialDate": "2024-02-01",
            "trialTime": "10:00:00",
            "status": "attended",
            "feedback": "表现良好",
            "rating": 5,
            "advisorId": 1,
            "remark": "首次试听",
            "createTime": "2024-01-30T10:00:00",
            "updateTime": "2024-01-30T11:00:00"
        }
    ]
}
```

---

### 8. 获取学员的试听记录列表

查询指定学员的所有试听记录。

**接口地址**: `GET /marketing/trial/student/{studentId}`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| studentId | Long | 是 | 学员ID |

**请求示例**:
```
GET /marketing/trial/student/1
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": [
        {
            "id": 2,
            "leadId": null,
            "studentId": 1,
            "courseId": 2,
            "classId": 2,
            "campusId": 1,
            "trialDate": "2024-02-05",
            "trialTime": "14:00:00",
            "status": "appointed",
            "feedback": null,
            "rating": null,
            "advisorId": 1,
            "remark": "第二次试听",
            "createTime": "2024-01-31T10:00:00",
            "updateTime": "2024-01-31T10:00:00"
        }
    ]
}
```

---

### 9. 获取招生转化漏斗统计

统计招生转化各阶段的数据和转化率。

**接口地址**: `GET /marketing/trial/conversion-funnel`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| campusId | Long | 否 | 校区ID |
| startDate | Date | 否 | 开始日期（格式：yyyy-MM-dd） |
| endDate | Date | 否 | 结束日期（格式：yyyy-MM-dd） |

**请求示例**:
```
GET /marketing/trial/conversion-funnel?campusId=1&startDate=2024-01-01&endDate=2024-01-31
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "newLeadCount": 100,
        "followingCount": 80,
        "appointedCount": 60,
        "trialedCount": 50,
        "convertedCount": 30,
        "lostCount": 20,
        "appointmentRate": 60.00,
        "trialRate": 50.00,
        "conversionRate": 30.00,
        "overallRate": 30.00
    }
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|-----|------|------|
| newLeadCount | Integer | 新线索数量 |
| followingCount | Integer | 跟进中数量 |
| appointedCount | Integer | 已预约数量 |
| trialedCount | Integer | 已试听数量 |
| convertedCount | Integer | 已成交数量 |
| lostCount | Integer | 已流失数量 |
| appointmentRate | BigDecimal | 预约转化率（%） |
| trialRate | BigDecimal | 试听转化率（%） |
| conversionRate | BigDecimal | 成交转化率（%） |
| overallRate | BigDecimal | 整体转化率（%） |

---

### 10. 获取顾问业绩统计

统计顾问的线索跟进和转化业绩。

**接口地址**: `GET /marketing/trial/advisor-performance`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| advisorId | Long | 否 | 顾问ID（为空则查询所有顾问） |
| campusId | Long | 否 | 校区ID |
| startDate | Date | 否 | 开始日期（格式：yyyy-MM-dd） |
| endDate | Date | 否 | 结束日期（格式：yyyy-MM-dd） |

**请求示例**:
```
GET /marketing/trial/advisor-performance?campusId=1&startDate=2024-01-01&endDate=2024-01-31
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": [
        {
            "advisorId": 1,
            "advisorName": "李老师",
            "totalLeadCount": 50,
            "followUpCount": 120,
            "appointmentCount": 30,
            "trialCount": 25,
            "conversionCount": 15,
            "conversionAmount": 150000.00,
            "conversionRate": 30.00,
            "avgFollowUpCount": 2.40
        },
        {
            "advisorId": 2,
            "advisorName": "王老师",
            "totalLeadCount": 40,
            "followUpCount": 100,
            "appointmentCount": 25,
            "trialCount": 20,
            "conversionCount": 12,
            "conversionAmount": 120000.00,
            "conversionRate": 30.00,
            "avgFollowUpCount": 2.50
        }
    ]
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|-----|------|------|
| advisorId | Long | 顾问ID |
| advisorName | String | 顾问姓名 |
| totalLeadCount | Integer | 线索总数 |
| followUpCount | Integer | 跟进次数 |
| appointmentCount | Integer | 预约数量 |
| trialCount | Integer | 试听数量 |
| conversionCount | Integer | 成交数量 |
| conversionAmount | BigDecimal | 成交金额 |
| conversionRate | BigDecimal | 转化率（%） |
| avgFollowUpCount | BigDecimal | 平均跟进次数 |

---

## 状态说明

### 试听状态 (status)

| 状态值 | 中文描述 | 说明 |
|-------|---------|------|
| appointed | 已预约 | 初始状态，预约成功 |
| attended | 已到场 | 签到成功，学员已到场 |
| absent | 未到场 | 签到失败，学员未到场 |
| converted | 已转化 | 成功转化为正式学员 |

### 状态流转

```
appointed（已预约）
    ↓ 签到
attended（已到场）或 absent（未到场）
    ↓ 提交反馈
attended（已到场，含反馈）
    ↓ 转化
converted（已转化）
```

---

## 错误码说明

| 错误码 | 说明 |
|-------|------|
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 业务错误示例

```json
{
    "code": 400,
    "msg": "线索ID和学员ID至少需要提供一个",
    "data": null
}
```

```json
{
    "code": 400,
    "msg": "只有已预约状态的试听记录才能签到",
    "data": null
}
```

```json
{
    "code": 400,
    "msg": "评分必须在1-5之间",
    "data": null
}
```

---

## 使用示例

### 完整业务流程示例

#### 1. 创建试听预约
```bash
curl -X POST http://localhost:8080/marketing/trial/appointment \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "leadId": 1,
    "courseId": 1,
    "campusId": 1,
    "trialDate": "2024-02-01",
    "trialTime": "10:00:00",
    "advisorId": 1
  }'
```

#### 2. 查询试听列表
```bash
curl -X GET "http://localhost:8080/marketing/trial/page-vo?pageNum=1&pageSize=10&status=appointed" \
  -H "Authorization: Bearer {token}"
```

#### 3. 试听签到
```bash
curl -X POST http://localhost:8080/marketing/trial/sign-in \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "trialId": 1234567890,
    "status": "attended",
    "remark": "学员准时到场"
  }'
```

#### 4. 提交反馈
```bash
curl -X POST http://localhost:8080/marketing/trial/feedback \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "trialId": 1234567890,
    "feedback": "学员表现良好，建议报名",
    "rating": 5
  }'
```

#### 5. 查看转化漏斗
```bash
curl -X GET "http://localhost:8080/marketing/trial/conversion-funnel?campusId=1" \
  -H "Authorization: Bearer {token}"
```

---

## 注意事项

1. **认证**: 所有接口都需要在请求头中携带有效的 JWT Token
2. **日期格式**: 日期参数统一使用 `yyyy-MM-dd` 格式
3. **时间格式**: 时间参数统一使用 `HH:mm:ss` 格式
4. **分页**: 默认每页10条，最大支持100条
5. **状态流转**: 必须按照规定的状态流转顺序操作
6. **数据权限**: 根据用户角色和校区权限过滤数据

---

## 更新日志

### v1.0.0 (2024-01-30)
- 初始版本发布
- 实现试听预约、签到、反馈等核心功能
- 实现转化漏斗和顾问业绩统计
