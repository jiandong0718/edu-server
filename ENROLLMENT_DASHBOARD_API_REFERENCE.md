# 招生数据看板 API 快速参考

## API 端点

### 1. 获取招生数据概览
```
GET /dashboard/enrollment/overview
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 | 默认值 |
|------|------|------|------|--------|
| campusId | Long | 否 | 校区ID | null（查询所有校区） |
| timeRange | String | 否 | 时间范围 | month |
| startDate | String | 否 | 开始日期（YYYY-MM-DD） | - |
| endDate | String | 否 | 结束日期（YYYY-MM-DD） | - |

**timeRange 可选值**:
- `today` - 今日
- `week` - 本周（最近7天）
- `month` - 本月（最近30天）
- `custom` - 自定义（需提供startDate和endDate）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "leadStats": {
      "total": 150,
      "newLeads": 25,
      "pending": 45,
      "converted": 30
    },
    "trialStats": {
      "total": 80,
      "scheduled": 60,
      "completed": 50,
      "conversionRate": 62.5
    },
    "conversionStats": {
      "trialConversionRate": 62.5,
      "dealConversionRate": 20.0,
      "overallConversionRate": 20.0
    },
    "dealStats": {
      "count": 5,
      "amount": 50000.00
    }
  }
}
```

---

### 2. 获取招生趋势
```
GET /dashboard/enrollment/trend
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 | 默认值 |
|------|------|------|------|--------|
| campusId | Long | 否 | 校区ID | null |
| days | Integer | 否 | 天数 | 30 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "leadTrend": [
      {"date": "2026-01-01", "count": 5},
      {"date": "2026-01-02", "count": 8}
    ],
    "trialTrend": [
      {"date": "2026-01-01", "count": 3},
      {"date": "2026-01-02", "count": 4}
    ],
    "dealTrend": [
      {"date": "2026-01-01", "count": 1},
      {"date": "2026-01-02", "count": 2}
    ]
  }
}
```

---

### 3. 获取转化漏斗
```
GET /dashboard/enrollment/funnel
```

**请求参数**: 同"获取招生数据概览"

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "stages": [
      {
        "name": "新线索",
        "count": 100,
        "conversionRate": 100.0
      },
      {
        "name": "跟进中",
        "count": 80,
        "conversionRate": 80.0
      },
      {
        "name": "已预约",
        "count": 60,
        "conversionRate": 75.0
      },
      {
        "name": "已试听",
        "count": 50,
        "conversionRate": 83.3
      },
      {
        "name": "已成交",
        "count": 30,
        "conversionRate": 60.0
      }
    ]
  }
}
```

---

### 4. 获取线索来源分布
```
GET /dashboard/enrollment/source
```

**请求参数**: 同"获取招生数据概览"

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sources": [
      {
        "source": "线上广告",
        "count": 60,
        "percentage": 40.0
      },
      {
        "source": "转介绍",
        "count": 50,
        "percentage": 33.3
      },
      {
        "source": "地推",
        "count": 40,
        "percentage": 26.7
      }
    ]
  }
}
```

---

### 5. 获取顾问排行榜
```
GET /dashboard/enrollment/advisor-ranking
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 | 默认值 |
|------|------|------|------|--------|
| campusId | Long | 否 | 校区ID | null |
| timeRange | String | 否 | 时间范围 | month |
| startDate | String | 否 | 开始日期 | - |
| endDate | String | 否 | 结束日期 | - |
| limit | Integer | 否 | 排行数量 | 10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "advisors": [
      {
        "advisorId": 1,
        "advisorName": "张三",
        "dealCount": 15,
        "dealAmount": 150000.00,
        "leadCount": 50,
        "conversionRate": 30.0
      },
      {
        "advisorId": 2,
        "advisorName": "李四",
        "dealCount": 12,
        "dealAmount": 120000.00,
        "leadCount": 45,
        "conversionRate": 26.7
      }
    ]
  }
}
```

---

## 前端使用示例

### 1. 导入API函数
```typescript
import {
  getEnrollmentOverview,
  getEnrollmentTrend,
  getEnrollmentFunnel,
  getEnrollmentSource,
  getEnrollmentAdvisorRanking,
} from '@/api/dashboard';
```

### 2. 调用API
```typescript
// 获取本月概览数据
const overview = await getEnrollmentOverview({ timeRange: 'month' });

// 获取最近30天趋势
const trend = await getEnrollmentTrend({ days: 30 });

// 获取本周转化漏斗
const funnel = await getEnrollmentFunnel({ timeRange: 'week' });

// 获取自定义时间范围的来源分布
const source = await getEnrollmentSource({
  timeRange: 'custom',
  startDate: '2026-01-01',
  endDate: '2026-01-31'
});

// 获取TOP 10顾问排行
const ranking = await getEnrollmentAdvisorRanking({
  timeRange: 'month',
  limit: 10
});
```

### 3. 并行请求
```typescript
const [overview, trend, funnel, source, ranking] = await Promise.all([
  getEnrollmentOverview({ timeRange: 'month' }),
  getEnrollmentTrend({ days: 30 }),
  getEnrollmentFunnel({ timeRange: 'month' }),
  getEnrollmentSource({ timeRange: 'month' }),
  getEnrollmentAdvisorRanking({ timeRange: 'month', limit: 10 }),
]);
```

---

## 错误处理

所有API都使用统一的错误响应格式：

```json
{
  "code": 500,
  "message": "错误信息",
  "data": null
}
```

**常见错误码**:
- `200` - 成功
- `400` - 请求参数错误
- `401` - 未授权
- `403` - 无权限
- `500` - 服务器内部错误

---

## 缓存说明

所有接口都使用Redis缓存，缓存时间为5分钟。

**缓存键格式**:
- 概览：`enrollment:overview:{campusId}:{timeRange}:{startDate}:{endDate}`
- 趋势：`enrollment:trend:{campusId}:{days}`
- 漏斗：`enrollment:funnel:{campusId}:{timeRange}:{startDate}:{endDate}`
- 来源：`enrollment:source:{campusId}:{timeRange}:{startDate}:{endDate}`
- 排行：`enrollment:advisor:{campusId}:{timeRange}:{startDate}:{endDate}:{limit}`

**清除缓存**:
```bash
# 清除所有招生看板缓存
redis-cli KEYS "enrollment:*" | xargs redis-cli DEL

# 清除特定类型缓存
redis-cli KEYS "enrollment:overview:*" | xargs redis-cli DEL
```

---

## 性能建议

1. **使用缓存**: 相同参数的请求会命中缓存，响应速度更快
2. **并行请求**: 使用Promise.all并行请求多个接口
3. **合理的时间范围**: 避免查询过长的时间范围
4. **分页查询**: 顾问排行榜使用limit参数控制返回数量

---

## 数据库表依赖

- `mkt_lead` - 线索表
- `mkt_trial_lesson` - 试听记录表
- `fin_contract` - 合同表
- `sys_user` - 用户表

确保这些表有适当的索引：
- `campus_id` - 校区筛选
- `create_time` - 时间范围查询
- `status` - 状态筛选
- `deleted` - 逻辑删除标记

---

## 测试命令

### 使用curl测试

```bash
# 获取本月概览
curl -X GET "http://localhost:8080/dashboard/enrollment/overview?timeRange=month"

# 获取最近30天趋势
curl -X GET "http://localhost:8080/dashboard/enrollment/trend?days=30"

# 获取转化漏斗
curl -X GET "http://localhost:8080/dashboard/enrollment/funnel?timeRange=month"

# 获取线索来源分布
curl -X GET "http://localhost:8080/dashboard/enrollment/source?timeRange=month"

# 获取顾问排行榜
curl -X GET "http://localhost:8080/dashboard/enrollment/advisor-ranking?timeRange=month&limit=10"
```

### 使用Postman测试

1. 导入Swagger文档：http://localhost:8080/v3/api-docs
2. 在"招生数据看板"分组下找到对应接口
3. 填写参数并发送请求

---

## 常见问题

### Q1: 为什么数据为0？
A: 检查数据库中是否有对应时间范围的数据，确认deleted字段为0。

### Q2: 如何查询特定校区的数据？
A: 在请求参数中添加campusId参数。

### Q3: 自定义时间范围如何使用？
A: 设置timeRange=custom，并提供startDate和endDate参数（格式：YYYY-MM-DD）。

### Q4: 转化率如何计算？
A:
- 试听转化率 = 试听转化数 / 已完成试听数 × 100%
- 成交转化率 = 成交数 / 新增线索数 × 100%
- 整体转化率 = 已转化线索数 / 新增线索数 × 100%

### Q5: 顾问排行榜如何排序？
A: 首先按成交数降序，成交数相同时按成交金额降序。

---

## 联系方式

如有问题，请联系开发团队或查看完整文档：
- 完整实现文档：`ENROLLMENT_DASHBOARD_IMPLEMENTATION.md`
- API文档：http://localhost:8080/doc.html
