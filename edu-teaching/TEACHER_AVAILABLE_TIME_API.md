# 教师可用时间配置 API 文档

## 概述

教师可用时间配置功能用于管理教师每周的可用时间段，支持排课时的时间冲突检测。

## 功能特性

- ✅ 教师可用时间的增删改查
- ✅ 批量设置教师可用时间
- ✅ 时间格式验证（HH:mm）
- ✅ 时间段有效性验证（开始时间 < 结束时间）
- ✅ 时间段冲突检测（同一天的时间段不能重叠）
- ✅ 教师可用性检查（用于排课）
- ✅ 启用/禁用时间段

## 数据模型

### 数据库表：tch_teacher_available_time

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| teacher_id | BIGINT | 教师ID |
| day_of_week | TINYINT | 星期几（1-7，1表示周一） |
| start_time | TIME | 开始时间 |
| end_time | TIME | 结束时间 |
| status | TINYINT | 状态（0-禁用，1-启用） |
| remark | VARCHAR(500) | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | BIGINT | 创建人 |
| update_by | BIGINT | 更新人 |
| deleted | TINYINT | 删除标记 |

## API 接口

### 1. 获取教师可用时间列表

**接口地址：** `GET /teaching/teacher/available-time/list`

**请求参数：**
- `teacherId` (Long, 必填): 教师ID

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "teacherId": 100,
      "dayOfWeek": 1,
      "startTime": "09:00",
      "endTime": "12:00",
      "status": 1,
      "remark": "周一上午"
    },
    {
      "id": 2,
      "teacherId": 100,
      "dayOfWeek": 1,
      "startTime": "14:00",
      "endTime": "17:00",
      "status": 1,
      "remark": "周一下午"
    }
  ]
}
```

### 2. 获取教师可用时间列表（VO格式）

**接口地址：** `GET /teaching/teacher/available-time/list-vo`

**请求参数：**
- `teacherId` (Long, 必填): 教师ID

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "teacherId": 100,
      "teacherName": "张老师",
      "dayOfWeek": 1,
      "dayOfWeekName": "周一",
      "startTime": "09:00",
      "endTime": "12:00",
      "status": 1,
      "statusName": "启用",
      "remark": "周一上午",
      "createTime": "2026-01-31T10:00:00",
      "updateTime": "2026-01-31T10:00:00"
    }
  ]
}
```

### 3. 获取可用时间详情

**接口地址：** `GET /teaching/teacher/available-time/{id}`

**路径参数：**
- `id` (Long, 必填): 可用时间ID

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "teacherId": 100,
    "dayOfWeek": 1,
    "startTime": "09:00",
    "endTime": "12:00",
    "status": 1,
    "remark": "周一上午"
  }
}
```

### 4. 新增可用时间

**接口地址：** `POST /teaching/teacher/available-time`

**请求体：**
```json
{
  "teacherId": 100,
  "dayOfWeek": 1,
  "startTime": "09:00",
  "endTime": "12:00",
  "status": 1,
  "remark": "周一上午"
}
```

**验证规则：**
- `teacherId`: 必填
- `dayOfWeek`: 必填，范围1-7
- `startTime`: 必填，格式HH:mm
- `endTime`: 必填，格式HH:mm
- 开始时间必须小于结束时间
- 不能与该教师同一天的其他时间段冲突

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**错误示例：**
```json
{
  "code": 500,
  "message": "周一的时间段09:00-12:00与已有时间段冲突",
  "data": null
}
```

### 5. 修改可用时间

**接口地址：** `PUT /teaching/teacher/available-time`

**请求体：**
```json
{
  "id": 1,
  "teacherId": 100,
  "dayOfWeek": 1,
  "startTime": "09:00",
  "endTime": "13:00",
  "status": 1,
  "remark": "周一上午延长"
}
```

**验证规则：** 同新增接口

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 6. 删除可用时间

**接口地址：** `DELETE /teaching/teacher/available-time/{id}`

**路径参数：**
- `id` (Long, 必填): 可用时间ID

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 7. 批量保存教师可用时间（新版本）

**接口地址：** `POST /teaching/teacher/available-time/batch-save`

**说明：** 先删除该教师的所有可用时间配置，再批量保存新的配置。包含完整的时间验证和冲突检测。

**请求体：**
```json
{
  "teacherId": 100,
  "timeSlots": [
    {
      "dayOfWeek": 1,
      "startTime": "09:00",
      "endTime": "12:00",
      "remark": "周一上午"
    },
    {
      "dayOfWeek": 1,
      "startTime": "14:00",
      "endTime": "17:00",
      "remark": "周一下午"
    },
    {
      "dayOfWeek": 2,
      "startTime": "09:00",
      "endTime": "12:00",
      "remark": "周二上午"
    },
    {
      "dayOfWeek": 3,
      "startTime": "14:00",
      "endTime": "18:00",
      "remark": "周三下午"
    }
  ]
}
```

**验证规则：**
- 每个时间段都会进行格式和有效性验证
- 检查同一天的时间段是否有冲突
- 如果有任何验证失败，整个操作回滚

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**错误示例：**
```json
{
  "code": 500,
  "message": "周一的时间段存在冲突：09:00-12:00 与 10:00-13:00",
  "data": null
}
```

### 8. 批量保存教师可用时间（旧版本）

**接口地址：** `POST /teaching/teacher/available-time/batch`

**说明：** 保持向后兼容的旧版本接口，不包含验证。

**请求参数：**
- `teacherId` (Long, 必填): 教师ID

**请求体：**
```json
[
  {
    "dayOfWeek": 1,
    "startTime": "09:00",
    "endTime": "12:00",
    "status": 1,
    "remark": "周一上午"
  },
  {
    "dayOfWeek": 1,
    "startTime": "14:00",
    "endTime": "17:00",
    "status": 1,
    "remark": "周一下午"
  }
]
```

### 9. 检查教师在指定时间是否可用

**接口地址：** `GET /teaching/teacher/available-time/check-available`

**说明：** 用于排课时检查教师在指定时间段是否可用。

**请求参数：**
- `teacherId` (Long, 必填): 教师ID
- `dayOfWeek` (Integer, 必填): 星期几（1-7）
- `startTime` (String, 必填): 开始时间（HH:mm）
- `endTime` (String, 必填): 结束时间（HH:mm）

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**说明：**
- `data: true` 表示教师在该时间段可用
- `data: false` 表示教师在该时间段不可用
- 只有当教师的可用时间段完全包含请求的时间段时，才返回true

**使用场景：**
```
教师可用时间：周一 09:00-17:00

查询1：周一 10:00-12:00 → 返回 true（完全包含）
查询2：周一 08:00-10:00 → 返回 false（部分超出）
查询3：周一 16:00-18:00 → 返回 false（部分超出）
查询4：周二 10:00-12:00 → 返回 false（不同日期）
```

### 10. 启用可用时间

**接口地址：** `PUT /teaching/teacher/available-time/{id}/enable`

**路径参数：**
- `id` (Long, 必填): 可用时间ID

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

### 11. 禁用可用时间

**接口地址：** `PUT /teaching/teacher/available-time/{id}/disable`

**路径参数：**
- `id` (Long, 必填): 可用时间ID

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

## 业务规则

### 时间格式

- 时间格式必须为 `HH:mm`（24小时制）
- 示例：`09:00`, `14:30`, `23:59`

### 星期几编码

- 1: 周一
- 2: 周二
- 3: 周三
- 4: 周四
- 5: 周五
- 6: 周六
- 7: 周日

### 时间段验证规则

1. **格式验证**：时间必须符合 HH:mm 格式
2. **有效性验证**：开始时间必须小于结束时间
3. **冲突检测**：同一教师在同一天的时间段不能重叠

### 时间段冲突判断

两个时间段 [start1, end1] 和 [start2, end2] 冲突的条件：
```
start1 < end2 AND start2 < end1
```

**示例：**
- `09:00-12:00` 与 `10:00-13:00` → 冲突
- `09:00-12:00` 与 `12:00-15:00` → 不冲突（边界不算冲突）
- `09:00-12:00` 与 `14:00-17:00` → 不冲突

### 教师可用性检查

教师在指定时间可用的条件：
- 存在至少一个启用状态的可用时间段
- 该时间段的星期几与请求相同
- 该时间段完全包含请求的时间段

**示例：**
```
教师可用时间：周一 09:00-17:00

✅ 查询 周一 10:00-12:00 → 可用（完全包含）
✅ 查询 周一 09:00-17:00 → 可用（完全相同）
❌ 查询 周一 08:00-10:00 → 不可用（开始时间超出）
❌ 查询 周一 16:00-18:00 → 不可用（结束时间超出）
❌ 查询 周二 10:00-12:00 → 不可用（不同日期）
```

## 使用场景

### 场景1：设置教师每周可用时间

教师张老师每周的工作时间为：
- 周一至周五：上午 09:00-12:00，下午 14:00-17:00
- 周六：上午 09:00-12:00

使用批量保存接口一次性设置：

```bash
POST /teaching/teacher/available-time/batch-save
Content-Type: application/json

{
  "teacherId": 100,
  "timeSlots": [
    {"dayOfWeek": 1, "startTime": "09:00", "endTime": "12:00", "remark": "周一上午"},
    {"dayOfWeek": 1, "startTime": "14:00", "endTime": "17:00", "remark": "周一下午"},
    {"dayOfWeek": 2, "startTime": "09:00", "endTime": "12:00", "remark": "周二上午"},
    {"dayOfWeek": 2, "startTime": "14:00", "endTime": "17:00", "remark": "周二下午"},
    {"dayOfWeek": 3, "startTime": "09:00", "endTime": "12:00", "remark": "周三上午"},
    {"dayOfWeek": 3, "startTime": "14:00", "endTime": "17:00", "remark": "周三下午"},
    {"dayOfWeek": 4, "startTime": "09:00", "endTime": "12:00", "remark": "周四上午"},
    {"dayOfWeek": 4, "startTime": "14:00", "endTime": "17:00", "remark": "周四下午"},
    {"dayOfWeek": 5, "startTime": "09:00", "endTime": "12:00", "remark": "周五上午"},
    {"dayOfWeek": 5, "startTime": "14:00", "endTime": "17:00", "remark": "周五下午"},
    {"dayOfWeek": 6, "startTime": "09:00", "endTime": "12:00", "remark": "周六上午"}
  ]
}
```

### 场景2：排课时检查教师是否可用

在创建课程安排时，需要检查教师在指定时间是否可用：

```bash
GET /teaching/teacher/available-time/check-available?teacherId=100&dayOfWeek=1&startTime=10:00&endTime=11:30
```

如果返回 `true`，则可以安排该教师在周一 10:00-11:30 上课。

### 场景3：临时调整教师可用时间

教师某天临时有事，需要禁用某个时间段：

```bash
PUT /teaching/teacher/available-time/5/disable
```

恢复时再启用：

```bash
PUT /teaching/teacher/available-time/5/enable
```

## 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 500 | 星期几必须在1-7之间 | dayOfWeek参数不在有效范围 |
| 500 | 时间格式错误，必须为HH:mm格式 | 时间格式不正确 |
| 500 | 开始时间必须小于结束时间 | 时间段无效 |
| 500 | 周X的时间段X-X与已有时间段冲突 | 时间段冲突 |
| 500 | 可用时间ID不能为空 | 更新时未提供ID |

## 技术实现

### 核心类

1. **Entity**: `TeacherAvailableTime` - 实体类
2. **DTO**:
   - `TeacherAvailableTimeDTO` - 单个时间段DTO
   - `BatchSaveAvailableTimeDTO` - 批量保存DTO
3. **VO**: `TeacherAvailableTimeVO` - 视图对象
4. **Mapper**: `TeacherAvailableTimeMapper` - MyBatis Mapper
5. **Service**: `TeacherAvailableTimeService` - 服务接口
6. **ServiceImpl**: `TeacherAvailableTimeServiceImpl` - 服务实现
7. **Controller**: `TeacherAvailableTimeController` - 控制器

### 关键方法

#### 时间段验证
```java
void validateTimeSlot(Integer dayOfWeek, String startTime, String endTime)
```

#### 时间冲突检测
```java
boolean checkTimeConflict(Long teacherId, Integer dayOfWeek,
                         String startTime, String endTime, Long excludeId)
```

#### 教师可用性检查
```java
boolean isTeacherAvailable(Long teacherId, Integer dayOfWeek,
                          String startTime, String endTime)
```

### 事务管理

所有写操作（新增、修改、删除、批量保存）都使用 `@Transactional` 注解，确保数据一致性。

### 验证框架

使用 Jakarta Validation (JSR-380) 进行参数验证：
- `@NotNull`: 非空验证
- `@NotBlank`: 非空字符串验证
- `@Min/@Max`: 数值范围验证
- `@Pattern`: 正则表达式验证
- `@Valid`: 嵌套对象验证

## 测试建议

### 单元测试

1. 时间格式验证测试
2. 时间段有效性验证测试
3. 时间冲突检测测试
4. 教师可用性检查测试

### 集成测试

1. 完整的CRUD操作测试
2. 批量保存测试
3. 并发操作测试
4. 事务回滚测试

### 测试用例示例

```java
// 测试时间冲突检测
@Test
public void testTimeConflict() {
    // 已存在：周一 09:00-12:00
    // 测试：周一 10:00-13:00 → 应该冲突
    boolean conflict = service.checkTimeConflict(100L, 1, "10:00", "13:00", null);
    assertTrue(conflict);

    // 测试：周一 12:00-15:00 → 不应该冲突（边界）
    conflict = service.checkTimeConflict(100L, 1, "12:00", "15:00", null);
    assertFalse(conflict);
}
```

## 注意事项

1. **时间格式**：数据库使用TIME类型，Java代码使用String类型（HH:mm格式），MyBatis会自动转换
2. **时区问题**：时间不涉及日期和时区，只是时间段
3. **并发控制**：批量保存操作会先删除再插入，需要在事务中执行
4. **软删除**：使用MyBatis-Plus的逻辑删除功能，deleted字段标记删除状态
5. **数据隔离**：如果需要多校区隔离，可以在查询时添加campus_id条件

## 后续扩展

1. **重复模式**：支持设置重复模式（如：每周、每两周）
2. **临时调整**：支持临时调整某一天的可用时间
3. **假期管理**：集成假期管理，自动排除假期时间
4. **时间模板**：支持创建时间模板，快速应用到多个教师
5. **历史记录**：记录可用时间的变更历史
