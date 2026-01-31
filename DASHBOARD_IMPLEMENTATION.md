# 数据看板接口实现文档

## 概述

本文档描述了教育机构学生管理系统的数据看板接口实现，包含任务 24.2（招生数据看板）、24.3（营收数据看板）和 24.4（教学数据看板）的完整实现。

## 实现内容

### 1. 招生数据看板接口（任务 24.2）

**接口路径**: `GET /system/dashboard/marketing`

**功能特性**:
- 线索数量统计（总数、待跟进、已转化）
- 试听数量统计（总数、本月试听、已转化）
- 转化率统计（线索转化率、试听转化率）
- 线索来源分布
- 线索趋势（近30天）
- 转化趋势（近30天）
- 支持多校区数据隔离

**返回数据结构**:
```json
{
  "leadCount": 150,
  "pendingLeadCount": 45,
  "convertedLeadCount": 30,
  "newLeadThisMonth": 25,
  "convertedThisMonth": 8,
  "conversionRate": 32.0,
  "trialCount": 80,
  "trialThisMonth": 15,
  "trialConvertedCount": 40,
  "trialConversionRate": 50.0,
  "sourceDistribution": [
    {"name": "online_ad", "value": 60},
    {"name": "referral", "value": 40}
  ],
  "leadTrend": [
    {"date": "2026-01-01", "count": 5},
    {"date": "2026-01-02", "count": 8}
  ],
  "conversionTrend": [
    {"date": "2026-01-01", "count": 2},
    {"date": "2026-01-02", "count": 3}
  ]
}
```

### 2. 营收数据看板接口（任务 24.3）

**接口路径**: `GET /system/dashboard/finance`

**功能特性**:
- 收入统计（今日、本周、本月、本年）
- 本月退费统计
- 欠费统计（待收款、逾期欠费）
- 合同总数
- 收款方式分布（微信、支付宝、现金等）
- 收入趋势图数据（近30天）
- 支持多校区数据隔离

**返回数据结构**:
```json
{
  "incomeToday": 5000.00,
  "incomeThisWeek": 35000.00,
  "incomeThisMonth": 150000.00,
  "incomeThisYear": 1800000.00,
  "refundThisMonth": 5000.00,
  "pendingAmount": 80000.00,
  "overdueAmount": 15000.00,
  "contractCount": 120,
  "paymentMethodDistribution": [
    {"name": "wechat", "count": 50, "amount": 80000.00},
    {"name": "alipay", "count": 30, "amount": 45000.00},
    {"name": "cash", "count": 20, "amount": 25000.00}
  ],
  "incomeTrend": [
    {"date": "2026-01-01", "amount": 5000.00},
    {"date": "2026-01-02", "amount": 8000.00}
  ]
}
```

### 3. 教学数据看板接口（任务 24.4）

**接口路径**: `GET /system/dashboard/teaching`

**功能特性**:
- 学员数量统计（总数、在读、试听、潜在）
- 班级数量统计（总数、进行中、已结业）
- 教师数量统计（总数、在职、休假）
- 课程数量统计
- 课节统计（今日、本周）
- 考勤率统计（本周）
- 支持多校区数据隔离

**返回数据结构**:
```json
{
  "todayScheduleCount": 15,
  "weekScheduleCount": 85,
  "classCount": 25,
  "ongoingClassCount": 20,
  "completedClassCount": 5,
  "teacherCount": 30,
  "activeTeacherCount": 28,
  "onLeaveTeacherCount": 2,
  "courseCount": 15,
  "studentCount": 200,
  "enrolledStudentCount": 150,
  "trialStudentCount": 30,
  "potentialStudentCount": 20,
  "attendanceRate": 92.5
}
```

## 技术实现

### 1. 核心文件

#### VO 类
**文件**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/domain/vo/DashboardVO.java`

包含四个内部类：
- `StudentStats`: 学员统计数据
- `FinanceStats`: 财务统计数据（增强版）
- `TeachingStats`: 教学统计数据（增强版）
- `MarketingStats`: 营销统计数据（增强版）

#### Mapper 接口
**文件**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/mapper/DashboardMapper.java`

新增方法：
- 财务统计：`sumIncomeToday`, `sumIncomeThisWeek`, `sumIncomeThisYear`, `sumOverdueAmount`, `getPaymentMethodDistribution`
- 教学统计：`countTeachersByStatus`, `countCourses`
- 营销统计：`countLeadsByStatus`, `countTrials`, `countTrialsThisMonth`, `countTrialConverted`, `getLeadTrend`, `getConversionTrend`

#### Mapper XML
**文件**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/resources/mapper/system/DashboardMapper.xml`

实现了所有统计查询的 SQL，包括：
- 复杂的 GROUP BY 聚合查询
- 时间范围筛选（今日、本周、本月、本年）
- 多校区数据隔离
- 趋势数据查询

#### Service 实现
**文件**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/service/impl/DashboardServiceImpl.java`

特性：
- 添加了 `@Cacheable` 注解实现 Redis 缓存
- 缓存 TTL 为 5 分钟
- 支持按校区 ID 缓存
- 空值不缓存

#### Controller
**文件**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/controller/DashboardController.java`

特性：
- 添加了 Swagger/Knife4j API 文档注解
- 支持校区 ID 参数（可选）
- 统一返回 Result 包装类

#### 缓存配置
**文件**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-framework/src/main/java/com/edu/framework/redis/CacheConfig.java`

配置：
- 默认缓存 TTL：5 分钟
- 针对数据看板的四个缓存区域单独配置
- 使用 JSON 序列化
- 不缓存 null 值

### 2. 数据库查询优化

#### 索引使用
所有查询都利用了现有的数据库索引：
- `campus_id` 索引：支持多校区查询
- `status` 索引：快速状态筛选
- `create_time`/`update_time` 索引：时间范围查询
- `deleted` 索引：逻辑删除过滤

#### 查询优化技巧
1. 使用 `COALESCE` 处理 NULL 值
2. 使用 `DATE_FORMAT` 进行时间分组
3. 使用 `CASE WHEN` 进行条件聚合
4. 使用 `INNER JOIN` 关联查询
5. 使用 `GROUP BY` 进行分组统计

### 3. 缓存策略

#### 缓存键设计
```
dashboard:student:{campusId}
dashboard:finance:{campusId}
dashboard:teaching:{campusId}
dashboard:marketing:{campusId}
```

如果 `campusId` 为 null，则使用 `all` 作为键。

#### 缓存失效策略
- TTL：5 分钟自动过期
- 可通过 Redis 命令手动清除缓存
- 建议在数据更新时主动清除相关缓存

### 4. 多校区数据隔离

所有查询都支持 `campusId` 参数：
- 传入 `campusId`：只查询该校区数据
- 不传 `campusId`：查询所有校区数据

实现方式：
```xml
<if test="campusId != null">
    AND campus_id = #{campusId}
</if>
```

## API 接口文档

### 1. 获取完整数据看板

**请求**:
```
GET /system/dashboard?campusId=1
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "studentStats": { ... },
    "financeStats": { ... },
    "teachingStats": { ... },
    "marketingStats": { ... }
  }
}
```

### 2. 获取学员统计

**请求**:
```
GET /system/dashboard/student?campusId=1
```

### 3. 获取财务统计

**请求**:
```
GET /system/dashboard/finance?campusId=1
```

### 4. 获取教学统计

**请求**:
```
GET /system/dashboard/teaching?campusId=1
```

### 5. 获取营销统计

**请求**:
```
GET /system/dashboard/marketing?campusId=1
```

## 测试建议

### 1. 单元测试
- 测试各个统计方法的正确性
- 测试多校区数据隔离
- 测试空数据情况
- 测试缓存功能

### 2. 性能测试
- 测试大数据量下的查询性能
- 测试缓存命中率
- 测试并发访问性能

### 3. 集成测试
- 测试完整的 API 调用流程
- 测试数据一致性
- 测试异常情况处理

## 部署说明

### 1. 数据库准备
确保以下表已创建：
- `stu_student` - 学员表
- `fin_contract` - 合同表
- `fin_payment` - 收款记录表
- `fin_refund` - 退费记录表
- `tch_class` - 班级表
- `tch_teacher` - 教师表
- `tch_course` - 课程表
- `tch_schedule` - 排课表
- `tch_attendance` - 考勤表
- `mkt_lead` - 线索表
- `mkt_trial_lesson` - 试听记录表

### 2. Redis 配置
确保 Redis 服务已启动并正确配置连接信息。

### 3. 应用启动
```bash
cd /Users/liujiandong/Documents/work/package/edu/edu-server
mvn clean install -DskipTests
mvn spring-boot:run -pl edu-admin
```

### 4. 访问 API 文档
启动后访问：http://localhost:8080/doc.html

## 性能优化建议

### 1. 数据库优化
- 定期分析慢查询日志
- 优化索引策略
- 考虑使用物化视图存储统计结果
- 对历史数据进行归档

### 2. 缓存优化
- 根据实际业务调整缓存 TTL
- 实现缓存预热机制
- 监控缓存命中率
- 实现缓存更新策略

### 3. 查询优化
- 对于复杂统计，考虑使用定时任务预计算
- 实现分页查询避免大数据量返回
- 使用读写分离减轻主库压力

## 监控建议

### 1. 性能监控
- 监控 API 响应时间
- 监控数据库查询时间
- 监控缓存命中率
- 监控 Redis 内存使用

### 2. 业务监控
- 监控各统计数据的变化趋势
- 设置异常数据告警
- 记录用户访问日志

## 总结

本次实现完成了数据看板的三个核心模块：
1. **招生数据看板**：提供线索、试听、转化的全面统计
2. **营收数据看板**：提供多时间维度的收入统计和分析
3. **教学数据看板**：提供学员、班级、教师、课程的综合统计

技术亮点：
- 使用 Redis 缓存优化查询性能
- 支持多校区数据隔离
- 提供趋势分析数据
- 完善的 API 文档
- 良好的代码结构和可维护性

所有代码已实现并可直接使用，建议进行充分测试后部署到生产环境。
