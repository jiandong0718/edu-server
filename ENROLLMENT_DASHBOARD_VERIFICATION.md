# 招生数据看板验证清单

## 后端验证（任务 24.2）

### 1. 文件创建检查

- [x] Controller: `EnrollmentDashboardController.java`
- [x] VO: `EnrollmentDashboardVO.java`
- [x] Service接口: `EnrollmentDashboardService.java`
- [x] Service实现: `EnrollmentDashboardServiceImpl.java`
- [x] Mapper接口: `EnrollmentDashboardMapper.java`
- [x] Mapper XML: `EnrollmentDashboardMapper.xml`

### 2. API接口验证

#### 2.1 获取招生数据概览
```bash
# 测试本月数据
curl -X GET "http://localhost:8080/dashboard/enrollment/overview?timeRange=month"

# 测试今日数据
curl -X GET "http://localhost:8080/dashboard/enrollment/overview?timeRange=today"

# 测试本周数据
curl -X GET "http://localhost:8080/dashboard/enrollment/overview?timeRange=week"

# 测试自定义时间范围
curl -X GET "http://localhost:8080/dashboard/enrollment/overview?timeRange=custom&startDate=2026-01-01&endDate=2026-01-31"

# 测试指定校区
curl -X GET "http://localhost:8080/dashboard/enrollment/overview?timeRange=month&campusId=1"
```

**验证点**:
- [ ] 返回状态码200
- [ ] 包含leadStats（线索统计）
- [ ] 包含trialStats（试听统计）
- [ ] 包含conversionStats（转化率统计）
- [ ] 包含dealStats（成交统计）
- [ ] 转化率计算正确（保留1位小数）
- [ ] 空数据返回0而不是null

#### 2.2 获取招生趋势
```bash
# 测试最近30天
curl -X GET "http://localhost:8080/dashboard/enrollment/trend?days=30"

# 测试最近7天
curl -X GET "http://localhost:8080/dashboard/enrollment/trend?days=7"

# 测试指定校区
curl -X GET "http://localhost:8080/dashboard/enrollment/trend?days=30&campusId=1"
```

**验证点**:
- [ ] 返回状态码200
- [ ] 包含leadTrend（线索趋势）
- [ ] 包含trialTrend（试听趋势）
- [ ] 包含dealTrend（成交趋势）
- [ ] 日期格式正确（YYYY-MM-DD）
- [ ] 数据按日期排序
- [ ] 缺失日期的数据为0

#### 2.3 获取转化漏斗
```bash
# 测试本月漏斗
curl -X GET "http://localhost:8080/dashboard/enrollment/funnel?timeRange=month"

# 测试自定义时间范围
curl -X GET "http://localhost:8080/dashboard/enrollment/funnel?timeRange=custom&startDate=2026-01-01&endDate=2026-01-31"
```

**验证点**:
- [ ] 返回状态码200
- [ ] 包含5个阶段（新线索、跟进中、已预约、已试听、已成交）
- [ ] 每个阶段包含name、count、conversionRate
- [ ] 转化率计算正确
- [ ] 第一阶段转化率为100%
- [ ] 后续阶段转化率相对于上一阶段

#### 2.4 获取线索来源分布
```bash
# 测试本月来源分布
curl -X GET "http://localhost:8080/dashboard/enrollment/source?timeRange=month"

# 测试指定校区
curl -X GET "http://localhost:8080/dashboard/enrollment/source?timeRange=month&campusId=1"
```

**验证点**:
- [ ] 返回状态码200
- [ ] 包含sources数组
- [ ] 每个来源包含source、count、percentage
- [ ] 百分比总和为100%（允许误差±0.1%）
- [ ] 按数量降序排列
- [ ] 未知来源显示为"未知"

#### 2.5 获取顾问排行榜
```bash
# 测试TOP 10
curl -X GET "http://localhost:8080/dashboard/enrollment/advisor-ranking?timeRange=month&limit=10"

# 测试TOP 5
curl -X GET "http://localhost:8080/dashboard/enrollment/advisor-ranking?timeRange=month&limit=5"

# 测试指定校区
curl -X GET "http://localhost:8080/dashboard/enrollment/advisor-ranking?timeRange=month&limit=10&campusId=1"
```

**验证点**:
- [ ] 返回状态码200
- [ ] 包含advisors数组
- [ ] 每个顾问包含advisorId、advisorName、dealCount、dealAmount、leadCount、conversionRate
- [ ] 按成交数降序排列
- [ ] 成交数相同时按成交金额降序
- [ ] 返回数量不超过limit
- [ ] 只包含有成交的顾问

### 3. 数据库查询验证

#### 3.1 检查SQL执行
```sql
-- 检查线索数据
SELECT COUNT(*) FROM mkt_lead WHERE deleted = 0;

-- 检查试听数据
SELECT COUNT(*) FROM mkt_trial_lesson WHERE deleted = 0;

-- 检查合同数据
SELECT COUNT(*) FROM fin_contract WHERE deleted = 0 AND status IN ('active', 'completed');

-- 检查用户数据
SELECT COUNT(*) FROM sys_user WHERE deleted = 0;
```

**验证点**:
- [ ] 所有表都有数据
- [ ] deleted字段正确设置
- [ ] status字段值正确
- [ ] 时间字段不为null

#### 3.2 检查索引
```sql
-- 检查mkt_lead表索引
SHOW INDEX FROM mkt_lead;

-- 检查mkt_trial_lesson表索引
SHOW INDEX FROM mkt_trial_lesson;

-- 检查fin_contract表索引
SHOW INDEX FROM fin_contract;
```

**验证点**:
- [ ] campus_id有索引
- [ ] create_time有索引
- [ ] status有索引
- [ ] deleted有索引

### 4. 缓存验证

```bash
# 启动Redis
redis-cli

# 查看缓存键
KEYS enrollment:*

# 查看特定缓存
GET "enrollment:overview:null:month:null:null"

# 查看缓存TTL
TTL "enrollment:overview:null:month:null:null"

# 清除缓存
DEL "enrollment:overview:null:month:null:null"
```

**验证点**:
- [ ] 第一次请求后缓存被创建
- [ ] 缓存TTL为300秒（5分钟）
- [ ] 第二次请求命中缓存（响应更快）
- [ ] 清除缓存后重新生成

### 5. API文档验证

访问：http://localhost:8080/doc.html

**验证点**:
- [ ] 能看到"招生数据看板"分组
- [ ] 5个接口都在列表中
- [ ] 每个接口有完整的参数说明
- [ ] 每个接口有响应示例
- [ ] 可以在线测试接口

### 6. 性能验证

```bash
# 使用ab工具进行压力测试
ab -n 1000 -c 10 "http://localhost:8080/dashboard/enrollment/overview?timeRange=month"
```

**验证点**:
- [ ] 平均响应时间 < 500ms（首次）
- [ ] 平均响应时间 < 50ms（缓存命中）
- [ ] 无错误请求
- [ ] 并发处理正常

---

## 前端验证（任务 24.7）

### 1. 文件创建检查

- [x] 页面组件: `src/pages/dashboard/enrollment/index.tsx`
- [x] API接口: `src/api/dashboard.ts`（已更新）

### 2. 页面访问验证

访问：http://localhost:5173/dashboard/enrollment

**验证点**:
- [ ] 页面能正常加载
- [ ] 无控制台错误
- [ ] 显示loading状态
- [ ] 数据加载完成后显示内容

### 3. UI组件验证

#### 3.1 页面标题
**验证点**:
- [ ] 显示"招生数据看板"标题
- [ ] 标题有渐变色效果
- [ ] 显示副标题"实时监控招生转化数据"

#### 3.2 时间范围选择器
**验证点**:
- [ ] 显示时间范围下拉框
- [ ] 包含4个选项：今日、本周、本月、自定义
- [ ] 默认选中"本月"
- [ ] 选择"自定义"时显示日期选择器
- [ ] 切换时间范围后自动刷新数据

#### 3.3 统计卡片（4个）
**验证点**:
- [ ] 显示4个统计卡片
- [ ] 卡片有深色背景和发光边框
- [ ] 总线索数卡片显示新增和待跟进
- [ ] 试听总数卡片显示已预约和已完成
- [ ] 试听转化率卡片显示百分比
- [ ] 成交金额卡片显示金额和成交数
- [ ] 数字有青色渐变效果
- [ ] 图标正确显示

#### 3.4 招生趋势图
**验证点**:
- [ ] 显示折线图
- [ ] 图表有渐变填充效果
- [ ] X轴显示日期（MM-DD格式）
- [ ] 显示图例（线索、试听、成交）
- [ ] 鼠标悬停无报错
- [ ] 数据为空时不显示图表

#### 3.5 线索来源饼图
**验证点**:
- [ ] 显示饼图
- [ ] 每个扇区有不同颜色
- [ ] 显示图例和数量
- [ ] 中心有空心圆
- [ ] 数据为空时不显示图表

#### 3.6 转化漏斗图
**验证点**:
- [ ] 显示5个阶段
- [ ] 每个阶段宽度按比例显示
- [ ] 显示数量和转化率
- [ ] 颜色渐变效果
- [ ] 阶段名称正确

#### 3.7 顾问排行榜
**验证点**:
- [ ] 显示TOP 10柱状图
- [ ] 显示排名序号
- [ ] 显示顾问姓名
- [ ] 显示成交数和成交金额
- [ ] 柱状图宽度按比例显示
- [ ] 有渐变色效果

### 4. 响应式布局验证

**验证点**:
- [ ] 桌面端（>1200px）：4列布局
- [ ] 平板端（768-1200px）：2列布局
- [ ] 移动端（<768px）：1列布局
- [ ] 图表在小屏幕上正常显示
- [ ] 无横向滚动条

### 5. 交互功能验证

#### 5.1 时间范围切换
**操作步骤**:
1. 选择"今日"
2. 观察数据变化
3. 选择"本周"
4. 观察数据变化
5. 选择"自定义"
6. 选择日期范围
7. 观察数据变化

**验证点**:
- [ ] 每次切换都触发数据刷新
- [ ] 显示loading状态
- [ ] 数据更新正确
- [ ] 无错误提示

#### 5.2 数据加载
**验证点**:
- [ ] 首次加载显示loading
- [ ] 加载失败显示错误提示
- [ ] 数据为空时显示空状态
- [ ] 加载成功后显示数据

### 6. 数据准确性验证

**验证点**:
- [ ] 统计卡片数据与API返回一致
- [ ] 图表数据与API返回一致
- [ ] 转化率计算正确
- [ ] 金额格式化正确（千分位）
- [ ] 百分比保留1位小数

### 7. 浏览器兼容性验证

**测试浏览器**:
- [ ] Chrome（最新版）
- [ ] Firefox（最新版）
- [ ] Safari（最新版）
- [ ] Edge（最新版）

**验证点**:
- [ ] 页面正常显示
- [ ] 图表正常渲染
- [ ] 交互功能正常
- [ ] 无控制台错误

### 8. 性能验证

**验证点**:
- [ ] 首次加载时间 < 3秒
- [ ] 数据刷新时间 < 1秒
- [ ] 图表渲染流畅
- [ ] 无内存泄漏
- [ ] 无卡顿现象

---

## 集成测试

### 1. 端到端测试

**测试场景1：查看本月招生数据**
1. 访问招生数据看板页面
2. 确认默认显示本月数据
3. 检查所有统计卡片有数据
4. 检查所有图表正常显示

**测试场景2：切换时间范围**
1. 选择"今日"
2. 确认数据更新
3. 选择"本周"
4. 确认数据更新
5. 选择"自定义"
6. 选择日期范围
7. 确认数据更新

**测试场景3：查看顾问排行**
1. 滚动到顾问排行榜
2. 确认显示TOP 10
3. 确认排序正确
4. 确认数据完整

### 2. 数据一致性测试

**验证点**:
- [ ] 前端显示的总线索数 = 后端返回的total
- [ ] 前端显示的转化率 = 后端计算的转化率
- [ ] 前端图表数据点数 = 后端返回的数据点数
- [ ] 前端来源分布总和 = 100%

### 3. 边界情况测试

**测试场景**:
- [ ] 数据库无数据时的显示
- [ ] 只有1条数据时的显示
- [ ] 时间范围内无数据时的显示
- [ ] 所有转化率为0时的显示
- [ ] 顾问数量少于10时的显示

---

## 问题记录

### 发现的问题

| 序号 | 问题描述 | 严重程度 | 状态 | 备注 |
|------|----------|----------|------|------|
| 1 | | | | |
| 2 | | | | |
| 3 | | | | |

### 待优化项

| 序号 | 优化建议 | 优先级 | 状态 | 备注 |
|------|----------|--------|------|------|
| 1 | | | | |
| 2 | | | | |
| 3 | | | | |

---

## 验证结论

### 后端验证结果
- [ ] 所有API接口正常
- [ ] 数据查询准确
- [ ] 缓存功能正常
- [ ] API文档完整
- [ ] 性能满足要求

### 前端验证结果
- [ ] 页面正常显示
- [ ] 所有组件正常
- [ ] 交互功能正常
- [ ] 响应式布局正常
- [ ] 浏览器兼容性良好

### 整体评估
- [ ] 功能完整
- [ ] 质量合格
- [ ] 可以上线

---

## 签字确认

| 角色 | 姓名 | 日期 | 签名 |
|------|------|------|------|
| 开发人员 | | | |
| 测试人员 | | | |
| 产品经理 | | | |
| 技术负责人 | | | |

---

## 附录

### 测试数据准备

```sql
-- 插入测试线索数据
INSERT INTO mkt_lead (name, phone, source, status, advisor_id, campus_id, create_time, deleted)
VALUES
('测试线索1', '13800138001', '线上广告', 'following', 1, 1, NOW(), 0),
('测试线索2', '13800138002', '转介绍', 'converted', 1, 1, NOW(), 0),
('测试线索3', '13800138003', '地推', 'following', 2, 1, NOW(), 0);

-- 插入测试试听数据
INSERT INTO mkt_trial_lesson (lead_id, student_id, course_id, trial_date, status, campus_id, create_time, deleted)
VALUES
(1, 1, 1, NOW(), 'scheduled', 1, NOW(), 0),
(2, 2, 1, NOW(), 'completed', 1, NOW(), 0),
(3, 3, 1, NOW(), 'converted', 1, NOW(), 0);

-- 插入测试合同数据
INSERT INTO fin_contract (student_id, total_amount, paid_amount, status, sign_date, campus_id, create_time, deleted)
VALUES
(1, 10000.00, 10000.00, 'active', NOW(), 1, NOW(), 0),
(2, 15000.00, 15000.00, 'active', NOW(), 1, NOW(), 0);
```

### 清理测试数据

```sql
-- 清理测试数据
DELETE FROM mkt_lead WHERE name LIKE '测试线索%';
DELETE FROM mkt_trial_lesson WHERE lead_id IN (SELECT id FROM mkt_lead WHERE name LIKE '测试线索%');
DELETE FROM fin_contract WHERE student_id IN (SELECT id FROM stu_student WHERE name LIKE '测试学员%');
```
