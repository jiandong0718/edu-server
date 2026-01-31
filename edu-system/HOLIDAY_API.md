# 节假日管理接口文档

## 概述

节假日管理模块提供了完整的节假日 CRUD 接口，支持多校区数据隔离，可用于排课和考勤系统。

## 数据模型

### SysHoliday 实体

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键ID |
| name | String | 节假日名称 |
| type | Integer | 类型：1-法定节假日，2-调休，3-公司假期 |
| startDate | LocalDate | 开始日期 |
| endDate | LocalDate | 结束日期 |
| description | String | 描述 |
| campusId | Long | 校区ID（NULL表示全局） |
| isWorkday | Integer | 是否工作日：0-否（休息），1-是（调休上班） |
| status | Integer | 状态：0-禁用，1-启用 |
| remark | String | 备注 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| createBy | Long | 创建人ID |
| updateBy | Long | 更新人ID |
| deleted | Integer | 删除标记：0-未删除，1-已删除 |

## API 接口

### 1. 分页查询节假日列表

**接口地址**: `GET /system/holiday/page`

**请求参数**:
- pageNum: 页码（默认1）
- pageSize: 每页数量（默认10）
- name: 节假日名称（模糊查询）
- type: 节假日类型
- campusId: 校区ID
- status: 状态
- startDate: 查询开始日期（yyyy-MM-dd）
- endDate: 查询结束日期（yyyy-MM-dd）

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "元旦",
        "type": 1,
        "typeName": "法定节假日",
        "startDate": "2026-01-01",
        "endDate": "2026-01-01",
        "description": "2026年元旦假期",
        "campusId": null,
        "campusName": "全局",
        "isWorkday": 0,
        "status": 1,
        "remark": null,
        "createTime": "2026-01-31T00:00:00",
        "updateTime": "2026-01-31T00:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 获取节假日列表

**接口地址**: `GET /system/holiday/list`

**请求参数**:
- type: 节假日类型（可选）
- campusId: 校区ID（可选）
- startDate: 开始日期（可选）
- endDate: 结束日期（可选）

**说明**: 返回所有启用状态的节假日列表，支持按类型、校区、日期范围过滤。

### 3. 获取节假日详情

**接口地址**: `GET /system/holiday/{id}`

**路径参数**:
- id: 节假日ID

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "name": "元旦",
    "type": 1,
    "typeName": "法定节假日",
    "startDate": "2026-01-01",
    "endDate": "2026-01-01",
    "description": "2026年元旦假期",
    "campusId": null,
    "campusName": "全局",
    "isWorkday": 0,
    "status": 1,
    "remark": null
  }
}
```

### 4. 新增节假日

**接口地址**: `POST /system/holiday`

**请求体**:
```json
{
  "name": "春节",
  "type": 1,
  "startDate": "2026-02-17",
  "endDate": "2026-02-23",
  "description": "2026年春节假期",
  "campusId": null,
  "isWorkday": 0,
  "status": 1,
  "remark": ""
}
```

**验证规则**:
- name: 必填
- type: 必填
- startDate: 必填，不能晚于结束日期
- endDate: 必填
- 日期范围不能与已有节假日冲突

### 5. 修改节假日

**接口地址**: `PUT /system/holiday`

**请求体**:
```json
{
  "id": 1,
  "name": "元旦",
  "type": 1,
  "startDate": "2026-01-01",
  "endDate": "2026-01-01",
  "description": "2026年元旦假期（已修改）",
  "campusId": null,
  "isWorkday": 0,
  "status": 1,
  "remark": ""
}
```

### 6. 删除节假日

**接口地址**: `DELETE /system/holiday/{id}`

**路径参数**:
- id: 节假日ID

### 7. 批量删除节假日

**接口地址**: `DELETE /system/holiday/batch`

**请求体**:
```json
[1, 2, 3]
```

### 8. 修改节假日状态

**接口地址**: `PUT /system/holiday/{id}/status`

**路径参数**:
- id: 节假日ID

**请求参数**:
- status: 状态（0-禁用，1-启用）

### 9. 判断指定日期是否为节假日

**接口地址**: `GET /system/holiday/check/holiday`

**请求参数**:
- date: 日期（yyyy-MM-dd）
- campusId: 校区ID（可选）

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

**说明**: 返回 true 表示该日期是节假日（休息日），false 表示不是。

### 10. 判断指定日期是否为工作日（调休上班）

**接口地址**: `GET /system/holiday/check/workday`

**请求参数**:
- date: 日期（yyyy-MM-dd）
- campusId: 校区ID（可选）

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": false
}
```

**说明**: 返回 true 表示该日期是调休工作日，false 表示不是。

### 11. 获取指定日期范围内的节假日

**接口地址**: `GET /system/holiday/range`

**请求参数**:
- startDate: 开始日期（yyyy-MM-dd）
- endDate: 结束日期（yyyy-MM-dd）
- campusId: 校区ID（可选）

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "元旦",
      "type": 1,
      "startDate": "2026-01-01",
      "endDate": "2026-01-01",
      "description": "2026年元旦假期",
      "campusId": null,
      "isWorkday": 0,
      "status": 1
    }
  ]
}
```

## 多校区数据隔离

节假日支持两种范围：

1. **全局节假日** (campusId = NULL)
   - 适用于所有校区
   - 例如：法定节假日

2. **校区节假日** (campusId = 具体校区ID)
   - 仅适用于指定校区
   - 例如：某个校区的特殊假期

查询时，系统会自动返回全局节假日和指定校区的节假日。

## 业务规则

1. **日期验证**: 开始日期不能晚于结束日期
2. **冲突检测**: 同一校区（或全局）的节假日日期范围不能重叠
3. **校区验证**: 如果指定了校区ID，必须确保校区存在
4. **状态控制**: 只有启用状态的节假日才会在查询和判断接口中生效
5. **逻辑删除**: 删除操作使用逻辑删除，不会物理删除数据

## 使用场景

### 1. 排课系统
```java
// 检查某个日期是否可以排课
LocalDate date = LocalDate.of(2026, 1, 1);
boolean isHoliday = holidayService.isHoliday(date, campusId);
if (isHoliday) {
    // 节假日，不能排课
}
```

### 2. 考勤系统
```java
// 判断某天是否需要考勤
LocalDate date = LocalDate.now();
boolean isHoliday = holidayService.isHoliday(date, campusId);
boolean isWorkday = holidayService.isWorkday(date, campusId);

if (isWorkday) {
    // 调休工作日，需要考勤
} else if (isHoliday) {
    // 节假日，不需要考勤
} else {
    // 正常工作日，需要考勤
}
```

### 3. 获取某个月的节假日
```java
// 获取2026年2月的所有节假日
LocalDate startDate = LocalDate.of(2026, 2, 1);
LocalDate endDate = LocalDate.of(2026, 2, 28);
List<SysHoliday> holidays = holidayService.getHolidaysByDateRange(startDate, endDate, campusId);
```

## 数据库迁移

数据库迁移脚本位于：
`edu-admin/src/main/resources/db/migration/V1.0.8__add_holiday_table.sql`

脚本包含：
1. sys_holiday 表结构创建
2. 2026年法定节假日示例数据

## 技术实现

### 技术栈
- Spring Boot 3.2.x
- MyBatis-Plus 3.5.x
- Hutool（工具类）
- Swagger/Knife4j（API文档）

### 核心类
- **Entity**: `com.edu.system.domain.entity.SysHoliday`
- **DTO**: `com.edu.system.domain.dto.SysHolidayDTO`
- **QueryDTO**: `com.edu.system.domain.dto.SysHolidayQueryDTO`
- **VO**: `com.edu.system.domain.vo.SysHolidayVO`
- **Mapper**: `com.edu.system.mapper.SysHolidayMapper`
- **Service**: `com.edu.system.service.SysHolidayService`
- **ServiceImpl**: `com.edu.system.service.impl.SysHolidayServiceImpl`
- **Controller**: `com.edu.system.controller.SysHolidayController`

### 特性
- 支持分页查询
- 支持多条件组合查询
- 支持多校区数据隔离
- 日期冲突检测
- 事务管理
- 统一异常处理
- API 文档自动生成
