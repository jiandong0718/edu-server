# 数据看板 API 快速参考

## 基础信息

**Base URL**: `http://localhost:8080`

**认证方式**: Bearer Token (JWT)

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

## API 列表

### 1. 获取完整数据看板

获取包含学员、财务、教学、营销的完整数据看板统计。

**接口**: `GET /system/dashboard`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| campusId | Long | 否 | 校区ID，不传则查询所有校区 |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/system/dashboard?campusId=1" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "studentStats": {
      "totalCount": 200,
      "enrolledCount": 150,
      "potentialCount": 30,
      "newCountThisMonth": 15,
      "statusDistribution": [...]
    },
    "financeStats": {
      "incomeToday": 5000.00,
      "incomeThisWeek": 35000.00,
      "incomeThisMonth": 150000.00,
      "incomeThisYear": 1800000.00,
      "refundThisMonth": 5000.00,
      "pendingAmount": 80000.00,
      "overdueAmount": 15000.00,
      "contractCount": 120,
      "paymentMethodDistribution": [...],
      "incomeTrend": [...]
    },
    "teachingStats": {
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
    },
    "marketingStats": {
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
      "sourceDistribution": [...],
      "leadTrend": [...],
      "conversionTrend": [...]
    }
  }
}
```

---

### 2. 获取学员统计

获取学员数量、状态分布等统计信息。

**接口**: `GET /system/dashboard/student`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| campusId | Long | 否 | 校区ID，不传则查询所有校区 |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/system/dashboard/student?campusId=1" \
  -H "Authorization: Bearer {token}"
```

**响应字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| totalCount | Integer | 学员总数 |
| enrolledCount | Integer | 在读学员数 |
| potentialCount | Integer | 潜在学员数 |
| newCountThisMonth | Integer | 本月新增学员数 |
| statusDistribution | Array | 学员状态分布 [{"name": "enrolled", "value": 150}] |

---

### 3. 获取财务统计

获取收入、退费、欠费、收款方式分布、收入趋势等统计信息。

**接口**: `GET /system/dashboard/finance`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| campusId | Long | 否 | 校区ID，不传则查询所有校区 |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/system/dashboard/finance?campusId=1" \
  -H "Authorization: Bearer {token}"
```

**响应字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| incomeToday | BigDecimal | 今日收入 |
| incomeThisWeek | BigDecimal | 本周收入 |
| incomeThisMonth | BigDecimal | 本月收入 |
| incomeThisYear | BigDecimal | 本年收入 |
| refundThisMonth | BigDecimal | 本月退费 |
| pendingAmount | BigDecimal | 待收款金额（欠费） |
| overdueAmount | BigDecimal | 逾期欠费金额 |
| contractCount | Integer | 合同总数 |
| paymentMethodDistribution | Array | 收款方式分布 [{"name": "wechat", "count": 50, "amount": 80000.00}] |
| incomeTrend | Array | 近30天收入趋势 [{"date": "2026-01-01", "amount": 5000.00}] |

**收款方式枚举**:
- `wechat`: 微信
- `alipay`: 支付宝
- `unionpay`: 银联
- `cash`: 现金
- `pos`: POS机

---

### 4. 获取教学统计

获取课节、班级、教师、学员、出勤率等统计信息。

**接口**: `GET /system/dashboard/teaching`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| campusId | Long | 否 | 校区ID，不传则查询所有校区 |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/system/dashboard/teaching?campusId=1" \
  -H "Authorization: Bearer {token}"
```

**响应字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| todayScheduleCount | Integer | 今日课节数 |
| weekScheduleCount | Integer | 本周课节数 |
| classCount | Integer | 班级总数 |
| ongoingClassCount | Integer | 进行中班级数 |
| completedClassCount | Integer | 已结业班级数 |
| teacherCount | Integer | 教师总数 |
| activeTeacherCount | Integer | 在职教师数 |
| onLeaveTeacherCount | Integer | 休假教师数 |
| courseCount | Integer | 课程总数 |
| studentCount | Integer | 学员总数 |
| enrolledStudentCount | Integer | 在读学员数 |
| trialStudentCount | Integer | 试听学员数 |
| potentialStudentCount | Integer | 潜在学员数 |
| attendanceRate | Double | 本周出勤率（百分比） |

---

### 5. 获取营销统计

获取线索、试听、转化率、来源分布、趋势等统计信息。

**接口**: `GET /system/dashboard/marketing`

**参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| campusId | Long | 否 | 校区ID，不传则查询所有校区 |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/system/dashboard/marketing?campusId=1" \
  -H "Authorization: Bearer {token}"
```

**响应字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| leadCount | Integer | 线索总数 |
| pendingLeadCount | Integer | 待跟进线索数 |
| convertedLeadCount | Integer | 已转化线索数 |
| newLeadThisMonth | Integer | 本月新增线索 |
| convertedThisMonth | Integer | 本月转化数 |
| conversionRate | Double | 转化率（百分比） |
| trialCount | Integer | 试听总数 |
| trialThisMonth | Integer | 本月试听数 |
| trialConvertedCount | Integer | 试听转化数 |
| trialConversionRate | Double | 试听转化率（百分比） |
| sourceDistribution | Array | 线索来源分布 [{"name": "online_ad", "value": 60}] |
| leadTrend | Array | 线索趋势（近30天） [{"date": "2026-01-01", "count": 5}] |
| conversionTrend | Array | 转化趋势（近30天） [{"date": "2026-01-01", "count": 2}] |

**线索来源枚举**:
- `offline`: 地推
- `referral`: 转介绍
- `online_ad`: 线上广告
- `walk_in`: 自然到访
- `phone`: 电话咨询

**线索状态枚举**:
- `new`: 新线索
- `following`: 跟进中
- `appointed`: 已预约
- `trialed`: 已试听
- `converted`: 已成交
- `lost`: 已流失

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 401 | 未授权，Token 无效或过期 |
| 403 | 无权限访问 |
| 500 | 服务器内部错误 |

## 缓存说明

所有数据看板接口都启用了 Redis 缓存：
- **缓存时间**: 5 分钟
- **缓存键格式**: `dashboard:{module}:{campusId}`
- **缓存策略**: 空值不缓存

如需获取实时数据，可以：
1. 等待缓存过期（5分钟）
2. 手动清除 Redis 缓存

**清除缓存命令**:
```bash
# 清除指定校区的财务统计缓存
redis-cli DEL "dashboard:finance:1"

# 清除所有数据看板缓存
redis-cli KEYS "dashboard:*" | xargs redis-cli DEL
```

## 性能说明

- **响应时间**: 首次请求 < 500ms，缓存命中 < 50ms
- **并发支持**: 支持高并发访问
- **数据实时性**: 5 分钟延迟（缓存 TTL）

## 使用建议

1. **首页看板**: 使用 `/system/dashboard` 获取完整数据
2. **专项分析**: 使用单独的接口获取特定模块数据
3. **定时刷新**: 建议前端每 5 分钟自动刷新一次
4. **校区切换**: 通过 `campusId` 参数切换不同校区数据
5. **全局统计**: 不传 `campusId` 参数获取所有校区汇总数据

## 前端集成示例

### React + Axios

```javascript
import axios from 'axios';

// 获取完整数据看板
export const getDashboard = (campusId) => {
  return axios.get('/system/dashboard', {
    params: { campusId },
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
};

// 获取财务统计
export const getFinanceStats = (campusId) => {
  return axios.get('/system/dashboard/finance', {
    params: { campusId },
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
};

// 使用示例
const Dashboard = () => {
  const [data, setData] = useState(null);
  const [campusId, setCampusId] = useState(1);

  useEffect(() => {
    getDashboard(campusId).then(res => {
      setData(res.data.data);
    });
  }, [campusId]);

  return (
    <div>
      {/* 渲染数据看板 */}
    </div>
  );
};
```

## 测试工具

推荐使用以下工具测试 API：
- **Postman**: 导入 OpenAPI 文档进行测试
- **Knife4j**: 访问 http://localhost:8080/doc.html 在线测试
- **curl**: 使用命令行快速测试

## 联系支持

如有问题，请查看：
- API 文档: http://localhost:8080/doc.html
- 实现文档: DASHBOARD_IMPLEMENTATION.md
