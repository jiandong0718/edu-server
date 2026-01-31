# 课程包管理功能实现说明

## 功能概述

课程包管理功能允许教育机构将多个课程组合成套餐进行销售，支持灵活的定价策略和课时配置。

## 后端实现

### 1. 数据库表结构

#### 课程包表 (tch_course_package)
- `id`: 主键ID
- `name`: 课程包名称
- `package_code`: 课程包编码（唯一）
- `description`: 课程包描述
- `cover_image`: 封面图片URL
- `price`: 售价
- `original_price`: 原价
- `discount`: 折扣（如8.5表示8.5折）
- `valid_days`: 有效期（天数）
- `total_class_hours`: 总课时数
- `status`: 状态（0-下架，1-上架）
- `campus_id`: 校区ID
- `sort_order`: 排序
- `create_time`, `update_time`, `create_by`, `update_by`, `deleted`

#### 课程包明细表 (tch_course_package_item)
- `id`: 主键ID
- `package_id`: 课程包ID
- `course_id`: 课程ID
- `course_count`: 课程数量（课时数）
- `sort_order`: 排序
- `create_time`, `update_time`, `create_by`, `update_by`, `deleted`

### 2. 核心接口

#### CoursePackageController
- `GET /teaching/course-package/page` - 分页查询课程包列表
- `GET /teaching/course-package/list` - 获取在售课程包列表
- `GET /teaching/course-package/{id}` - 获取课程包详情
- `POST /teaching/course-package` - 创建课程包
- `PUT /teaching/course-package/{id}` - 更新课程包
- `DELETE /teaching/course-package/{id}` - 删除课程包
- `PUT /teaching/course-package/{id}/publish` - 上架课程包
- `PUT /teaching/course-package/{id}/unpublish` - 下架课程包
- `PUT /teaching/course-package/{id}/status` - 更新课程包状态

### 3. 业务逻辑

#### CoursePackageService
- 自动生成课程包编码（格式：PKG000001）
- 自动计算总课时数（所有课程课时之和）
- 自动计算折扣（售价/原价 * 10）
- 验证课程是否存在
- 级联删除课程包明细
- 支持校区筛选

### 4. 数据迁移

执行以下SQL文件：
1. `V1.0.11__add_course_package_and_price_strategy.sql` - 创建基础表
2. `V1.0.24__enhance_course_package_table.sql` - 增强表结构
3. `test_course_package_enhanced_data.sql` - 测试数据（可选）

## 前端实现

### 1. 页面路由

路径：`/teaching/course-package`

### 2. 主要功能

#### 列表页面
- 统计卡片：总数、上架数、下架数、总价值
- 搜索筛选：名称、状态、校区
- 表格展示：课程包信息、价格、包含课程、有效期、状态
- 操作按钮：查看、编辑、上架/下架、删除

#### 新增/编辑表单
- 基本信息：名称、编码、描述、封面图片
- 价格配置：原价、售价、自动计算折扣
- 课程选择：使用Transfer组件选择课程，配置每个课程的课时数
- 其他配置：有效期、状态、校区、排序

### 3. 组件结构

```
coursePackage/
├── index.tsx          # 主页面组件
└── components/        # 子组件（可选）
    ├── PackageForm.tsx    # 表单组件
    ├── CourseSelector.tsx # 课程选择器
    └── PackageCard.tsx    # 课程包卡片
```

### 4. API接口

文件：`src/api/coursePackage.ts`

主要方法：
- `getCoursePackageList()` - 获取列表
- `getCoursePackageDetail()` - 获取详情
- `createCoursePackage()` - 创建
- `updateCoursePackage()` - 更新
- `deleteCoursePackage()` - 删除
- `publishCoursePackage()` - 上架
- `unpublishCoursePackage()` - 下架
- `getAvailableCourses()` - 获取可用课程

### 5. 类型定义

文件：`src/types/coursePackage.ts`

主要类型：
- `CoursePackage` - 课程包
- `CoursePackageItem` - 课程包明细
- `CoursePackageFormData` - 表单数据
- `CoursePackageQueryParams` - 查询参数
- `CourseInfo` - 课程信息

## 使用说明

### 1. 创建课程包

1. 点击"新增课程包"按钮
2. 填写基本信息（名称、描述等）
3. 设置价格（原价、售价，系统自动计算折扣）
4. 使用Transfer组件选择要包含的课程
5. 为每个课程设置课时数
6. 设置有效期和状态
7. 提交保存

### 2. 编辑课程包

1. 点击列表中的"编辑"按钮
2. 修改课程包信息
3. 可以添加或删除课程
4. 提交保存

### 3. 上架/下架

- 点击"上架"按钮将课程包状态设置为上架（可用于新合同）
- 点击"下架"按钮将课程包状态设置为下架（不可用于新合同）

### 4. 删除课程包

- 点击"删除"按钮
- 确认删除操作
- 注意：如果课程包已被合同使用，将无法删除

## 业务规则

1. **课程包编码唯一性**：系统自动生成，格式为PKG + 6位数字
2. **至少包含1个课程**：创建课程包时必须选择至少一个课程
3. **售价不能高于原价**：前端和后端都会进行验证
4. **删除检查**：删除前检查是否被合同使用
5. **下架限制**：下架的课程包不能用于新合同
6. **总课时计算**：自动计算所有课程的课时之和
7. **折扣计算**：自动计算折扣 = 售价 / 原价 * 10

## 设计风格

### 暗色科技主题
- 背景色：#111827
- 主色调：青色渐变 (#00d4ff → #0099ff)
- 边框：rgba(0, 212, 255, 0.1)
- 卡片：玻璃态效果

### 状态颜色
- 上架：绿色 (#00ff88)
- 下架：红色 (#ff4d6a)
- 价格：橙色 (#ffaa00)

### 交互效果
- 悬停：卡片上浮、阴影增强
- 渐变按钮：青色渐变
- 发光边框：青色光晕

## 测试建议

### 后端测试
1. 测试课程包的CRUD操作
2. 测试课程包编码自动生成
3. 测试总课时和折扣自动计算
4. 测试课程验证逻辑
5. 测试上架/下架功能
6. 测试删除时的关联检查

### 前端测试
1. 测试列表加载和分页
2. 测试搜索和筛选功能
3. 测试表单验证
4. 测试课程选择器
5. 测试价格和折扣计算
6. 测试上架/下架操作
7. 测试删除确认

## 注意事项

1. **数据迁移**：确保按顺序执行SQL迁移文件
2. **课程数据**：创建课程包前需要先有课程数据
3. **权限控制**：根据实际需求配置菜单权限
4. **图片上传**：需要配置文件上传功能
5. **校区隔离**：如果启用多校区，注意数据隔离
6. **性能优化**：大量数据时考虑分页和索引优化

## 扩展功能建议

1. **批量操作**：批量上架/下架、批量删除
2. **导出功能**：导出课程包列表为Excel
3. **复制功能**：快速复制现有课程包
4. **价格策略**：支持时间段折扣、学员类型折扣
5. **销售统计**：统计课程包销售情况
6. **推荐算法**：根据学员情况推荐合适的课程包
7. **预览功能**：课程包详情预览页面
8. **版本管理**：课程包内容变更历史记录

## 相关文档

- [课程管理](../course/README.md)
- [合同管理](../../finance/contract/README.md)
- [价格策略](../price-strategy/README.md)
