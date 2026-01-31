# 课时消课规则和课时调整 API 快速参考

## 消课规则管理 API

### 1. 创建消课规则
```http
POST /finance/class-hour/rule
Content-Type: application/json

{
  "name": "一对一课程规则",
  "courseId": 1,
  "classType": "one_on_one",
  "deductType": "per_class",
  "deductAmount": 1.0,
  "status": "active",
  "campusId": 1,
  "remark": "一对一课程按课次扣减"
}
```

### 2. 更新消课规则
```http
PUT /finance/class-hour/rule/{id}
Content-Type: application/json

{
  "id": 1,
  "name": "更新后的规则名称",
  "deductAmount": 1.5,
  "status": "active"
}
```

### 3. 分页查询消课规则
```http
GET /finance/class-hour/rule/page?current=1&size=10&name=一对一&status=active
```

查询参数：
- `current`: 页码（默认1）
- `size`: 每页数量（默认10）
- `name`: 规则名称（模糊查询）
- `courseId`: 课程ID
- `classType`: 班级类型
- `deductType`: 扣减类型
- `status`: 状态
- `campusId`: 校区ID

### 4. 查询规则详情
```http
GET /finance/class-hour/rule/{id}
```

### 5. 删除消课规则
```http
DELETE /finance/class-hour/rule/{id}
```

### 6. 启用消课规则
```http
PUT /finance/class-hour/rule/{id}/enable
```

### 7. 停用消课规则
```http
PUT /finance/class-hour/rule/{id}/disable
```

### 8. 获取适用的消课规则
```http
GET /finance/class-hour/rule/get-rule?courseId=1&classType=one_on_one&campusId=1
```

### 9. 计算应扣减的课时数
```http
GET /finance/class-hour/rule/calculate-deduct?courseId=1&classType=one_on_one&campusId=1&classHours=2
```

## 课时调整 API

### 1. 课时调整（单个）
```http
POST /finance/class-hour/adjust
Content-Type: application/json
```

#### 赠送课时
```json
{
  "accountId": 1,
  "adjustType": "gift",
  "hours": 10,
  "reason": "活动赠送10课时"
}
```

#### 扣减课时
```json
{
  "accountId": 1,
  "adjustType": "deduct",
  "hours": 5,
  "reason": "违规扣减5课时"
}
```

#### 撤销记录
```json
{
  "accountId": 1,
  "adjustType": "revoke",
  "originalRecordId": 100,
  "reason": "误操作，撤销之前的扣减"
}
```

### 2. 批量课时调整
```http
POST /finance/class-hour/adjust/batch
Content-Type: application/json

{
  "adjustments": [
    {
      "accountId": 1,
      "adjustType": "gift",
      "hours": 5,
      "reason": "批量赠送活动"
    },
    {
      "accountId": 2,
      "adjustType": "gift",
      "hours": 5,
      "reason": "批量赠送活动"
    },
    {
      "accountId": 3,
      "adjustType": "gift",
      "hours": 5,
      "reason": "批量赠送活动"
    }
  ],
  "needApproval": false,
  "approverId": null
}
```

## 数据字典

### 班级类型 (classType)
- `one_on_one`: 一对一
- `small_class`: 小班课
- `large_class`: 大班课

### 扣减类型 (deductType)
- `per_hour`: 按课时（实际课时 × 扣减系数）
- `per_class`: 按课次（固定扣减数量）
- `custom`: 自定义（使用配置的扣减数量）

### 规则状态 (status)
- `active`: 启用
- `inactive`: 停用

### 调整类型 (adjustType)
- `gift`: 赠送（增加课时）
- `deduct`: 扣减（减少课时）
- `revoke`: 撤销（撤销之前的消耗记录）

## 响应格式

### 成功响应
```json
{
  "code": 200,
  "msg": "success",
  "data": { ... },
  "timestamp": 1706745600000
}
```

### 失败响应
```json
{
  "code": 500,
  "msg": "错误信息",
  "data": null,
  "timestamp": 1706745600000
}
```

## 规则匹配优先级

消课规则按以下优先级匹配（从高到低）：
1. 课程ID + 班级类型 + 校区ID
2. 课程ID + 班级类型
3. 班级类型 + 校区ID
4. 班级类型
5. 默认规则（所有条件为空）

## 使用示例

### 场景1：为一对一课程创建专属规则

```bash
# 1. 创建规则
curl -X POST http://localhost:8080/finance/class-hour/rule \
  -H "Content-Type: application/json" \
  -d '{
    "name": "一对一课程规则",
    "classType": "one_on_one",
    "deductType": "per_class",
    "deductAmount": 1.0,
    "status": "active",
    "remark": "一对一课程每次上课扣减1课时"
  }'

# 2. 验证规则
curl -X GET "http://localhost:8080/finance/class-hour/rule/calculate-deduct?classType=one_on_one&classHours=2"
# 返回: 1.0 (按课次扣减，不管实际上了几课时)
```

### 场景2：批量赠送课时

```bash
curl -X POST http://localhost:8080/finance/class-hour/adjust/batch \
  -H "Content-Type: application/json" \
  -d '{
    "adjustments": [
      {"accountId": 1, "adjustType": "gift", "hours": 10, "reason": "新年活动赠送"},
      {"accountId": 2, "adjustType": "gift", "hours": 10, "reason": "新年活动赠送"},
      {"accountId": 3, "adjustType": "gift", "hours": 10, "reason": "新年活动赠送"}
    ],
    "needApproval": false
  }'
```

### 场景3：撤销误操作

```bash
# 1. 先查询要撤销的记录ID
curl -X GET "http://localhost:8080/finance/class-hour/record?accountId=1"

# 2. 撤销记录
curl -X POST http://localhost:8080/finance/class-hour/adjust \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "adjustType": "revoke",
    "originalRecordId": 100,
    "reason": "误操作，需要撤销"
  }'
```

## 注意事项

1. **规则创建**
   - 相同条件（课程ID、班级类型、校区ID）的规则不能重复创建
   - 建议先创建通用规则，再创建特殊规则

2. **课时调整**
   - 赠送课时数必须大于0
   - 扣减课时不能超过账户余额
   - 撤销操作只能撤销"消耗"类型的记录

3. **批量操作**
   - 批量调整支持部分成功
   - 返回结果中包含每个账户的调整状态
   - 建议先测试单个调整，确认无误后再批量操作

4. **权限控制**
   - 课时调整操作需要相应权限
   - 批量调整建议启用审批流程
   - 撤销操作需要更高级别的权限

## 常见问题

### Q1: 如何设置默认规则？
A: 创建规则时，将 courseId、classType、campusId 都设置为 null，即为默认规则。

### Q2: 规则优先级如何工作？
A: 系统会按照优先级从高到低查找匹配的规则，找到第一个匹配的规则就停止查找。

### Q3: 批量调整失败怎么办？
A: 批量调整支持部分成功，返回结果中会标明每个账户的调整状态。失败的账户可以单独重试。

### Q4: 如何查看调整历史？
A: 所有调整操作都会记录在 fin_class_hour_record 表中，可以通过查询记录接口查看。

### Q5: 撤销操作会影响什么？
A: 撤销操作会将之前扣减的课时加回账户，并创建一条"撤销"类型的记录。原记录不会被删除。
