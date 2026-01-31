# 班级升班和结业功能实现文档

## 概述

本文档描述了教学管理系统中班级升班（任务 14.5）和班级结业（任务 14.6）功能的完整实现。

## 功能特性

### 1. 班级升班功能（任务 14.5）

#### 功能描述
- 支持整个班级升级到下一级别课程
- 自动处理学员从原班级转移到新班级
- 可选择转入已有班级或创建新班级
- 保留原班级历史记录（可选）
- 支持批量升班操作

#### 核心流程
1. 验证原班级状态（必须是进行中或已结班）
2. 查询班级所有在读学员
3. 确定目标班级（已有班级或创建新班级）
4. 转移所有学员到目标班级
5. 更新原班级状态（保留或删除）
6. 发布升班事件通知

#### API 接口

**单个班级升班**
```
POST /teaching/class/promote
Content-Type: application/json

{
  "classId": 1,                    // 当前班级ID（必填）
  "targetCourseId": 10,            // 目标课程ID（必填）
  "targetClassId": 20,             // 目标班级ID（可选，不填则创建新班级）
  "newClassName": "高级班A",        // 新班级名称（创建新班级时必填）
  "newClassCode": "ADV-A",         // 新班级编码（创建新班级时必填）
  "teacherId": 5,                  // 主讲教师ID（可选）
  "assistantId": 6,                // 助教ID（可选）
  "classroomId": 3,                // 教室ID（可选）
  "capacity": 30,                  // 班级容量（可选）
  "keepOriginalClass": true,       // 是否保留原班级（默认true）
  "remark": "升班备注"
}
```

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "originalClassId": 1,
    "originalClassName": "初级班A",
    "targetClassId": 20,
    "targetClassName": "高级班A",
    "newClassCreated": true,
    "transferredStudentCount": 25,
    "transferredStudentIds": [101, 102, 103, ...],
    "promotionTime": "2026-01-31T10:30:00",
    "success": true,
    "failureReason": null
  }
}
```

**批量班级升班**
```
POST /teaching/class/promote/batch
Content-Type: application/json

{
  "classIds": [1, 2, 3],           // 班级ID列表（必填）
  "targetCourseId": 10,            // 目标课程ID（必填）
  "keepOriginalClass": true,       // 是否保留原班级（默认true）
  "remark": "批量升班"
}
```

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 3,
    "successCount": 2,
    "failureCount": 1,
    "results": [
      {
        "originalClassId": 1,
        "success": true,
        ...
      },
      {
        "originalClassId": 2,
        "success": true,
        ...
      },
      {
        "originalClassId": 3,
        "success": false,
        "failureReason": "班级没有在读学员，无法升班"
      }
    ]
  }
}
```

### 2. 班级结业功能（任务 14.6）

#### 功能描述
- 标记班级完成学习
- 更新所有学员状态为已结业
- 生成结业统计数据（学员数量、课时统计、出勤率等）
- 支持结业证书生成（可选）
- 支持批量结业操作

#### 核心流程
1. 验证班级状态（必须是进行中）
2. 查询班级所有在读学员
3. 更新学员状态为已结业
4. 更新班级状态为已结班
5. 生成结业统计数据
6. 生成结业证书（可选）
7. 发布结业事件通知

#### API 接口

**单个班级结业**
```
POST /teaching/class/graduate
Content-Type: application/json

{
  "classId": 1,                    // 班级ID（必填）
  "graduationDate": "2026-01-31",  // 结业日期（可选，默认当前日期）
  "generateCertificate": true,     // 是否生成结业证书（默认false）
  "graduationComment": "优秀班级", // 结业评语（可选）
  "remark": "结业备注"
}
```

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "classId": 1,
    "className": "初级班A",
    "graduationDate": "2026-01-31",
    "graduatedStudentCount": 25,
    "graduatedStudentIds": [101, 102, 103, ...],
    "totalLessons": 48,
    "completedLessons": 46,
    "attendanceRate": 95.8,
    "certificateGenerated": true,
    "certificateIds": [1001, 1002, 1003, ...],
    "graduationTime": "2026-01-31T10:30:00",
    "success": true,
    "failureReason": null
  }
}
```

**批量班级结业**
```
POST /teaching/class/graduate/batch
Content-Type: application/json

{
  "classIds": [1, 2, 3],           // 班级ID列表（必填）
  "graduationDate": "2026-01-31",  // 结业日期（可选）
  "generateCertificate": true,     // 是否生成结业证书（默认false）
  "graduationComment": "优秀班级", // 结业评语（可选）
  "remark": "批量结业"
}
```

**响应示例**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 3,
    "successCount": 3,
    "failureCount": 0,
    "results": [
      {
        "classId": 1,
        "success": true,
        ...
      },
      {
        "classId": 2,
        "success": true,
        ...
      },
      {
        "classId": 3,
        "success": true,
        ...
      }
    ]
  }
}
```

## 技术实现

### 1. 数据模型

#### 班级实体（TeachClass）
- 状态字段：pending（待开班）、ongoing（进行中）、finished（已结班）、cancelled（已取消）

#### 班级学员关联实体（ClassStudent）
- 状态字段：active（在读）、left（已退出）、graduated（已结业）
- 新增 graduated 状态用于区分结业学员

### 2. 核心类文件

#### DTO（数据传输对象）
- `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/ClassPromotionDTO.java` - 班级升班请求
- `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/BatchClassPromotionDTO.java` - 批量升班请求
- `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/ClassGraduationDTO.java` - 班级结业请求
- `/edu-teaching/src/main/java/com/edu/teaching/domain/dto/BatchClassGraduationDTO.java` - 批量结业请求

#### VO（视图对象）
- `/edu-teaching/src/main/java/com/edu/teaching/domain/vo/ClassPromotionResultVO.java` - 升班结果
- `/edu-teaching/src/main/java/com/edu/teaching/domain/vo/BatchClassPromotionResultVO.java` - 批量升班结果
- `/edu-teaching/src/main/java/com/edu/teaching/domain/vo/ClassGraduationResultVO.java` - 结业结果
- `/edu-teaching/src/main/java/com/edu/teaching/domain/vo/BatchClassGraduationResultVO.java` - 批量结业结果

#### 事件（Event）
- `/edu-teaching/src/main/java/com/edu/teaching/event/ClassPromotionEvent.java` - 班级升班事件
- `/edu-teaching/src/main/java/com/edu/teaching/event/ClassGraduationEvent.java` - 班级结业事件

#### 服务层
- `/edu-teaching/src/main/java/com/edu/teaching/service/TeachClassService.java` - 服务接口（新增方法）
- `/edu-teaching/src/main/java/com/edu/teaching/service/impl/TeachClassServiceImpl.java` - 服务实现

#### 控制器
- `/edu-teaching/src/main/java/com/edu/teaching/controller/TeachClassController.java` - REST API 控制器

#### 数据库迁移
- `/edu-admin/src/main/resources/db/migration/V1.0.16__update_class_student_status.sql` - 数据库迁移脚本

### 3. 事务管理

所有升班和结业操作都使用 `@Transactional` 注解确保数据一致性：
- 学员状态更新
- 班级状态更新
- 班级人数统计
- 新班级创建（升班时）

如果任何步骤失败，整个事务将回滚。

### 4. 事件发布

使用 Spring 事件机制发布业务事件：
- `ClassPromotionEvent` - 升班完成后发布
- `ClassGraduationEvent` - 结业完成后发布

其他模块可以监听这些事件进行后续处理：
- 发送通知给学员和家长
- 更新学员档案
- 生成统计报表
- 记录操作日志

### 5. 状态流转验证

#### 升班状态验证
- 原班级必须是 `ongoing`（进行中）或 `finished`（已结班）状态
- 目标班级必须是 `pending`（待开班）或 `ongoing`（进行中）状态
- 班级必须有在读学员（status = 'active'）

#### 结业状态验证
- 班级必须是 `ongoing`（进行中）状态
- 班级必须有在读学员（status = 'active'）

### 6. 批量操作处理

批量操作采用逐个处理策略：
- 每个班级独立处理，互不影响
- 记录每个班级的处理结果（成功/失败）
- 返回汇总统计和详细结果列表
- 部分失败不影响其他班级的处理

## 使用示例

### 场景1：班级升班到已有班级

```bash
curl -X POST http://localhost:8080/teaching/class/promote \
  -H "Content-Type: application/json" \
  -d '{
    "classId": 1,
    "targetCourseId": 10,
    "targetClassId": 20,
    "keepOriginalClass": true,
    "remark": "升入高级班"
  }'
```

### 场景2：班级升班并创建新班级

```bash
curl -X POST http://localhost:8080/teaching/class/promote \
  -H "Content-Type: application/json" \
  -d '{
    "classId": 1,
    "targetCourseId": 10,
    "newClassName": "高级班A",
    "newClassCode": "ADV-A",
    "teacherId": 5,
    "capacity": 30,
    "keepOriginalClass": true,
    "remark": "创建新的高级班"
  }'
```

### 场景3：批量班级升班

```bash
curl -X POST http://localhost:8080/teaching/class/promote/batch \
  -H "Content-Type: application/json" \
  -d '{
    "classIds": [1, 2, 3],
    "targetCourseId": 10,
    "keepOriginalClass": true,
    "remark": "批量升入高级课程"
  }'
```

### 场景4：班级结业

```bash
curl -X POST http://localhost:8080/teaching/class/graduate \
  -H "Content-Type: application/json" \
  -d '{
    "classId": 1,
    "graduationDate": "2026-01-31",
    "generateCertificate": true,
    "graduationComment": "优秀班级，全员通过考核",
    "remark": "2026年春季班结业"
  }'
```

### 场景5：批量班级结业

```bash
curl -X POST http://localhost:8080/teaching/class/graduate/batch \
  -H "Content-Type: application/json" \
  -d '{
    "classIds": [1, 2, 3],
    "graduationDate": "2026-01-31",
    "generateCertificate": true,
    "graduationComment": "优秀班级",
    "remark": "2026年春季班批量结业"
  }'
```

## 扩展点

### 1. 结业证书生成

当前实现中，结业证书生成逻辑标记为 TODO，可以扩展为：
- 调用证书生成服务
- 生成 PDF 格式证书
- 存储证书文件
- 返回证书下载链接

### 2. 课时统计

当前实现中，课时统计数据标记为 TODO，可以扩展为：
- 查询班级排课记录
- 统计总课时数和已完成课时数
- 计算出勤率
- 生成学习报告

### 3. 事件监听器

可以创建事件监听器处理升班和结业事件：
- 发送通知消息
- 更新学员档案
- 生成统计报表
- 记录操作日志

示例：
```java
@Component
public class ClassEventListener {

    @EventListener
    public void handleClassPromotion(ClassPromotionEvent event) {
        // 发送升班通知
        // 更新学员档案
        // 记录操作日志
    }

    @EventListener
    public void handleClassGraduation(ClassGraduationEvent event) {
        // 发送结业通知
        // 生成结业报告
        // 更新学员状态
    }
}
```

## 测试建议

### 单元测试
- 测试升班逻辑（转入已有班级、创建新班级）
- 测试结业逻辑（生成证书、统计数据）
- 测试状态验证
- 测试事务回滚

### 集成测试
- 测试完整的升班流程
- 测试完整的结业流程
- 测试批量操作
- 测试事件发布

### 边界测试
- 空班级升班/结业
- 容量不足的目标班级
- 无效的班级状态
- 并发操作

## 注意事项

1. **数据一致性**：所有操作都在事务中执行，确保数据一致性
2. **状态验证**：严格验证班级和学员状态，防止非法操作
3. **容量检查**：升班时检查目标班级容量，防止超员
4. **历史记录**：升班时可选择保留原班级作为历史记录
5. **批量操作**：批量操作采用逐个处理策略，部分失败不影响其他记录
6. **事件通知**：通过事件机制解耦业务逻辑，便于扩展

## 数据库变更

执行以下 SQL 脚本更新数据库：

```sql
-- 修改 tch_class_student 表的 status 字段注释
ALTER TABLE tch_class_student MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'active'
COMMENT '状态：active-在读，left-已退出，graduated-已结业';

-- 添加索引以提高查询性能
CREATE INDEX idx_class_student_status ON tch_class_student(class_id, status);
CREATE INDEX idx_student_class_status ON tch_class_student(student_id, status);
```

或者使用 Flyway 自动迁移：
```bash
mvn flyway:migrate
```

## API 文档

启动应用后，访问 Knife4j 文档查看完整的 API 接口：
- URL: http://localhost:8080/doc.html
- 模块: 班级管理
- 接口: 班级升班、批量班级升班、班级结业、批量班级结业
