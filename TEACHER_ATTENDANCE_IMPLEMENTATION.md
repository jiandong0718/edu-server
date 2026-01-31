# 教师考勤与考勤统计接口实现文档

## 概述

本文档描述了教师考勤管理和考勤统计功能的实现，包括教师签到/签退、请假记录、以及学员和教师的综合考勤统计分析。

## 实现内容

### 任务 16.7 - 教师考勤接口

#### 1. 数据库表结构

**表名**: `tch_teacher_attendance`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| schedule_id | BIGINT | 排课ID |
| teacher_id | BIGINT | 教师ID |
| class_id | BIGINT | 班级ID |
| sign_in_time | DATETIME | 签到时间 |
| sign_out_time | DATETIME | 签退时间 |
| status | VARCHAR(20) | 状态：present-出勤，absent-缺勤，late-迟到，early_leave-早退，leave-请假 |
| is_late | TINYINT | 是否迟到 |
| is_early_leave | TINYINT | 是否早退 |
| late_minutes | INT | 迟到分钟数 |
| early_leave_minutes | INT | 早退分钟数 |
| remark | VARCHAR(500) | 备注 |

**索引**:
- 唯一索引: `uk_schedule_teacher (schedule_id, teacher_id)`
- 普通索引: `idx_teacher_id`, `idx_class_id`, `idx_status`, `idx_sign_in_time`

#### 2. 核心功能

##### 2.1 教师签到
- **接口**: `POST /teaching/teacher-attendance/sign-in`
- **功能**:
  - 记录教师签到时间
  - 自动判断是否迟到（容忍时间：15分钟）
  - 计算迟到分钟数
  - 防止重复签到
- **状态判断逻辑**:
  - 在课程开始时间前或准时：`present`
  - 超过开始时间15分钟内：`late`
  - 超过15分钟：`late`（可根据业务需求调整为`absent`）

##### 2.2 教师签退
- **接口**: `POST /teaching/teacher-attendance/sign-out`
- **功能**:
  - 记录教师签退时间
  - 自动判断是否早退（容忍时间：15分钟）
  - 计算早退分钟数
  - 综合判断最终状态（迟到+早退=早退，仅迟到=迟到，正常=出勤）
- **状态判断逻辑**:
  - 在课程结束时间后或准时：保持原状态
  - 提前15分钟以上：`early_leave`

##### 2.3 教师请假
- **接口**: `POST /teaching/teacher-attendance/leave`
- **功能**:
  - 记录教师请假信息
  - 标记状态为`leave`
  - 防止已签到后标记请假

#### 3. 实体类和DTO

**实体类**: `TeacherAttendance.java`
- 包含考勤基本信息
- 关联教师、排课、班级信息
- 支持迟到、早退状态和时长记录

**DTO类**:
- `TeacherSignInDTO`: 教师签到请求
- `TeacherSignOutDTO`: 教师签退请求
- `TeacherLeaveDTO`: 教师请假请求

#### 4. 业务规则

1. **迟到判定**:
   - 容忍时间：15分钟
   - 超过容忍时间标记为迟到
   - 记录实际迟到分钟数

2. **早退判定**:
   - 容忍时间：15分钟
   - 提前15分钟以上离开标记为早退
   - 记录实际早退分钟数

3. **状态优先级**:
   - 早退 > 迟到 > 出勤
   - 既迟到又早退，最终状态为`early_leave`

4. **防重复操作**:
   - 已签到不能重复签到
   - 已签退不能重复签退
   - 已签到不能标记请假

### 任务 16.8 - 考勤统计接口

#### 1. 学员考勤统计

##### 1.1 按班级统计
- **接口**: `GET /teaching/attendance-stats/student/by-class`
- **参数**: classId, startDate, endDate
- **返回数据**:
  ```json
  {
    "total": 100,           // 总考勤次数
    "present": 85,          // 出勤次数
    "absent": 5,            // 缺勤次数
    "late": 8,              // 迟到次数
    "leave": 2,             // 请假次数
    "attendanceRate": 93.0  // 出勤率(%)
  }
  ```

##### 1.2 按学员统计
- **接口**: `GET /teaching/attendance-stats/student/by-student`
- **参数**: studentId, classId, startDate, endDate
- **返回数据**: 同上

##### 1.3 按时间段统计
- **接口**: `GET /teaching/attendance-stats/student/by-period`
- **参数**: AttendanceStatsQueryDTO（支持多维度筛选）

#### 2. 教师考勤统计

##### 2.1 按教师统计
- **接口**: `GET /teaching/attendance-stats/teacher/by-teacher`
- **参数**: teacherId, classId, startDate, endDate
- **返回数据**:
  ```json
  {
    "total": 50,                    // 总考勤次数
    "present": 45,                  // 出勤次数
    "absent": 1,                    // 缺勤次数
    "late": 3,                      // 迟到次数
    "earlyLeave": 1,                // 早退次数
    "leave": 0,                     // 请假次数
    "attendanceRate": 96.0,         // 出勤率(%)
    "totalLateMinutes": 45,         // 总迟到分钟数
    "totalEarlyLeaveMinutes": 20,   // 总早退分钟数
    "avgLateMinutes": 15.0,         // 平均迟到分钟数
    "avgEarlyLeaveMinutes": 20.0    // 平均早退分钟数
  }
  ```

##### 2.2 按班级统计
- **接口**: `GET /teaching/attendance-stats/teacher/by-class`
- **参数**: classId, startDate, endDate
- **返回数据**: 同上

##### 2.3 按时间段统计
- **接口**: `GET /teaching/attendance-stats/teacher/by-period`
- **参数**: TeacherAttendanceStatsQueryDTO（支持多维度筛选）

#### 3. 综合统计功能

##### 3.1 综合考勤统计
- **接口**: `GET /teaching/attendance-stats/comprehensive`
- **功能**: 同时获取学员和教师的考勤统计
- **返回数据**:
  ```json
  {
    "studentStats": { /* 学员统计数据 */ },
    "teacherStats": { /* 教师统计数据 */ }
  }
  ```

##### 3.2 出勤率对比
- **接口**: `GET /teaching/attendance-stats/attendance-rate-comparison`
- **功能**: 对比学员和教师的出勤率
- **支持维度**: 按学员、按教师、按班级

##### 3.3 考勤异常统计
- **接口**: `GET /teaching/attendance-stats/abnormal-stats`
- **功能**: 统计迟到、早退、缺勤等异常情况
- **返回数据**:
  ```json
  {
    "studentAbnormal": {
      "late": 8,
      "absent": 5,
      "leave": 2
    },
    "teacherAbnormal": {
      "late": 3,
      "earlyLeave": 1,
      "absent": 1,
      "leave": 0,
      "totalLateMinutes": 45,
      "totalEarlyLeaveMinutes": 20
    }
  }
  ```

#### 4. 出勤率计算

**计算公式**:
```
出勤率 = (出勤次数 + 迟到次数) / 总考勤次数 × 100%
```

**说明**:
- 出勤和迟到都计入出勤率
- 缺勤和请假不计入出勤率
- 早退根据业务需求可计入或不计入（当前实现不计入）

## 文件清单

### 数据库迁移
- `/edu-admin/src/main/resources/db/migration/V1.0.18__add_teacher_attendance_table.sql`

### 实体类
- `/edu-teaching/src/main/java/com/edu/teaching/domain/entity/TeacherAttendance.java`

### DTO类
- `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/TeacherSignInDTO.java`
- `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/TeacherSignOutDTO.java`
- `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/TeacherLeaveDTO.java`
- `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/TeacherAttendanceStatsQueryDTO.java`
- `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/AttendanceStatsQueryDTO.java`

### Mapper
- `/edu-teaching/src/main/java/com/edu/teaching/mapper/TeacherAttendanceMapper.java`
- `/edu-teaching/src/main/resources/mapper/teaching/TeacherAttendanceMapper.xml`

### Service
- `/edu-teaching/src/main/java/com/edu/teaching/service/TeacherAttendanceService.java`
- `/edu-teaching/src/main/java/com/edu/teaching/service/impl/TeacherAttendanceServiceImpl.java`

### Controller
- `/edu-teaching/src/main/java/com/edu/teaching/controller/TeacherAttendanceController.java`
- `/edu-teaching/src/main/java/com/edu/teaching/controller/AttendanceStatsController.java`

## API接口列表

### 教师考勤接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 分页查询 | GET | /teaching/teacher-attendance/page | 分页查询教师考勤列表 |
| 获取排课考勤 | GET | /teaching/teacher-attendance/schedule/{scheduleId} | 获取指定排课的教师考勤 |
| 教师签到 | POST | /teaching/teacher-attendance/sign-in | 教师签到 |
| 教师签退 | POST | /teaching/teacher-attendance/sign-out | 教师签退 |
| 教师请假 | POST | /teaching/teacher-attendance/leave | 教师请假 |
| 更新状态 | PUT | /teaching/teacher-attendance/{id}/status | 更新考勤状态 |
| 查询考勤记录 | GET | /teaching/teacher-attendance/teacher/{teacherId} | 查询教师考勤记录 |
| 教师出勤统计 | GET | /teaching/teacher-attendance/stats/teacher/{teacherId} | 统计教师出勤情况 |
| 班级教师统计 | GET | /teaching/teacher-attendance/stats/class/{classId} | 统计班级教师出勤 |
| 计算出勤率 | GET | /teaching/teacher-attendance/attendance-rate/{teacherId} | 计算教师出勤率 |

### 考勤统计接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 学员按班级统计 | GET | /teaching/attendance-stats/student/by-class | 按班级统计学员考勤 |
| 学员按学员统计 | GET | /teaching/attendance-stats/student/by-student | 按学员统计考勤 |
| 学员按时间段统计 | GET | /teaching/attendance-stats/student/by-period | 按时间段统计学员考勤 |
| 教师按教师统计 | GET | /teaching/attendance-stats/teacher/by-teacher | 按教师统计考勤 |
| 教师按班级统计 | GET | /teaching/attendance-stats/teacher/by-class | 按班级统计教师考勤 |
| 教师按时间段统计 | GET | /teaching/attendance-stats/teacher/by-period | 按时间段统计教师考勤 |
| 综合统计 | GET | /teaching/attendance-stats/comprehensive | 综合考勤统计 |
| 出勤率对比 | GET | /teaching/attendance-stats/attendance-rate-comparison | 出勤率对比 |
| 异常统计 | GET | /teaching/attendance-stats/abnormal-stats | 考勤异常统计 |

## 使用示例

### 1. 教师签到
```bash
curl -X POST http://localhost:8080/teaching/teacher-attendance/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "scheduleId": 1,
    "teacherId": 10,
    "remark": "准时签到"
  }'
```

### 2. 教师签退
```bash
curl -X POST http://localhost:8080/teaching/teacher-attendance/sign-out \
  -H "Content-Type: application/json" \
  -d '{
    "scheduleId": 1,
    "teacherId": 10,
    "remark": "课程结束"
  }'
```

### 3. 查询教师考勤统计
```bash
curl -X GET "http://localhost:8080/teaching/attendance-stats/teacher/by-teacher?teacherId=10&startDate=2024-01-01&endDate=2024-01-31"
```

### 4. 综合考勤统计
```bash
curl -X GET "http://localhost:8080/teaching/attendance-stats/comprehensive?classId=5&startDate=2024-01-01&endDate=2024-01-31"
```

## 技术特点

1. **自动化判断**: 自动判断迟到、早退状态，无需手动输入
2. **精确计算**: 精确计算迟到和早退的分钟数
3. **防重复操作**: 防止重复签到、签退
4. **多维度统计**: 支持按学员、教师、班级、时间段等多维度统计
5. **综合分析**: 提供学员和教师的综合考勤对比分析
6. **异常监控**: 专门的异常考勤统计，便于管理监控

## 注意事项

1. 迟到和早退的容忍时间可通过常量配置调整
2. 考勤状态的优先级逻辑可根据业务需求调整
3. 出勤率计算公式可根据业务需求调整
4. 建议定期清理历史考勤数据，避免数据量过大影响查询性能
5. 统计接口建议添加缓存，提升查询性能

## 后续优化建议

1. **缓存优化**: 对统计接口添加Redis缓存
2. **异步处理**: 考勤事件可异步处理，提升响应速度
3. **报表导出**: 添加考勤报表导出功能（Excel/PDF）
4. **消息通知**: 异常考勤自动发送通知给管理员
5. **数据分析**: 添加考勤趋势分析、预警功能
6. **移动端支持**: 支持移动端扫码签到/签退
