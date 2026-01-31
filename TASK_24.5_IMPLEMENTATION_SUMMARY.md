# 数据预警接口实现总结（任务24.5）

## 实现概述

本次任务成功实现了教育机构学生管理系统的数据看板预警功能，包括11种预警类型的检测、预警规则配置管理、预警列表查询和预警汇总统计。

## 实现的功能

### 1. API接口（4个）

#### 1.1 GET /system/dashboard/warning/list - 获取预警列表
- **功能**：分页查询数据预警信息
- **参数**：
  - `pageNum`: 页码（默认1）
  - `pageSize`: 每页大小（默认10）
  - `campusId`: 校区ID（可选，不传则查询所有校区）
  - `warningType`: 预警类型（可选）
  - `warningLevel`: 预警级别（可选：normal/warning/urgent）
- **返回**：分页的预警列表

#### 1.2 GET /system/dashboard/warning/summary - 获取预警汇总
- **功能**：获取预警统计汇总信息
- **参数**：
  - `campusId`: 校区ID（可选）
- **返回**：预警汇总统计，包括：
  - 总预警数、各级别预警数
  - 业务/运营/财务预警分类统计
  - 预警类型分布

#### 1.3 POST /system/dashboard/warning/config - 配置预警规则
- **功能**：创建新的预警规则配置
- **参数**：WarningConfigDTO（JSON Body）
  - `warningType`: 预警类型（必填）
  - `warningName`: 预警名称（必填）
  - `warningLevel`: 预警级别（必填）
  - `thresholdConfig`: 阈值配置JSON（必填）
  - `enabled`: 是否启用（必填）
  - `campusId`: 校区ID（可选，为空表示全局配置）
  - `remark`: 备注（可选）
- **返回**：配置ID

#### 1.4 PUT /system/dashboard/warning/config/{id} - 更新预警规则
- **功能**：更新已有的预警规则配置
- **参数**：
  - `id`: 配置ID（路径参数）
  - WarningConfigDTO（JSON Body）
- **返回**：是否成功

### 2. 预警类型（11种）

#### 业务预警（5种）
1. **course_hour_low** - 课时不足预警
   - 触发条件：剩余课时 < 阈值（默认5小时）
   - 级别：warning（警告）
   - 阈值配置：`{"courseHourThreshold":5}`

2. **course_hour_expire** - 课时即将到期预警
   - 触发条件：距离到期 < 阈值（默认30天）
   - 级别：warning（警告）
   - 阈值配置：`{"daysThreshold":30}`

3. **overdue** - 欠费预警
   - 触发条件：欠费天数 > 阈值（默认7天）
   - 级别：urgent（紧急）
   - 阈值配置：`{"daysThreshold":7}`

4. **contract_expire** - 合同即将到期预警
   - 触发条件：距离到期 < 阈值（默认30天）
   - 级别：warning（警告）
   - 阈值配置：`{"daysThreshold":30}`

5. **student_loss** - 学员流失预警
   - 触发条件：超过阈值天数未上课（默认30天）
   - 级别：urgent（紧急）
   - 阈值配置：`{"daysThreshold":30}`

#### 运营预警（4种）
6. **class_full** - 班级满员预警
   - 触发条件：报名人数 >= 班级容量
   - 级别：normal（正常）
   - 阈值配置：`{}`

7. **schedule_conflict** - 教师排课冲突预警
   - 触发条件：教师存在时间冲突的排课
   - 级别：urgent（紧急）
   - 阈值配置：`{}`

8. **classroom_conflict** - 教室使用冲突预警
   - 触发条件：教室存在时间冲突的使用
   - 级别：urgent（紧急）
   - 阈值配置：`{}`

9. **trial_conversion_low** - 试听转化率低预警
   - 触发条件：试听转化率 < 阈值（默认30%）
   - 级别：warning（警告）
   - 阈值配置：`{"rateThreshold":0.3}`

#### 财务预警（2种）
10. **income_abnormal** - 收入异常预警
    - 触发条件：当月收入 < 上月收入 × 阈值（默认80%）
    - 级别：warning（警告）
    - 阈值配置：`{"rateThreshold":0.8}`

11. **refund_rate_high** - 退费率高预警
    - 触发条件：退费率 > 阈值（默认10%）
    - 级别：urgent（紧急）
    - 阈值配置：`{"rateThreshold":0.1}`

### 3. 预警级别

- **normal（正常）** - 绿色，一般性提醒
- **warning（警告）** - 橙色，需要关注
- **urgent（紧急）** - 红色，需要立即处理

## 技术实现

### 1. 文件结构

```
edu-system/
├── src/main/java/com/edu/system/
│   ├── controller/
│   │   └── DashboardWarningController.java          # 预警控制器
│   ├── service/
│   │   ├── DashboardWarningService.java             # 预警服务接口
│   │   └── impl/
│   │       └── DashboardWarningServiceImpl.java     # 预警服务实现
│   ├── mapper/
│   │   └── DashboardWarningMapper.java              # 预警Mapper接口
│   ├── domain/
│   │   ├── entity/
│   │   │   └── WarningConfig.java                   # 预警配置实体
│   │   ├── dto/
│   │   │   └── WarningConfigDTO.java                # 预警配置DTO
│   │   └── vo/
│   │       └── WarningVO.java                       # 预警VO
│   └── resources/mapper/system/
│       └── DashboardWarningMapper.xml               # Mapper XML
└── edu-admin/src/main/resources/db/migration/
    └── V1.0.21__add_warning_config_table.sql        # 数据库迁移脚本
```

### 2. 核心技术点

#### 2.1 复杂SQL聚合查询
- 使用多表JOIN查询关联学员、课程、合同、支付等数据
- 使用聚合函数（COUNT、SUM、AVG）进行统计计算
- 使用日期函数（DATEDIFF、DATE_FORMAT）进行时间计算
- 使用子查询进行复杂的数据对比（如收入环比）

#### 2.2 动态预警规则配置
- 预警配置存储在数据库表 `sys_warning_config`
- 阈值配置使用JSON格式存储，灵活可扩展
- 支持全局配置和校区级配置
- 校区级配置优先于全局配置

#### 2.3 分页支持
- 使用MyBatis-Plus的Page对象
- 先查询所有预警数据，再进行内存分页
- 支持按预警类型和级别过滤

#### 2.4 API文档
- 使用Swagger/Knife4j注解
- 完整的接口描述和参数说明
- 支持在线测试

### 3. 数据库设计

#### sys_warning_config 表结构
```sql
CREATE TABLE sys_warning_config (
    id BIGINT PRIMARY KEY,
    warning_type VARCHAR(50) NOT NULL,      -- 预警类型
    warning_name VARCHAR(100) NOT NULL,     -- 预警名称
    warning_level VARCHAR(20) NOT NULL,     -- 预警级别
    threshold_config TEXT NOT NULL,         -- 阈值配置（JSON）
    enabled TINYINT NOT NULL DEFAULT 1,     -- 是否启用
    campus_id BIGINT,                       -- 校区ID
    remark VARCHAR(500),                    -- 备注
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_warning_type (warning_type),
    INDEX idx_campus_id (campus_id),
    INDEX idx_enabled (enabled)
);
```

### 4. 关键实现逻辑

#### 4.1 预警数据获取流程
1. 获取启用的预警配置（支持校区级和全局配置）
2. 根据配置类型调用对应的Mapper方法
3. 从数据库查询符合条件的预警数据
4. 构建预警VO对象，包含详细描述
5. 返回预警列表

#### 4.2 预警汇总统计
1. 获取所有预警数据
2. 按预警级别分组统计（urgent/warning/normal）
3. 按预警类别分组统计（业务/运营/财务）
4. 按预警类型分组统计，生成分布数据
5. 返回汇总统计结果

#### 4.3 配置管理
1. 创建配置：验证JSON格式，保存到数据库
2. 更新配置：检查配置是否存在，验证JSON格式，更新数据库
3. 查询配置：支持按校区查询，包含全局配置
4. 删除配置：逻辑删除

## API使用示例

### 1. 获取预警列表
```bash
GET /system/dashboard/warning/list?pageNum=1&pageSize=10&campusId=1&warningLevel=urgent
```

### 2. 获取预警汇总
```bash
GET /system/dashboard/warning/summary?campusId=1
```

### 3. 配置预警规则
```bash
POST /system/dashboard/warning/config
Content-Type: application/json

{
  "warningType": "course_hour_low",
  "warningName": "课时不足预警",
  "warningLevel": "warning",
  "thresholdConfig": "{\"courseHourThreshold\":5}",
  "enabled": 1,
  "campusId": 1,
  "remark": "当学员剩余课时低于5小时时触发预警"
}
```

### 4. 更新预警规则
```bash
PUT /system/dashboard/warning/config/1
Content-Type: application/json

{
  "warningType": "course_hour_low",
  "warningName": "课时不足预警",
  "warningLevel": "urgent",
  "thresholdConfig": "{\"courseHourThreshold\":3}",
  "enabled": 1,
  "campusId": 1,
  "remark": "当学员剩余课时低于3小时时触发预警"
}
```

## 完成标准检查

- ✅ 4个API接口实现完成
  - GET /system/dashboard/warning/list
  - GET /system/dashboard/warning/summary
  - POST /system/dashboard/warning/config
  - PUT /system/dashboard/warning/config/{id}
  - 额外实现：GET /system/dashboard/warning/config/list（查询配置列表）
  - 额外实现：GET /system/dashboard/warning/config/{id}（查询配置详情）
  - 额外实现：DELETE /system/dashboard/warning/config/{id}（删除配置）

- ✅ 预警规则配置功能完整
  - 支持创建、更新、查询、删除配置
  - 支持全局配置和校区级配置
  - 支持启用/禁用配置
  - 阈值配置使用JSON格式，灵活可扩展

- ✅ 预警查询功能完整
  - 支持分页查询
  - 支持按校区、预警类型、预警级别筛选
  - 实现11种预警类型的检测
  - 提供详细的预警描述和业务信息

- ✅ 完整的API文档
  - 使用Swagger/Knife4j注解
  - 包含接口描述、参数说明、返回值说明
  - 支持在线测试

- ✅ 创建实现总结文档
  - 本文档

## 技术亮点

1. **灵活的配置系统**：支持全局和校区级配置，阈值使用JSON格式存储，易于扩展
2. **复杂SQL查询**：使用多表JOIN、聚合函数、子查询实现复杂的预警检测逻辑
3. **完整的预警体系**：覆盖业务、运营、财务三大类11种预警类型
4. **清晰的代码结构**：遵循分层架构，职责分明
5. **完善的API文档**：使用Swagger注解，提供详细的接口说明

## 后续优化建议

1. **性能优化**：
   - 对于大数据量场景，可以考虑使用缓存
   - 可以将预警数据定时计算并存储，避免实时查询

2. **功能扩展**：
   - 添加预警通知功能（邮件、短信、站内消息）
   - 添加预警历史记录功能
   - 添加预警处理状态跟踪

3. **用户体验**：
   - 前端可以使用不同颜色展示不同级别的预警
   - 添加预警趋势图表
   - 支持预警导出功能

## 文件清单

### 新增文件（9个）
1. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/controller/DashboardWarningController.java`
2. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/service/DashboardWarningService.java`
3. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/service/impl/DashboardWarningServiceImpl.java`
4. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/mapper/DashboardWarningMapper.java`
5. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/domain/entity/WarningConfig.java`
6. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/domain/dto/WarningConfigDTO.java`
7. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/domain/vo/WarningVO.java`
8. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/resources/mapper/system/DashboardWarningMapper.xml`
9. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-admin/src/main/resources/db/migration/V1.0.21__add_warning_config_table.sql`

## 总结

本次任务成功实现了数据看板的预警功能，包括11种预警类型的检测、预警规则配置管理、预警列表查询和预警汇总统计。实现了4个核心API接口，并额外提供了3个辅助接口。使用复杂SQL聚合查询实现了多维度的预警检测，支持灵活的预警规则配置，提供了完整的API文档。代码结构清晰，遵循项目规范，易于维护和扩展。
