# 数据预警API快速参考

## 基础信息
- **Base URL**: `/system/dashboard/warning`
- **认证**: 需要JWT Token
- **返回格式**: JSON

## API列表

### 1. 获取预警列表
```
GET /system/dashboard/warning/list
```

**查询参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页大小 |
| campusId | Long | 否 | - | 校区ID，不传则查询所有校区 |
| warningType | String | 否 | - | 预警类型 |
| warningLevel | String | 否 | - | 预警级别：normal/warning/urgent |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "warningId": "course_hour_low_1001_2001",
        "warningType": "course_hour_low",
        "warningName": "课时不足预警",
        "warningLevel": "warning",
        "description": "学员 张三 的 数学课程 剩余课时仅 3.0 小时，低于阈值 5 小时",
        "businessId": 1001,
        "businessName": "张三",
        "campusId": 1,
        "campusName": "总部校区",
        "currentValue": "3.0小时",
        "thresholdValue": "5小时",
        "warningTime": "2026-01-31T15:30:00"
      }
    ],
    "total": 25,
    "size": 10,
    "current": 1,
    "pages": 3
  }
}
```

---

### 2. 获取预警汇总
```
GET /system/dashboard/warning/summary
```

**查询参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| campusId | Long | 否 | - | 校区ID，不传则查询所有校区 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 25,
    "urgentCount": 8,
    "warningCount": 12,
    "normalCount": 5,
    "businessWarningCount": 15,
    "operationWarningCount": 7,
    "financeWarningCount": 3,
    "typeDistribution": [
      {
        "warningType": "course_hour_low",
        "warningName": "课时不足预警",
        "count": 8,
        "warningLevel": "warning"
      },
      {
        "warningType": "student_loss",
        "warningName": "学员流失预警",
        "count": 5,
        "warningLevel": "urgent"
      }
    ]
  }
}
```

---

### 3. 配置预警规则
```
POST /system/dashboard/warning/config
```

**请求体**:
```json
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

**字段说明**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| warningType | String | 是 | 预警类型 |
| warningName | String | 是 | 预警名称 |
| warningLevel | String | 是 | 预警级别：normal/warning/urgent |
| thresholdConfig | String | 是 | 阈值配置（JSON格式） |
| enabled | Integer | 是 | 是否启用：0-禁用，1-启用 |
| campusId | Long | 否 | 校区ID，为空表示全局配置 |
| remark | String | 否 | 备注 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 12345
}
```

---

### 4. 更新预警规则
```
PUT /system/dashboard/warning/config/{id}
```

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 配置ID |

**请求体**: 同"配置预警规则"

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

### 5. 获取预警配置列表
```
GET /system/dashboard/warning/config/list
```

**查询参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| campusId | Long | 否 | - | 校区ID，不传则查询全局配置 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "warningType": "course_hour_low",
      "warningName": "课时不足预警",
      "warningLevel": "warning",
      "thresholdConfig": "{\"courseHourThreshold\":5}",
      "enabled": 1,
      "campusId": null,
      "remark": "当学员剩余课时低于5小时时触发预警",
      "createTime": "2026-01-31T10:00:00",
      "updateTime": "2026-01-31T10:00:00"
    }
  ]
}
```

---

### 6. 获取预警配置详情
```
GET /system/dashboard/warning/config/{id}
```

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 配置ID |

**响应示例**: 同"获取预警配置列表"中的单个对象

---

### 7. 删除预警配置
```
DELETE /system/dashboard/warning/config/{id}
```

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 配置ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## 预警类型说明

### 业务预警
| 类型 | 名称 | 默认级别 | 阈值配置示例 |
|------|------|----------|--------------|
| course_hour_low | 课时不足预警 | warning | `{"courseHourThreshold":5}` |
| course_hour_expire | 课时即将到期预警 | warning | `{"daysThreshold":30}` |
| overdue | 欠费预警 | urgent | `{"daysThreshold":7}` |
| contract_expire | 合同即将到期预警 | warning | `{"daysThreshold":30}` |
| student_loss | 学员流失预警 | urgent | `{"daysThreshold":30}` |

### 运营预警
| 类型 | 名称 | 默认级别 | 阈值配置示例 |
|------|------|----------|--------------|
| class_full | 班级满员预警 | normal | `{}` |
| schedule_conflict | 教师排课冲突预警 | urgent | `{}` |
| classroom_conflict | 教室使用冲突预警 | urgent | `{}` |
| trial_conversion_low | 试听转化率低预警 | warning | `{"rateThreshold":0.3}` |

### 财务预警
| 类型 | 名称 | 默认级别 | 阈值配置示例 |
|------|------|----------|--------------|
| income_abnormal | 收入异常预警 | warning | `{"rateThreshold":0.8}` |
| refund_rate_high | 退费率高预警 | urgent | `{"rateThreshold":0.1}` |

---

## 预警级别

| 级别 | 值 | 颜色 | 说明 |
|------|-----|------|------|
| 正常 | normal | 绿色 | 一般性提醒 |
| 警告 | warning | 橙色 | 需要关注 |
| 紧急 | urgent | 红色 | 需要立即处理 |

---

## 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 使用示例

### cURL示例

```bash
# 1. 获取预警列表
curl -X GET "http://localhost:8080/system/dashboard/warning/list?pageNum=1&pageSize=10&warningLevel=urgent" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 2. 获取预警汇总
curl -X GET "http://localhost:8080/system/dashboard/warning/summary?campusId=1" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 3. 配置预警规则
curl -X POST "http://localhost:8080/system/dashboard/warning/config" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "warningType": "course_hour_low",
    "warningName": "课时不足预警",
    "warningLevel": "warning",
    "thresholdConfig": "{\"courseHourThreshold\":5}",
    "enabled": 1,
    "campusId": 1,
    "remark": "当学员剩余课时低于5小时时触发预警"
  }'

# 4. 更新预警规则
curl -X PUT "http://localhost:8080/system/dashboard/warning/config/1" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "warningType": "course_hour_low",
    "warningName": "课时不足预警",
    "warningLevel": "urgent",
    "thresholdConfig": "{\"courseHourThreshold\":3}",
    "enabled": 1,
    "campusId": 1,
    "remark": "当学员剩余课时低于3小时时触发预警"
  }'
```

### JavaScript示例

```javascript
// 使用axios
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Authorization': 'Bearer YOUR_TOKEN'
  }
});

// 1. 获取预警列表
const getWarningList = async (params) => {
  const response = await api.get('/system/dashboard/warning/list', { params });
  return response.data;
};

// 2. 获取预警汇总
const getWarningSummary = async (campusId) => {
  const response = await api.get('/system/dashboard/warning/summary', {
    params: { campusId }
  });
  return response.data;
};

// 3. 配置预警规则
const configWarning = async (data) => {
  const response = await api.post('/system/dashboard/warning/config', data);
  return response.data;
};

// 4. 更新预警规则
const updateWarningConfig = async (id, data) => {
  const response = await api.put(`/system/dashboard/warning/config/${id}`, data);
  return response.data;
};
```

---

## 注意事项

1. **阈值配置格式**：必须是有效的JSON字符串
2. **校区配置优先级**：校区级配置优先于全局配置
3. **分页查询**：预警数据是实时计算的，大数据量时可能较慢
4. **预警级别**：建议根据业务重要性合理设置预警级别
5. **启用状态**：禁用的预警配置不会触发预警检测
