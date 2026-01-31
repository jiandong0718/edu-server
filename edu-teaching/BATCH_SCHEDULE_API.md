# 批量排课接口文档

## 概述

批量排课功能支持按规则自动生成排课计划，可以设置重复规则（每周固定时间）、跳过节假日、设置总课次等。

## 接口信息

### 批量排课（增强版本）

**接口地址**: `POST /teaching/schedule/batch-enhanced`

**接口描述**: 支持按规则自动生成排课计划，支持跳过节假日、设置总课次等高级功能

**请求参数**:

```json
{
  "classId": 1001,
  "courseId": 2001,
  "teacherId": 3001,
  "classroomId": 4001,
  "startDate": "2024-01-01",
  "endDate": "2024-06-30",
  "totalLessons": 40,
  "weekdays": [1, 3, 5],
  "startTime": "09:00",
  "endTime": "11:00",
  "classHours": 2,
  "skipHolidays": true,
  "skipWeekends": false,
  "repeatRule": "weekly",
  "topicPrefix": "第{n}课",
  "remark": "春季班排课"
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| classId | Long | 是 | 班级ID |
| courseId | Long | 否 | 课程ID（可选，默认使用班级关联的课程） |
| teacherId | Long | 否 | 教师ID（可选，默认使用班级关联的教师） |
| classroomId | Long | 否 | 教室ID（可选，默认使用班级关联的教室） |
| startDate | String | 是 | 开始日期（格式：yyyy-MM-dd） |
| endDate | String | 否 | 结束日期（与totalLessons二选一） |
| totalLessons | Integer | 否 | 总课次（与endDate二选一） |
| weekdays | List<Integer> | 是 | 上课星期：1-7（1表示周一，7表示周日） |
| startTime | String | 是 | 开始时间（格式：HH:mm） |
| endTime | String | 是 | 结束时间（格式：HH:mm） |
| classHours | Integer | 否 | 每节课时数（默认：2） |
| skipHolidays | Boolean | 否 | 是否跳过节假日（默认：true） |
| skipWeekends | Boolean | 否 | 是否跳过周末（默认：false） |
| repeatRule | String | 否 | 重复规则：weekly-每周，monthly-每月（默认：weekly） |
| topicPrefix | String | 否 | 课节主题前缀（{n}会被替换为课节序号） |
| remark | String | 否 | 备注 |

**响应示例**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "successCount": 38,
    "skippedCount": 5,
    "failedCount": 0,
    "scheduleIds": [10001, 10002, 10003, ...],
    "skippedDates": [
      {
        "date": "2024-01-01",
        "reason": "节假日"
      },
      {
        "date": "2024-02-10",
        "reason": "节假日"
      },
      {
        "date": "2024-03-15",
        "reason": "时间冲突（教师或教室已被占用）"
      }
    ],
    "failedDates": []
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| successCount | Integer | 成功创建的排课数量 |
| skippedCount | Integer | 跳过的日期数量 |
| failedCount | Integer | 失败的日期数量 |
| scheduleIds | List<Long> | 创建的排课ID列表 |
| skippedDates | List | 跳过的日期列表 |
| skippedDates[].date | String | 跳过的日期 |
| skippedDates[].reason | String | 跳过原因 |
| failedDates | List | 失败的日期列表 |
| failedDates[].date | String | 失败的日期 |
| failedDates[].reason | String | 失败原因 |

## 使用场景

### 场景1：按日期范围排课

每周一、三、五上课，从2024年1月1日到2024年6月30日：

```json
{
  "classId": 1001,
  "startDate": "2024-01-01",
  "endDate": "2024-06-30",
  "weekdays": [1, 3, 5],
  "startTime": "09:00",
  "endTime": "11:00",
  "skipHolidays": true
}
```

### 场景2：按总课次排课

每周一、三、五上课，共40节课：

```json
{
  "classId": 1001,
  "startDate": "2024-01-01",
  "totalLessons": 40,
  "weekdays": [1, 3, 5],
  "startTime": "09:00",
  "endTime": "11:00",
  "skipHolidays": true
}
```

### 场景3：周末班排课

每周六、日上课，不跳过周末：

```json
{
  "classId": 1001,
  "startDate": "2024-01-01",
  "endDate": "2024-06-30",
  "weekdays": [6, 7],
  "startTime": "14:00",
  "endTime": "16:00",
  "skipHolidays": true,
  "skipWeekends": false
}
```

### 场景4：自定义课节主题

设置课节主题前缀，自动生成"第1课"、"第2课"等：

```json
{
  "classId": 1001,
  "startDate": "2024-01-01",
  "totalLessons": 40,
  "weekdays": [1, 3, 5],
  "startTime": "09:00",
  "endTime": "11:00",
  "topicPrefix": "第{n}课",
  "skipHolidays": true
}
```

## 业务规则

### 1. 节假日处理

- 当 `skipHolidays=true` 时，系统会自动跳过节假日
- 系统会查询 `sys_holiday` 表，根据校区ID和日期判断是否为节假日
- 支持调休工作日：如果某天是调休工作日（`is_workday=1`），则正常排课

### 2. 冲突检测

系统会自动检测以下冲突：

- **教师冲突**：同一教师在同一时间段已有其他课程
- **教室冲突**：同一教室在同一时间段已被占用

如果检测到冲突，该日期会被跳过，并记录在 `skippedDates` 中。

### 3. 课节序号

- 课节序号（`lessonNo`）从1开始自动递增
- 跳过的日期不计入课节序号

### 4. 日期范围与总课次

- `endDate` 和 `totalLessons` 二选一，必须至少指定一个
- 如果同时指定，以 `totalLessons` 为准，但不会超过 `endDate`
- 如果只指定 `totalLessons`，系统会从 `startDate` 开始，按规则生成指定数量的课程

### 5. 批量插入优化

- 系统使用 MyBatis-Plus 的 `saveBatch` 方法进行批量插入
- 单次批量插入最多1000条记录
- 所有排课记录在同一事务中执行，保证数据一致性

## 错误处理

### 常见错误

1. **班级不存在**
   ```json
   {
     "code": 500,
     "msg": "班级不存在"
   }
   ```

2. **参数验证失败**
   ```json
   {
     "code": 500,
     "msg": "结束日期和总课次必须至少指定一个"
   }
   ```

3. **时间范围错误**
   ```json
   {
     "code": 500,
     "msg": "结束日期不能早于开始日期"
   }
   ```

4. **星期参数错误**
   ```json
   {
     "code": 500,
     "msg": "星期几必须在1-7之间"
   }
   ```

## 性能优化

### 1. 批量插入

使用 MyBatis-Plus 的 `saveBatch` 方法，减少数据库交互次数：

```java
saveBatch(schedulesToSave);
```

### 2. 事务管理

使用 `@Transactional` 注解，确保批量操作的原子性：

```java
@Transactional(rollbackFor = Exception.class)
public BatchScheduleResultVO batchCreateScheduleEnhanced(BatchScheduleDTO dto)
```

### 3. 防止无限循环

在按总课次生成时，设置最大迭代次数：

```java
int maxIterations = dto.getTotalLessons() * 10;
```

## 数据库表结构

### tch_schedule 表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| class_id | BIGINT | 班级ID |
| course_id | BIGINT | 课程ID |
| teacher_id | BIGINT | 教师ID |
| classroom_id | BIGINT | 教室ID |
| campus_id | BIGINT | 校区ID |
| schedule_date | DATE | 上课日期 |
| start_time | TIME | 开始时间 |
| end_time | TIME | 结束时间 |
| class_hours | INT | 课时数 |
| status | VARCHAR | 状态：scheduled-已排课，ongoing-进行中，finished-已完成，cancelled-已取消 |
| lesson_no | INT | 课节序号 |
| topic | VARCHAR | 课节主题 |
| remark | VARCHAR | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | BIGINT | 创建人ID |
| update_by | BIGINT | 更新人ID |
| deleted | TINYINT | 删除标志 |

## 测试用例

### 测试用例1：基本批量排课

```bash
curl -X POST http://localhost:8080/teaching/schedule/batch-enhanced \
  -H "Content-Type: application/json" \
  -d '{
    "classId": 1001,
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "weekdays": [1, 3, 5],
    "startTime": "09:00",
    "endTime": "11:00",
    "skipHolidays": true
  }'
```

### 测试用例2：按总课次排课

```bash
curl -X POST http://localhost:8080/teaching/schedule/batch-enhanced \
  -H "Content-Type: application/json" \
  -d '{
    "classId": 1001,
    "startDate": "2024-01-01",
    "totalLessons": 20,
    "weekdays": [1, 3, 5],
    "startTime": "09:00",
    "endTime": "11:00",
    "skipHolidays": true,
    "topicPrefix": "第{n}课"
  }'
```

### 测试用例3：周末班排课

```bash
curl -X POST http://localhost:8080/teaching/schedule/batch-enhanced \
  -H "Content-Type: application/json" \
  -d '{
    "classId": 1001,
    "startDate": "2024-01-01",
    "endDate": "2024-06-30",
    "weekdays": [6, 7],
    "startTime": "14:00",
    "endTime": "16:00",
    "skipHolidays": true,
    "skipWeekends": false
  }'
```

## 注意事项

1. **节假日数据**：确保 `sys_holiday` 表中已配置节假日数据
2. **班级信息**：确保班级已关联课程、教师、教室等信息
3. **时间冲突**：建议在排课前检查教师和教室的可用性
4. **数据量**：单次批量排课建议不超过1000条记录
5. **事务回滚**：如果批量排课过程中出现异常，所有排课记录都会回滚

## 相关接口

- **查询课表**: `GET /teaching/schedule/list`
- **创建单个排课**: `POST /teaching/schedule`
- **批量排课（简单版本）**: `POST /teaching/schedule/batch`
- **调课**: `PUT /teaching/schedule/{id}/reschedule`
- **代课**: `PUT /teaching/schedule/{id}/substitute`
- **取消课程**: `PUT /teaching/schedule/{id}/cancel`
