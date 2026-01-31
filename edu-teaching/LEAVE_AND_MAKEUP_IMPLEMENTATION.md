# 请假和补课功能实现文档

## 概述

本文档描述了教育机构学生管理系统中请假申请、审批和补课安排功能的后端实现（任务 16.4、16.5、16.6）。

## 功能特性

### 1. 请假申请（任务 16.4）
- 学员提交请假申请
- 支持两种请假类型：
  - **单次请假**：针对特定课节的请假
  - **时段请假**：针对一段时间范围的请假
- 自动生成请假单号（格式：LV + 日期 + 序号）
- 支持填写请假原因和备注
- 可选择是否需要补课

### 2. 请假审批（任务 16.5）
- 教师/管理员审批请假申请
- 支持批准或拒绝操作
- 可填写审批意见
- 审批通过后自动更新考勤记录为请假状态
- 发布事件用于通知学员

### 3. 补课安排（任务 16.6）
- 为请假学员安排补课
- 选择补课时间和排课
- 自动检查补课时间冲突
- 支持补课完成和取消操作
- 发布事件用于通知学员和教师

## 数据库表结构

### tch_leave_request（请假申请表）
```sql
CREATE TABLE tch_leave_request (
    id BIGINT PRIMARY KEY,
    leave_no VARCHAR(50) UNIQUE NOT NULL COMMENT '请假单号',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    schedule_id BIGINT COMMENT '排课ID（单次请假）',
    class_id BIGINT COMMENT '班级ID',
    campus_id BIGINT COMMENT '校区ID',
    type VARCHAR(20) DEFAULT 'single' COMMENT '请假类型：single-单次，period-时段',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    reason VARCHAR(500) COMMENT '请假原因',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审批，approved-已批准，rejected-已拒绝，cancelled-已取消',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    need_makeup TINYINT DEFAULT 0 COMMENT '是否需要补课',
    makeup_schedule_id BIGINT COMMENT '补课排课ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0
);
```

### tch_makeup_lesson（补课记录表）
```sql
CREATE TABLE tch_makeup_lesson (
    id BIGINT PRIMARY KEY,
    leave_request_id BIGINT COMMENT '请假申请ID',
    original_schedule_id BIGINT COMMENT '原排课ID',
    makeup_schedule_id BIGINT COMMENT '补课排课ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    campus_id BIGINT COMMENT '校区ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待补课，completed-已完成，cancelled-已取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0
);
```

## 代码结构

### 实体类（Entity）
- `LeaveRequest.java` - 请假申请实体（已存在，已完善）
- `MakeupLesson.java` - 补课记录实体（新增）

### 数据传输对象（DTO）
- `LeaveRequestDTO.java` - 请假申请提交DTO（新增）
- `LeaveApprovalDTO.java` - 请假审批DTO（新增）
- `MakeupLessonDTO.java` - 补课安排DTO（新增）

### 视图对象（VO）
- `LeaveRequestVO.java` - 请假申请视图对象（新增）
- `MakeupLessonVO.java` - 补课记录视图对象（新增）

### Mapper接口
- `LeaveRequestMapper.java` - 请假申请Mapper（已存在）
- `LeaveRequestMapper.xml` - 请假申请Mapper XML（已存在）
- `MakeupLessonMapper.java` - 补课记录Mapper（新增）
- `MakeupLessonMapper.xml` - 补课记录Mapper XML（新增）

### 服务层（Service）
- `LeaveRequestService.java` - 请假申请服务接口（已存在）
- `LeaveRequestServiceImpl.java` - 请假申请服务实现（已完善）
- `MakeupLessonService.java` - 补课记录服务接口（新增）
- `MakeupLessonServiceImpl.java` - 补课记录服务实现（新增）

### 控制器（Controller）
- `LeaveRequestController.java` - 请假管理控制器（已完善）
- `MakeupLessonController.java` - 补课管理控制器（新增）

### 事件（Event）
- `LeaveApprovedEvent.java` - 请假审批事件（新增）
- `MakeupArrangedEvent.java` - 补课安排事件（新增）
- `LeaveEventListener.java` - 事件监听器（新增）

### 通用类
- `Result.java` - 统一响应结果类（新增，继承自R类）

## API接口说明

### 请假管理接口

#### 1. 分页查询请假申请列表
```
GET /teaching/leave/page
参数：
  - pageNum: 页码（默认1）
  - pageSize: 每页数量（默认10）
  - studentId: 学员ID（可选）
  - classId: 班级ID（可选）
  - campusId: 校区ID（可选）
  - status: 状态（可选）
  - type: 请假类型（可选）
```

#### 2. 获取请假申请详情
```
GET /teaching/leave/{id}
参数：
  - id: 请假申请ID
```

#### 3. 提交请假申请
```
POST /teaching/leave
请求体：LeaveRequestDTO
{
  "studentId": 学员ID,
  "scheduleId": 排课ID（单次请假时必填）,
  "classId": 班级ID,
  "campusId": 校区ID,
  "type": "single" 或 "period",
  "startDate": "开始日期（时段请假时必填）",
  "endDate": "结束日期（时段请假时必填）",
  "reason": "请假原因",
  "needMakeup": 0 或 1,
  "remark": "备注"
}
```

#### 4. 审批请假申请
```
PUT /teaching/leave/{id}/approve
参数：
  - id: 请假申请ID
  - approved: true（批准）或 false（拒绝）
  - remark: 审批意见（可选）
```

#### 5. 取消请假申请
```
PUT /teaching/leave/{id}/cancel
参数：
  - id: 请假申请ID
```

#### 6. 安排补课
```
PUT /teaching/leave/{id}/makeup
参数：
  - id: 请假申请ID
  - makeupScheduleId: 补课排课ID
```

### 补课管理接口

#### 1. 分页查询补课记录列表
```
GET /teaching/makeup/page
参数：
  - pageNum: 页码（默认1）
  - pageSize: 每页数量（默认10）
  - studentId: 学员ID（可选）
  - campusId: 校区ID（可选）
  - status: 状态（可选）
  - leaveRequestId: 请假申请ID（可选）
```

#### 2. 获取补课记录详情
```
GET /teaching/makeup/{id}
参数：
  - id: 补课记录ID
```

#### 3. 安排补课
```
POST /teaching/makeup
请求体：MakeupLessonDTO
{
  "leaveRequestId": 请假申请ID,
  "originalScheduleId": 原排课ID,
  "makeupScheduleId": 补课排课ID,
  "studentId": 学员ID,
  "campusId": 校区ID,
  "remark": "备注"
}
```

#### 4. 完成补课
```
PUT /teaching/makeup/{id}/complete
参数：
  - id: 补课记录ID
```

#### 5. 取消补课
```
PUT /teaching/makeup/{id}/cancel
参数：
  - id: 补课记录ID
```

#### 6. 检查补课时间冲突
```
GET /teaching/makeup/check-conflict
参数：
  - studentId: 学员ID
  - makeupScheduleId: 补课排课ID
返回：true（有冲突）或 false（无冲突）
```

## 业务流程

### 请假申请流程
1. 学员提交请假申请
2. 系统生成请假单号
3. 如果是单次请假，自动关联排课信息
4. 请假申请进入待审批状态

### 请假审批流程
1. 教师/管理员查看待审批的请假申请
2. 选择批准或拒绝，填写审批意见
3. 如果批准：
   - 更新请假状态为已批准
   - 自动更新对应的考勤记录为请假状态
   - 发布请假审批事件，触发通知
4. 如果拒绝：
   - 更新请假状态为已拒绝
   - 发布请假审批事件，通知学员

### 补课安排流程
1. 对于已批准的请假申请，可以安排补课
2. 选择补课的排课时间
3. 系统检查补课时间是否与学员其他课程冲突
4. 创建补课记录
5. 发布补课安排事件，触发通知

## 技术要点

### 1. 事务管理
所有涉及数据修改的操作都使用 `@Transactional` 注解确保数据一致性。

### 2. 事件驱动
使用Spring事件机制实现模块解耦：
- 请假审批后发布 `LeaveApprovedEvent`
- 补课安排后发布 `MakeupArrangedEvent`
- 事件监听器异步处理通知发送

### 3. 业务校验
- 请假申请提交时验证日期范围
- 审批时检查请假状态
- 补课安排时检查时间冲突
- 防止重复安排补课

### 4. 自动编号
请假单号自动生成规则：`LV + yyyyMMdd + 4位序号`
例如：LV202601310001

### 5. 关联查询
使用MyBatis XML配置实现多表关联查询，获取学员、班级、教师等关联信息。

## 扩展功能建议

1. **批量请假审批**：支持一次审批多个请假申请
2. **请假统计**：统计学员请假次数和时长
3. **补课提醒**：补课前自动发送提醒通知
4. **请假规则**：配置请假规则（如提前多久申请、最多请假次数等）
5. **补课课时扣除**：补课时自动扣除课时账户
6. **移动端支持**：提供移动端请假申请和审批功能

## 测试建议

### 单元测试
- 测试请假单号生成逻辑
- 测试日期范围验证
- 测试补课时间冲突检测

### 集成测试
- 测试完整的请假申请流程
- 测试请假审批流程
- 测试补课安排流程
- 测试事件发布和监听

### 接口测试
使用Knife4j（Swagger UI）进行接口测试：
访问 http://localhost:8080/doc.html

## 注意事项

1. **权限控制**：需要配置相应的权限，确保只有授权用户可以审批请假
2. **数据权限**：需要实现数据权限过滤，确保用户只能查看自己校区的数据
3. **审批人设置**：当前代码中审批人ID设置为TODO，需要集成用户认证系统
4. **通知服务**：事件监听器中的通知发送为TODO，需要集成通知模块
5. **时段请假**：时段请假的考勤更新逻辑需要进一步完善

## 文件清单

### 新增文件
1. `/edu-common/src/main/java/com/edu/common/core/Result.java`
2. `/edu-teaching/src/main/java/com/edu/teaching/domain/entity/MakeupLesson.java`
3. `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/LeaveRequestDTO.java`
4. `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/LeaveApprovalDTO.java`
5. `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/MakeupLessonDTO.java`
6. `/edu-teaching/src/main/java/com/edu/teaching/domain/vo/LeaveRequestVO.java`
7. `/edu-teaching/src/main/java/com/edu/teaching/domain/vo/MakeupLessonVO.java`
8. `/edu-teaching/src/main/java/com/edu/teaching/mapper/MakeupLessonMapper.java`
9. `/edu-teaching/src/main/resources/mapper/teaching/MakeupLessonMapper.xml`
10. `/edu-teaching/src/main/java/com/edu/teaching/service/MakeupLessonService.java`
11. `/edu-teaching/src/main/java/com/edu/teaching/service/impl/MakeupLessonServiceImpl.java`
12. `/edu-teaching/src/main/java/com/edu/teaching/controller/MakeupLessonController.java`
13. `/edu-teaching/src/main/java/com/edu/teaching/event/LeaveApprovedEvent.java`
14. `/edu-teaching/src/main/java/com/edu/teaching/event/MakeupArrangedEvent.java`
15. `/edu-teaching/src/main/java/com/edu/teaching/event/listener/LeaveEventListener.java`

### 修改文件
1. `/edu-teaching/src/main/java/com/edu/teaching/service/impl/LeaveRequestServiceImpl.java`
2. `/edu-teaching/src/main/java/com/edu/teaching/controller/LeaveRequestController.java`

## 总结

本实现完成了请假申请、审批和补课安排的完整功能，包括：
- 完整的实体、DTO、VO定义
- Mapper接口和XML配置
- 服务层业务逻辑实现
- RESTful API接口
- 事件驱动的通知机制
- 业务校验和冲突检测

代码遵循Spring Boot最佳实践，使用MyBatis-Plus简化数据访问，通过事件机制实现模块解耦，为后续功能扩展提供了良好的基础。
