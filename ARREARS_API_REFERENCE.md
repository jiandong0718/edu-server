# 欠费查询接口快速参考

## API 端点

### 1. 欠费分页查询
```
GET /finance/payment/arrears/page
```

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| campusId | Long | 否 | 校区ID | 1 |
| studentId | Long | 否 | 学员ID | 1 |
| studentName | String | 否 | 学员姓名（模糊） | 张三 |
| contractId | Long | 否 | 合同ID | 1 |
| contractNo | String | 否 | 合同编号（模糊） | HT2026 |
| minArrearsAmount | BigDecimal | 否 | 最小欠费金额 | 1000.00 |
| maxArrearsAmount | BigDecimal | 否 | 最大欠费金额 | 10000.00 |
| minArrearsDays | Integer | 否 | 最小欠费天数 | 7 |
| maxArrearsDays | Integer | 否 | 最大欠费天数 | 30 |
| pageNum | Integer | 否 | 页码 | 1 |
| pageSize | Integer | 否 | 每页大小 | 10 |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/finance/payment/arrears/page?campusId=1&minArrearsAmount=1000&pageNum=1&pageSize=10"
```

**响应字段**:
- contractId: 合同ID
- contractNo: 合同编号
- studentId: 学员ID
- studentName: 学员姓名
- studentPhone: 学员手机号
- campusId: 校区ID
- campusName: 校区名称
- contractAmount: 合同金额
- paidAmount: 实付金额（应收）
- receivedAmount: 已收金额
- arrearsAmount: 欠费金额
- signDate: 签约日期
- effectiveDate: 生效日期
- arrearsDays: 欠费天数
- contractStatus: 合同状态
- contractStatusDesc: 合同状态描述
- salesId: 销售顾问ID
- salesName: 销售顾问姓名
- lastPaymentTime: 最后收款时间
- createTime: 创建时间

---

### 2. 欠费统计
```
GET /finance/payment/arrears/statistics
```

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| campusId | Long | 否 | 校区ID（按校区统计） | 1 |
| studentId | Long | 否 | 学员ID（查询特定学员） | 1 |
| contractId | Long | 否 | 合同ID（查询特定合同） | 1 |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/finance/payment/arrears/statistics?campusId=1"
```

**响应字段**:
- totalArrearsAmount: 总欠费金额
- arrearsStudentCount: 欠费人数
- arrearsContractCount: 欠费合同数
- avgArrearsAmount: 平均欠费金额
- maxArrearsAmount: 最大欠费金额
- arrears7DaysCount: 欠费7天以内的合同数
- arrears7To15DaysCount: 欠费7-15天的合同数
- arrears15To30DaysCount: 欠费15-30天的合同数
- arrears30PlusDaysCount: 欠费30天以上的合同数

---

### 3. 欠费提醒
```
GET /finance/payment/arrears/remind
```

**查询参数**:
| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| minDays | Integer | 否 | 最小欠费天数阈值 | 7 |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/finance/payment/arrears/remind?minDays=15"
```

**响应字段**:
- contractId: 合同ID
- contractNo: 合同编号
- studentId: 学员ID
- studentName: 学员姓名
- studentPhone: 学员手机号
- campusId: 校区ID
- campusName: 校区名称
- arrearsAmount: 欠费金额
- arrearsDays: 欠费天数
- remindLevel: 提醒级别（normal/warning/urgent）
- remindLevelDesc: 提醒级别描述（正常/警告/紧急）
- salesId: 销售顾问ID
- salesName: 销售顾问姓名
- signDate: 签约日期
- effectiveDate: 生效日期

**提醒级别说明**:
- normal (正常): 欠费 7-14 天
- warning (警告): 欠费 15-29 天
- urgent (紧急): 欠费 30 天以上

---

## 使用场景

### 场景1: 查询某校区所有欠费超过1000元的记录
```bash
GET /finance/payment/arrears/page?campusId=1&minArrearsAmount=1000
```

### 场景2: 查询欠费超过15天的记录
```bash
GET /finance/payment/arrears/page?minArrearsDays=15
```

### 场景3: 查询某学员的欠费情况
```bash
GET /finance/payment/arrears/page?studentId=1
```

### 场景4: 获取全校欠费统计
```bash
GET /finance/payment/arrears/statistics
```

### 场景5: 获取某校区欠费统计
```bash
GET /finance/payment/arrears/statistics?campusId=1
```

### 场景6: 获取需要催款的记录（欠费超过7天）
```bash
GET /finance/payment/arrears/remind?minDays=7
```

### 场景7: 获取紧急催款记录（欠费超过30天）
```bash
GET /finance/payment/arrears/remind?minDays=30
```

---

## 业务规则

### 欠费判定条件
1. 合同状态为"已签署"或"已完成"
2. 实付金额 > 已收金额
3. 合同未删除

### 欠费金额计算
```
欠费金额 = 实付金额 - 已收金额
```

### 欠费天数计算
```
欠费天数 = 当前日期 - 合同生效日期
```

### 提醒级别判定
- **normal (正常)**: 欠费天数 < 15天
- **warning (警告)**: 15天 ≤ 欠费天数 < 30天
- **urgent (紧急)**: 欠费天数 ≥ 30天

---

## 数据库表关系

```
fin_contract (合同表)
├── student_id → stu_student (学员表)
├── campus_id → sys_campus (校区表)
└── sales_id → sys_user (用户表 - 销售顾问)

fin_payment (收款表)
└── contract_id → fin_contract (合同表)
```

---

## 访问 API 文档

启动应用后，访问 Knife4j 文档：
```
http://localhost:8080/doc.html
```

在文档中找到"收款管理"模块，可以看到：
- 分页查询欠费记录
- 欠费统计
- 获取需要提醒的欠费记录

可以直接在文档中测试 API。
