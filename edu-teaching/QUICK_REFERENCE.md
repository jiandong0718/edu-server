# 教师可用时间配置 - 快速参考

## 快速开始

### 1. 设置教师可用时间（推荐方式）

```bash
POST /teaching/teacher/available-time/batch-save
Content-Type: application/json

{
  "teacherId": 100,
  "timeSlots": [
    {"dayOfWeek": 1, "startTime": "09:00", "endTime": "12:00"},
    {"dayOfWeek": 1, "startTime": "14:00", "endTime": "17:00"}
  ]
}
```

### 2. 查询教师可用时间

```bash
GET /teaching/teacher/available-time/list?teacherId=100
```

### 3. 检查教师是否可用（排课时使用）

```bash
GET /teaching/teacher/available-time/check-available?teacherId=100&dayOfWeek=1&startTime=10:00&endTime=11:30
```

## 核心API

| 功能 | 方法 | 路径 |
|------|------|------|
| 查询列表 | GET | `/list?teacherId={id}` |
| 批量保存 | POST | `/batch-save` |
| 新增 | POST | `/` |
| 修改 | PUT | `/` |
| 删除 | DELETE | `/{id}` |
| 检查可用 | GET | `/check-available` |
| 启用 | PUT | `/{id}/enable` |
| 禁用 | PUT | `/{id}/disable` |

## 星期几编码

```
1 = 周一
2 = 周二
3 = 周三
4 = 周四
5 = 周五
6 = 周六
7 = 周日
```

## 时间格式

- 格式：`HH:mm`（24小时制）
- 示例：`09:00`, `14:30`, `23:59`

## 验证规则

1. ✅ 时间格式必须为 HH:mm
2. ✅ 开始时间必须小于结束时间
3. ✅ 同一天的时间段不能重叠
4. ✅ 星期几必须在 1-7 之间

## 时间冲突示例

```
✅ 09:00-12:00 和 12:00-15:00 → 不冲突（边界不算）
❌ 09:00-12:00 和 10:00-13:00 → 冲突（有重叠）
❌ 09:00-12:00 和 11:00-14:00 → 冲突（有重叠）
```

## 教师可用性检查

```
教师可用时间：周一 09:00-17:00

✅ 查询 周一 10:00-12:00 → 可用（完全包含）
✅ 查询 周一 09:00-17:00 → 可用（完全相同）
❌ 查询 周一 08:00-10:00 → 不可用（开始时间超出）
❌ 查询 周一 16:00-18:00 → 不可用（结束时间超出）
```

## 常见错误

| 错误信息 | 原因 | 解决方法 |
|----------|------|----------|
| 星期几必须在1-7之间 | dayOfWeek不在有效范围 | 使用1-7的值 |
| 时间格式错误 | 时间格式不是HH:mm | 使用正确格式如09:00 |
| 开始时间必须小于结束时间 | 时间段无效 | 确保start < end |
| 时间段冲突 | 与已有时间段重叠 | 调整时间段或删除冲突的时间段 |

## 代码示例

### Java调用示例

```java
// 注入Service
@Autowired
private TeacherAvailableTimeService availableTimeService;

// 检查教师是否可用
boolean available = availableTimeService.isTeacherAvailable(
    teacherId,    // 教师ID
    1,            // 周一
    "10:00",      // 开始时间
    "11:30"       // 结束时间
);

if (available) {
    // 可以安排课程
} else {
    // 教师不可用
}
```

### 批量保存示例

```java
BatchSaveAvailableTimeDTO dto = new BatchSaveAvailableTimeDTO();
dto.setTeacherId(100L);

List<TimeSlotDTO> slots = new ArrayList<>();
slots.add(new TimeSlotDTO(1, "09:00", "12:00", "周一上午"));
slots.add(new TimeSlotDTO(1, "14:00", "17:00", "周一下午"));
dto.setTimeSlots(slots);

boolean success = availableTimeService.batchSaveWithValidation(dto);
```

## 文件位置

```
edu-teaching/
├── domain/
│   ├── entity/
│   │   └── TeacherAvailableTime.java          # 实体类
│   ├── dto/
│   │   ├── TeacherAvailableTimeDTO.java       # 单个时间段DTO
│   │   └── BatchSaveAvailableTimeDTO.java     # 批量保存DTO
│   └── vo/
│       └── TeacherAvailableTimeVO.java        # 视图对象
├── mapper/
│   └── TeacherAvailableTimeMapper.java        # Mapper接口
├── service/
│   ├── TeacherAvailableTimeService.java       # Service接口
│   └── impl/
│       └── TeacherAvailableTimeServiceImpl.java # Service实现
└── controller/
    └── TeacherAvailableTimeController.java    # Controller
```

## 数据库表

```sql
CREATE TABLE tch_teacher_available_time (
    id BIGINT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status TINYINT DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME,
    update_time DATETIME,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0
);
```

## 完整文档

详细文档请参考：
- API文档：`TEACHER_AVAILABLE_TIME_API.md`
- 实现总结：`IMPLEMENTATION_SUMMARY.md`
