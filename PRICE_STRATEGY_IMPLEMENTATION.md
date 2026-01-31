# 价格策略配置功能实现文档

## 概述

本文档描述了教育机构学生管理系统中价格策略配置功能（Task 13.5）的完整实现。该功能支持灵活的定价规则，包括阶梯价格、会员价、促销价等多种策略类型。

## 数据库设计

### 1. 价格策略表 (tch_price_strategy)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| strategy_name | VARCHAR(100) | 策略名称 |
| strategy_code | VARCHAR(50) | 策略编码（唯一） |
| description | TEXT | 策略描述 |
| course_id | BIGINT | 关联课程ID（可为空表示通用策略） |
| strategy_type | VARCHAR(20) | 策略类型：TIERED-阶梯价格, MEMBER-会员价, PROMOTION-促销价, CUSTOM-自定义 |
| priority | INT | 优先级（数字越大优先级越高） |
| start_date | DATE | 有效期开始日期 |
| end_date | DATE | 有效期结束日期 |
| status | VARCHAR(20) | 状态：ACTIVE-启用, INACTIVE-禁用 |
| campus_id | BIGINT | 校区ID（null表示全部校区可用） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | BIGINT | 创建人ID |
| update_by | BIGINT | 更新人ID |
| deleted | TINYINT | 删除标志 |

### 2. 价格策略规则表 (tch_price_strategy_rule)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| strategy_id | BIGINT | 策略ID |
| condition_type | VARCHAR(30) | 条件类型：CLASS_HOURS-课时数, AMOUNT-金额, MEMBER_LEVEL-会员等级 |
| condition_value | TEXT | 条件值（JSON格式） |
| discount_type | VARCHAR(20) | 折扣类型：PERCENTAGE-百分比, FIXED-固定金额, PRICE-直接定价 |
| discount_value | DECIMAL(10,2) | 折扣值 |
| create_time | DATETIME | 创建时间 |
| deleted | TINYINT | 删除标志 |

## 文件结构

### 数据库迁移
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-admin/src/main/resources/db/migration/V1.0.25__add_price_strategy_tables.sql`

### 实体类 (Entity)
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/domain/entity/PriceStrategy.java`
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/domain/entity/PriceStrategyRule.java`

### 数据传输对象 (DTO)
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/domain/dto/PriceStrategyDTO.java`
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/domain/dto/PriceStrategyRuleDTO.java`
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/domain/dto/PriceStrategyQueryDTO.java`
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/domain/dto/PriceCalculateDTO.java`

### 视图对象 (VO)
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/domain/vo/PriceStrategyVO.java`
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/domain/vo/PriceStrategyRuleVO.java`
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/domain/vo/PriceCalculateVO.java`

### 数据访问层 (Mapper)
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/mapper/PriceStrategyMapper.java`
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/mapper/PriceStrategyRuleMapper.java`

### 服务层 (Service)
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/service/PriceStrategyService.java`
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/service/impl/PriceStrategyServiceImpl.java`

### 控制器层 (Controller)
- `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/controller/PriceStrategyController.java`

## API 接口

### 1. 分页查询价格策略
```
GET /teaching/price-strategy/page
参数：
  - strategyName: 策略名称（模糊查询）
  - strategyCode: 策略编码
  - strategyType: 策略类型
  - courseId: 课程ID
  - status: 状态
  - campusId: 校区ID
  - pageNum: 页码（默认1）
  - pageSize: 每页大小（默认10）
```

### 2. 获取启用的策略列表
```
GET /teaching/price-strategy/list
参数：
  - courseId: 课程ID（可选）
  - campusId: 校区ID（可选）
```

### 3. 获取适用的策略列表
```
GET /teaching/price-strategy/applicable
参数：
  - courseId: 课程ID（可选）
  - campusId: 校区ID（可选）
说明：返回当前有效期内的启用策略
```

### 4. 获取策略详情
```
GET /teaching/price-strategy/{id}
```

### 5. 创建价格策略
```
POST /teaching/price-strategy
请求体：PriceStrategyDTO（包含规则列表）
```

### 6. 更新价格策略
```
PUT /teaching/price-strategy/{id}
请求体：PriceStrategyDTO（包含规则列表）
```

### 7. 删除价格策略
```
DELETE /teaching/price-strategy/{id}
```

### 8. 启用价格策略
```
PUT /teaching/price-strategy/{id}/enable
```

### 9. 禁用价格策略
```
PUT /teaching/price-strategy/{id}/disable
```

### 10. 更新策略状态
```
PUT /teaching/price-strategy/{id}/status
参数：
  - status: ACTIVE 或 INACTIVE
```

### 11. 计算价格
```
POST /teaching/price-strategy/calculate
请求体：PriceCalculateDTO
返回：PriceCalculateVO（包含原价、最终价格、折扣金额、应用的策略列表等）
```

## 价格策略示例

### 1. 阶梯价格策略
```json
{
  "strategyName": "阶梯价格策略",
  "strategyCode": "TIERED_001",
  "description": "根据购买课时数量提供不同折扣",
  "strategyType": "TIERED",
  "priority": 10,
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "status": "ACTIVE",
  "rules": [
    {
      "conditionType": "CLASS_HOURS",
      "conditionValue": "{\"min\": 10, \"max\": 20}",
      "discountType": "PERCENTAGE",
      "discountValue": 90.00
    },
    {
      "conditionType": "CLASS_HOURS",
      "conditionValue": "{\"min\": 21, \"max\": 50}",
      "discountType": "PERCENTAGE",
      "discountValue": 85.00
    },
    {
      "conditionType": "CLASS_HOURS",
      "conditionValue": "{\"min\": 51, \"max\": null}",
      "discountType": "PERCENTAGE",
      "discountValue": 80.00
    }
  ]
}
```

### 2. 会员价策略
```json
{
  "strategyName": "会员价策略",
  "strategyCode": "MEMBER_001",
  "description": "根据会员等级提供不同折扣",
  "strategyType": "MEMBER",
  "priority": 20,
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "status": "ACTIVE",
  "rules": [
    {
      "conditionType": "MEMBER_LEVEL",
      "conditionValue": "{\"level\": \"NORMAL\"}",
      "discountType": "PERCENTAGE",
      "discountValue": 95.00
    },
    {
      "conditionType": "MEMBER_LEVEL",
      "conditionValue": "{\"level\": \"SILVER\"}",
      "discountType": "PERCENTAGE",
      "discountValue": 90.00
    },
    {
      "conditionType": "MEMBER_LEVEL",
      "conditionValue": "{\"level\": \"GOLD\"}",
      "discountType": "PERCENTAGE",
      "discountValue": 85.00
    },
    {
      "conditionType": "MEMBER_LEVEL",
      "conditionValue": "{\"level\": \"DIAMOND\"}",
      "discountType": "PERCENTAGE",
      "discountValue": 80.00
    }
  ]
}
```

### 3. 促销价策略
```json
{
  "strategyName": "春季促销",
  "strategyCode": "PROMOTION_001",
  "description": "春季限时促销活动",
  "strategyType": "PROMOTION",
  "priority": 30,
  "startDate": "2026-03-01",
  "endDate": "2026-05-31",
  "status": "ACTIVE",
  "rules": [
    {
      "conditionType": "AMOUNT",
      "conditionValue": "{\"min\": 0, \"max\": null}",
      "discountType": "PERCENTAGE",
      "discountValue": 70.00
    }
  ]
}
```

## 价格计算逻辑

### 计算流程
1. 获取适用的价格策略（按优先级从高到低排序）
2. 对每个策略，遍历其规则，查找匹配的规则
3. 应用匹配的规则进行折扣计算
4. 每个策略只应用一个规则（第一个匹配的规则）
5. 多个策略可以叠加应用
6. 确保最终价格不为负数
7. 保留2位小数

### 折扣类型说明
- **PERCENTAGE（百分比）**: 折扣值表示折后价格的百分比（如90表示9折）
- **FIXED（固定金额）**: 折扣值表示减免的金额
- **PRICE（直接定价）**: 折扣值表示最终价格

### 条件类型说明
- **CLASS_HOURS（课时数）**: 根据购买的课时数量匹配
- **AMOUNT（金额）**: 根据购买金额匹配
- **MEMBER_LEVEL（会员等级）**: 根据会员等级匹配（NORMAL/SILVER/GOLD/DIAMOND）

## 业务规则

1. **策略编码唯一性**: 每个策略的编码必须唯一
2. **优先级管理**: 数字越大优先级越高，高优先级策略先应用
3. **有效期控制**: 只有在有效期内的策略才会被应用
4. **状态控制**: 只有启用状态的策略才会参与计算
5. **规则匹配**: 每个策略只应用第一个匹配的规则
6. **策略叠加**: 多个策略可以叠加应用
7. **价格保护**: 最终价格不能为负数
8. **精度控制**: 价格计算结果保留2位小数

## 使用示例

### 创建阶梯价格策略
```bash
curl -X POST http://localhost:8080/teaching/price-strategy \
  -H "Content-Type: application/json" \
  -d '{
    "strategyName": "课时阶梯优惠",
    "strategyCode": "TIERED_002",
    "description": "购买课时越多，折扣越大",
    "strategyType": "TIERED",
    "priority": 10,
    "startDate": "2026-01-01",
    "endDate": "2026-12-31",
    "status": "ACTIVE",
    "rules": [
      {
        "conditionType": "CLASS_HOURS",
        "conditionValue": "{\"min\": 10, \"max\": 20}",
        "discountType": "PERCENTAGE",
        "discountValue": 90.00
      }
    ]
  }'
```

### 计算价格
```bash
curl -X POST http://localhost:8080/teaching/price-strategy/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": 1,
    "originalPrice": 1000.00,
    "classHours": 15,
    "memberLevel": "GOLD",
    "campusId": 1
  }'
```

响应示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "originalPrice": 1000.00,
    "finalPrice": 765.00,
    "discountAmount": 235.00,
    "discountRate": 23.50,
    "appliedStrategies": [
      {
        "strategyId": 1,
        "strategyName": "阶梯价格策略",
        "strategyType": "TIERED",
        "priority": 10,
        "ruleId": 1,
        "ruleDescription": "购买10-20课时，90折",
        "discountAmount": 100.00
      },
      {
        "strategyId": 2,
        "strategyName": "会员价策略",
        "strategyType": "MEMBER",
        "priority": 20,
        "ruleId": 6,
        "ruleDescription": "金卡会员，85折",
        "discountAmount": 135.00
      }
    ],
    "description": "原价：1000.00元，应用【阶梯价格策略】购买10-20课时，90折，优惠100.00元，应用【会员价策略】金卡会员，85折，优惠135.00元，最终价格：765.00元"
  }
}
```

## 测试建议

1. **单元测试**: 测试价格计算逻辑的各种场景
2. **集成测试**: 测试完整的API调用流程
3. **边界测试**: 测试边界条件（如0课时、负数价格等）
4. **并发测试**: 测试多个策略同时应用的情况
5. **性能测试**: 测试大量策略和规则的计算性能

## 注意事项

1. 价格策略与合同模块关联，在创建合同时可以应用价格策略
2. 策略变更不影响已创建的合同
3. 删除策略前应检查是否有关联的合同
4. 建议定期清理过期的策略
5. 价格计算结果应该在前端展示给用户确认
6. 支持策略的导入导出功能（可选扩展）
7. 支持策略变更历史记录（可选扩展）

## 后续扩展

1. **策略模板**: 提供常用策略模板，快速创建策略
2. **策略复制**: 支持复制现有策略
3. **策略预览**: 在应用前预览策略效果
4. **策略统计**: 统计各策略的使用情况和效果
5. **策略推荐**: 根据历史数据推荐最优策略
6. **A/B测试**: 支持策略的A/B测试
7. **策略审批**: 重要策略需要审批后才能启用
