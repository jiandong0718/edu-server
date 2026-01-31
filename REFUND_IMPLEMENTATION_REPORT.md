# 退费申请后端接口实现报告

## 项目信息
- **项目名称**: 教育机构学生管理系统
- **模块**: edu-finance (财务模块)
- **任务编号**: 18.5-18.7
- **实施日期**: 2026-01-31
- **实施状态**: ✅ 已完成

---

## 任务概述

本次任务实现了完整的退费申请、金额计算和审批功能，包括：

### 任务18.5：实现退费申请接口 ✅
- 接口：`POST /finance/refund/apply`
- 功能：创建退费申请，自动计算退费金额，生成退费单号
- 状态：待审批（pending）

### 任务18.6：实现退费金额计算逻辑 ✅
- 接口：`GET /finance/refund/calculate/{contractId}`
- 功能：根据合同金额、已用课时、剩余课时计算退费金额
- 计算：扣除违约金（默认5%）

### 任务18.7：实现退费审批接口 ✅
- 接口：`POST /finance/refund/approve`
- 功能：审批退费申请（通过/拒绝）
- 记录：审批人、审批时间、审批意见
- 联动：审批通过后更新合同状态，冻结课时账户

---

## 实现的API接口

### 核心接口（任务要求）

| 接口 | 方法 | 路径 | 功能 | 状态 |
|------|------|------|------|------|
| 1 | POST | /finance/refund/apply | 提交退费申请 | ✅ |
| 2 | GET | /finance/refund/calculate/{contractId} | 计算退费金额 | ✅ |
| 3 | POST | /finance/refund/approve | 审批退费申请 | ✅ |

### 扩展接口（完善功能）

| 接口 | 方法 | 路径 | 功能 | 状态 |
|------|------|------|------|------|
| 4 | GET | /finance/refund/page | 分页查询退费申请列表 | ✅ |
| 5 | GET | /finance/refund/{id} | 获取退费申请详情 | ✅ |
| 6 | POST | /finance/refund/{id}/execute | 执行退款 | ✅ |
| 7 | DELETE | /finance/refund/{id} | 删除退费申请 | ✅ |

---

## 文件清单

### 1. 实体类（Entity）
```
/edu-finance/src/main/java/com/edu/finance/domain/entity/Refund.java
```
- 退费申请实体，对应数据库表 `fin_refund`
- 包含退费单号、合同ID、学员ID、申请金额、实际金额、违约金等字段
- 支持关联查询学员姓名、合同编号、校区名称、审批人姓名

### 2. 数据传输对象（DTO）
```
/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApplyDTO.java
/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundApproveDTO.java
/edu-finance/src/main/java/com/edu/finance/domain/dto/RefundCalculationDTO.java
```
- **RefundApplyDTO**: 退费申请请求参数
- **RefundApproveDTO**: 退费审批请求参数
- **RefundCalculationDTO**: 退费金额计算结果

### 3. 视图对象（VO）
```
/edu-finance/src/main/java/com/edu/finance/domain/vo/RefundVO.java
```
- 退费申请视图对象，用于前端展示
- 包含所有退费信息和关联信息

### 4. 数据访问层（Mapper）
```
/edu-finance/src/main/java/com/edu/finance/mapper/RefundMapper.java
/edu-finance/src/main/resources/mapper/finance/RefundMapper.xml
```
- MyBatis Mapper接口和XML映射文件
- 实现分页查询，支持多条件查询
- 关联查询学员、合同、校区、审批人信息

### 5. 业务逻辑层（Service）
```
/edu-finance/src/main/java/com/edu/finance/service/RefundService.java
/edu-finance/src/main/java/com/edu/finance/service/impl/RefundServiceImpl.java
```
- 退费服务接口和实现
- 核心方法：
  - `calculateRefundAmount`: 计算退费金额
  - `applyRefund`: 提交退费申请
  - `approveRefund`: 审批退费申请
  - `executeRefund`: 执行退款
  - `generateRefundNo`: 生成退费单号

### 6. 控制器层（Controller）
```
/edu-finance/src/main/java/com/edu/finance/controller/RefundController.java
```
- RESTful API控制器
- 使用Knife4j注解提供完整的API文档
- 统一响应格式，参数验证

### 7. 事件驱动（Event）
```
/edu-finance/src/main/java/com/edu/finance/event/RefundCompletedEvent.java
/edu-finance/src/main/java/com/edu/finance/listener/RefundCompletedListener.java
```
- 退费完成事件和监听器
- 异步处理后续操作（发送通知、记录日志等）

### 8. 数据库迁移（Migration）
```
/edu-admin/src/main/resources/db/migration/V1.0.3__finance_marketing_tables.sql
/edu-admin/src/main/resources/db/migration/V1.0.17__update_refund_table.sql
```
- 初始表结构创建
- 表结构更新（添加详细字段）

### 9. 文档（Documentation）
```
/edu-finance/REFUND_README.md
/edu-finance/REFUND_IMPLEMENTATION_SUMMARY.md
/edu-finance/REFUND_API_TEST_GUIDE.md
```
- 使用文档
- 实现总结
- 测试指南

---

## 技术实现

### 1. 技术栈
- **框架**: Spring Boot 3.2.x
- **ORM**: MyBatis-Plus 3.5.x
- **数据库**: MySQL 8.0+
- **API文档**: Knife4j (OpenAPI 3.0)
- **工具库**: Hutool, Lombok

### 2. 设计模式
- **分层架构**: Controller → Service → Mapper
- **DTO模式**: 数据传输对象分离
- **事件驱动**: Spring Event机制
- **事务管理**: Spring @Transactional

### 3. 核心特性

#### 退费金额计算
```java
可退金额 = 已支付金额 - 已消耗金额 - 违约金

其中：
- 单课时价格 = 已支付金额 / 总课时数
- 已消耗金额 = 单课时价格 × 已消耗课时数
- 违约金 = (已支付金额 - 已消耗金额) × 违约金比例（默认5%）
```

#### 退费单号生成
```java
格式：TF + 日期(yyyyMMdd) + 序号(4位)
示例：TF202601310001
```

#### 状态流转
```
pending (待审批) → approved (已通过) → refunded (已退款)
                 ↘ rejected (已拒绝)
```

#### 事务管理
- 退费申请：创建退费记录
- 退费审批：更新退费状态 + 更新合同状态 + 冻结课时账户
- 退款执行：更新退费状态 + 发布事件

---

## 数据库设计

### fin_refund 表结构
```sql
CREATE TABLE fin_refund (
    id BIGINT NOT NULL COMMENT 'ID',
    refund_no VARCHAR(50) NOT NULL COMMENT '退费单号',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    campus_id BIGINT COMMENT '校区ID',
    apply_amount DECIMAL(10,2) DEFAULT 0 COMMENT '申请退费金额',
    actual_amount DECIMAL(10,2) DEFAULT 0 COMMENT '实际退费金额',
    penalty_amount DECIMAL(10,2) DEFAULT 0 COMMENT '违约金',
    reason VARCHAR(200) COMMENT '退费原因',
    description VARCHAR(1000) COMMENT '退费说明',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
    apply_time DATETIME COMMENT '申请时间',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批备注',
    refund_time DATETIME COMMENT '退款时间',
    refund_method VARCHAR(20) COMMENT '退款方式',
    refund_transaction_no VARCHAR(100) COMMENT '退款交易号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_refund_no (refund_no),
    KEY idx_contract_id (contract_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退费记录表';
```

### 关联表
- `fin_contract`: 合同表
- `fin_class_hour_account`: 课时账户表
- `stu_student`: 学员表
- `sys_campus`: 校区表
- `sys_user`: 用户表（审批人）

---

## 业务流程

### 1. 退费申请流程
```
1. 学员/管理员选择合同
2. 系统计算退费金额
   ├─ 查询合同信息
   ├─ 查询课时账户
   ├─ 计算已消耗金额
   ├─ 计算违约金
   └─ 计算可退金额
3. 填写退费原因和说明
4. 提交申请
5. 系统生成退费单号
6. 状态设为"待审批"
```

### 2. 退费审批流程
```
1. 管理员查看退费申请
2. 查看计算结果和详情
3. 决定审批结果
   ├─ 通过
   │  ├─ 填写实际退费金额
   │  ├─ 填写审批备注
   │  ├─ 更新退费状态为"已通过"
   │  ├─ 更新合同状态为"已退费"
   │  └─ 冻结课时账户
   └─ 拒绝
      ├─ 填写拒绝原因
      └─ 更新退费状态为"已拒绝"
```

### 3. 退款执行流程
```
1. 财务人员查看已通过的申请
2. 执行退款操作
3. 更新退费状态为"已退款"
4. 记录退款时间和方式
5. 发布退费完成事件
6. 触发后续操作
   ├─ 发送通知
   ├─ 记录日志
   └─ 更新统计
```

---

## 数据验证

### 退费申请验证
- ✅ 合同必须存在
- ✅ 合同状态必须是"已签署"或"已完成"
- ✅ 同一合同不能有多个待审批的退费申请
- ✅ 退费原因不能为空

### 退费审批验证
- ✅ 退费申请必须存在
- ✅ 退费申请状态必须是"待审批"
- ✅ 审批结果必须是"approved"或"rejected"
- ✅ 审批通过时，实际退费金额必填
- ✅ 实际退费金额必须大于0
- ✅ 实际退费金额不能大于申请金额

### 退款执行验证
- ✅ 退费申请必须存在
- ✅ 退费申请状态必须是"已通过"

---

## API文档

### 访问方式
启动应用后访问：`http://localhost:8080/doc.html`

### 文档特性
- ✅ 完整的接口说明
- ✅ 参数说明和示例
- ✅ 响应格式说明
- ✅ 在线测试功能
- ✅ 模型定义
- ✅ 错误码说明

---

## 测试建议

### 1. 单元测试
```java
// 测试退费金额计算
@Test
public void testCalculateRefundAmount() {
    // 准备测试数据
    // 调用计算方法
    // 验证计算结果
}

// 测试退费单号生成
@Test
public void testGenerateRefundNo() {
    // 调用生成方法
    // 验证单号格式
}
```

### 2. 集成测试
```java
// 测试完整退费流程
@Test
public void testRefundProcess() {
    // 1. 提交退费申请
    // 2. 审批通过
    // 3. 执行退款
    // 4. 验证状态变化
}
```

### 3. 接口测试
使用Knife4j文档页面或Postman进行接口测试。

### 4. 性能测试
使用JMeter或Apache Bench进行并发测试。

---

## 完成标准检查

### ✅ 3个API接口实现完成
- [x] POST /finance/refund/apply - 提交退费申请
- [x] GET /finance/refund/calculate/{contractId} - 计算退费金额
- [x] POST /finance/refund/approve - 审批退费申请

### ✅ 退费金额计算逻辑正确
- [x] 根据合同金额、已用课时、剩余课时计算
- [x] 扣除违约金（默认5%）
- [x] 计算公式正确
- [x] 详细的计算说明

### ✅ 审批流程完整
- [x] 支持通过/拒绝
- [x] 记录审批人、审批时间、审批意见
- [x] 审批通过后更新合同状态
- [x] 审批通过后冻结课时账户
- [x] 状态流转控制

### ✅ 完整的API文档
- [x] 使用Knife4j注解
- [x] 接口说明完整
- [x] 参数说明完整
- [x] 响应格式说明
- [x] 在线测试功能

### ✅ 创建实现总结文档
- [x] REFUND_README.md - 使用文档
- [x] REFUND_IMPLEMENTATION_SUMMARY.md - 实现总结
- [x] REFUND_API_TEST_GUIDE.md - 测试指南

---

## 技术亮点

### 1. 完整的业务逻辑
- 涵盖退费申请、金额计算、审批、退款全流程
- 严格的数据校验和状态控制
- 完善的异常处理

### 2. 事务管理
- 使用Spring事务确保数据一致性
- 审批操作涉及多表更新，使用事务保证原子性

### 3. 事件驱动
- 使用Spring事件机制实现解耦
- 异步处理后续操作，不影响主流程

### 4. 灵活的计算逻辑
- 支持配置违约金比例
- 详细的计算说明
- 可扩展的计算规则

### 5. 完整的API文档
- 使用Knife4j提供在线文档
- 支持在线测试
- 清晰的接口说明

### 6. 关联查询优化
- 一次查询获取所有关联信息
- 减少数据库查询次数
- 提高查询效率

---

## 扩展建议

### 短期扩展
1. 对接支付系统，实现自动退款
2. 发送退费通知给学员和管理员
3. 增加退费原因分类和统计

### 中期扩展
1. 支持部分退费（退部分课时）
2. 支持退费规则配置（违约金比例、退费条件等）
3. 退费统计报表

### 长期扩展
1. 退费审批流程配置（多级审批）
2. 增加退费时效控制（如签约后多少天内可以全额退费）
3. 退费预测和分析

---

## 部署说明

### 1. 数据库迁移
```bash
# Flyway会自动执行迁移脚本
# 确保以下文件存在：
# - V1.0.3__finance_marketing_tables.sql
# - V1.0.17__update_refund_table.sql
```

### 2. 应用启动
```bash
cd /Users/liujiandong/Documents/work/package/edu/edu-server
mvn clean install -DskipTests
mvn spring-boot:run -pl edu-admin
```

### 3. 验证部署
```bash
# 访问健康检查接口
curl http://localhost:8080/actuator/health

# 访问API文档
open http://localhost:8080/doc.html
```

---

## 监控和维护

### 1. 日志监控
```bash
# 查看退费相关日志
tail -f logs/edu-admin.log | grep -i refund
```

### 2. 数据库监控
```sql
-- 查看退费申请统计
SELECT status, COUNT(*) as count
FROM fin_refund
WHERE deleted = 0
GROUP BY status;

-- 查看退费金额统计
SELECT
    DATE_FORMAT(apply_time, '%Y-%m') as month,
    COUNT(*) as count,
    SUM(apply_amount) as total_apply_amount,
    SUM(actual_amount) as total_actual_amount
FROM fin_refund
WHERE deleted = 0 AND status = 'refunded'
GROUP BY DATE_FORMAT(apply_time, '%Y-%m')
ORDER BY month DESC;
```

### 3. 性能监控
- 监控接口响应时间
- 监控数据库查询性能
- 监控事务执行时间

---

## 总结

本次任务成功实现了完整的退费申请后端接口，包括：

1. **任务18.5**: 退费申请接口 ✅
   - 自动计算退费金额
   - 生成退费单号
   - 状态控制

2. **任务18.6**: 退费金额计算逻辑 ✅
   - 根据课时消耗计算
   - 扣除违约金
   - 详细计算说明

3. **任务18.7**: 退费审批接口 ✅
   - 支持通过/拒绝
   - 记录审批信息
   - 联动更新合同和课时账户

### 技术实现
- ✅ 使用MyBatis-Plus
- ✅ 完整的Knife4j API文档
- ✅ 事务管理
- ✅ 状态流转控制
- ✅ 审批流程记录

### 文档完善
- ✅ 使用文档（REFUND_README.md）
- ✅ 实现总结（REFUND_IMPLEMENTATION_SUMMARY.md）
- ✅ 测试指南（REFUND_API_TEST_GUIDE.md）

### 代码质量
- ✅ 分层架构清晰
- ✅ 代码规范统一
- ✅ 注释完整
- ✅ 异常处理完善

---

## 附录

### A. 文件路径清单
```
edu-finance/
├── src/main/java/com/edu/finance/
│   ├── controller/
│   │   └── RefundController.java
│   ├── domain/
│   │   ├── dto/
│   │   │   ├── RefundApplyDTO.java
│   │   │   ├── RefundApproveDTO.java
│   │   │   └── RefundCalculationDTO.java
│   │   ├── entity/
│   │   │   └── Refund.java
│   │   └── vo/
│   │       └── RefundVO.java
│   ├── event/
│   │   └── RefundCompletedEvent.java
│   ├── listener/
│   │   └── RefundCompletedListener.java
│   ├── mapper/
│   │   └── RefundMapper.java
│   └── service/
│       ├── RefundService.java
│       └── impl/
│           └── RefundServiceImpl.java
├── src/main/resources/
│   └── mapper/finance/
│       └── RefundMapper.xml
├── REFUND_README.md
├── REFUND_IMPLEMENTATION_SUMMARY.md
└── REFUND_API_TEST_GUIDE.md

edu-admin/
└── src/main/resources/db/migration/
    ├── V1.0.3__finance_marketing_tables.sql
    └── V1.0.17__update_refund_table.sql
```

### B. 依赖清单
```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
</dependency>

<!-- Knife4j -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-spring-boot-starter</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- Hutool -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
</dependency>
```

---

**报告版本**: 1.0
**创建日期**: 2026-01-31
**创建人**: Claude Code Agent
**审核状态**: 待审核
