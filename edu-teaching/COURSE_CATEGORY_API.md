# 课程分类 CRUD 接口文档

## 概述

课程分类管理模块提供了完整的 CRUD 接口，支持树形结构、分类排序、启用/禁用状态管理，以及多校区数据隔离。

## 数据表结构

**表名**: `tch_course_category`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| name | VARCHAR(50) | 分类名称 |
| parent_id | BIGINT | 父分类ID（0表示顶级分类） |
| sort_order | INT | 排序号 |
| icon | VARCHAR(100) | 图标 |
| description | VARCHAR(500) | 描述 |
| status | TINYINT | 状态：0-禁用，1-启用 |
| campus_id | BIGINT | 校区ID（null表示全部校区可用） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | BIGINT | 创建人ID |
| update_by | BIGINT | 更新人ID |
| deleted | TINYINT | 删除标记：0-未删除，1-已删除 |

## API 接口列表

### 1. 分页查询分类列表

**接口**: `GET /teaching/course-category/page`

**描述**: 分页查询课程分类列表，支持按名称、状态、校区筛选

**请求参数**:
- `pageNum` (Integer, 可选): 页码，默认 1
- `pageSize` (Integer, 可选): 每页数量，默认 10
- `name` (String, 可选): 分类名称（模糊查询）
- `status` (Integer, 可选): 状态（0-禁用，1-启用）
- `campusId` (Long, 可选): 校区ID

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "语言类",
        "parentId": 0,
        "sortOrder": 1,
        "icon": "language",
        "description": "语言学习课程",
        "status": 1,
        "campusId": null,
        "createTime": "2024-01-30T10:00:00",
        "updateTime": "2024-01-30T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 获取分类树

**接口**: `GET /teaching/course-category/tree`

**描述**: 获取树形结构的课程分类列表

**请求参数**:
- `status` (Integer, 可选): 状态筛选（0-禁用，1-启用）
- `campusId` (Long, 可选): 校区ID

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "语言类",
      "parentId": 0,
      "sortOrder": 1,
      "icon": "language",
      "description": "语言学习课程",
      "status": 1,
      "campusId": null,
      "children": [
        {
          "id": 2,
          "name": "英语",
          "parentId": 1,
          "sortOrder": 1,
          "icon": "english",
          "description": "英语课程",
          "status": 1,
          "campusId": null,
          "children": null
        },
        {
          "id": 3,
          "name": "中文",
          "parentId": 1,
          "sortOrder": 2,
          "icon": "chinese",
          "description": "中文课程",
          "status": 1,
          "campusId": null,
          "children": null
        }
      ]
    }
  ]
}
```

### 3. 获取分类列表

**接口**: `GET /teaching/course-category/list`

**描述**: 获取扁平化的分类列表，支持按父分类、状态、校区筛选

**请求参数**:
- `parentId` (Long, 可选): 父分类ID
- `status` (Integer, 可选): 状态（0-禁用，1-启用）
- `campusId` (Long, 可选): 校区ID

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 2,
      "name": "英语",
      "parentId": 1,
      "sortOrder": 1,
      "icon": "english",
      "description": "英语课程",
      "status": 1,
      "campusId": null
    }
  ]
}
```

### 4. 获取分类详情

**接口**: `GET /teaching/course-category/{id}`

**描述**: 根据ID获取课程分类详情

**路径参数**:
- `id` (Long, 必填): 分类ID

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "name": "语言类",
    "parentId": 0,
    "sortOrder": 1,
    "icon": "language",
    "description": "语言学习课程",
    "status": 1,
    "campusId": null,
    "createTime": "2024-01-30T10:00:00",
    "updateTime": "2024-01-30T10:00:00"
  }
}
```

### 5. 新增分类

**接口**: `POST /teaching/course-category`

**描述**: 创建新的课程分类

**请求体**:
```json
{
  "name": "艺术类",
  "parentId": 0,
  "sortOrder": 3,
  "icon": "art",
  "description": "艺术类课程",
  "status": 1,
  "campusId": null
}
```

**字段说明**:
- `name` (String, 必填): 分类名称
- `parentId` (Long, 可选): 父分类ID，默认 0（顶级分类）
- `sortOrder` (Integer, 可选): 排序号，默认 0
- `icon` (String, 可选): 图标
- `description` (String, 可选): 描述
- `status` (Integer, 可选): 状态，默认 1（启用）
- `campusId` (Long, 可选): 校区ID，null 表示全部校区可用

**业务规则**:
- 同级分类名称不能重复
- 如果指定父分类，父分类必须存在

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

### 6. 修改分类

**接口**: `PUT /teaching/course-category`

**描述**: 更新课程分类信息

**请求体**:
```json
{
  "id": 1,
  "name": "语言类课程",
  "parentId": 0,
  "sortOrder": 1,
  "icon": "language",
  "description": "各类语言学习课程",
  "status": 1,
  "campusId": null
}
```

**字段说明**:
- `id` (Long, 必填): 分类ID
- 其他字段同新增接口

**业务规则**:
- 分类必须存在
- 同级分类名称不能重复（排除自己）
- 不能将自己设置为父分类
- 如果修改父分类，新父分类必须存在

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

### 7. 删除分类

**接口**: `DELETE /teaching/course-category/{id}`

**描述**: 删除指定的课程分类

**路径参数**:
- `id` (Long, 必填): 分类ID

**业务规则**:
- 存在子分类时不能删除
- 该分类下存在课程时不能删除

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

**错误示例**:
```json
{
  "code": 500,
  "msg": "存在子分类，无法删除",
  "data": null
}
```

### 8. 批量删除分类

**接口**: `DELETE /teaching/course-category/batch`

**描述**: 批量删除课程分类

**请求体**:
```json
[1, 2, 3]
```

**业务规则**:
- 所有分类都必须满足删除条件（无子分类、无关联课程）
- 任一分类不满足条件则全部回滚

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

### 9. 更新分类状态

**接口**: `PUT /teaching/course-category/{id}/status`

**描述**: 更新课程分类的启用/禁用状态

**路径参数**:
- `id` (Long, 必填): 分类ID

**请求参数**:
- `status` (Integer, 必填): 状态（0-禁用，1-启用）

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": true
}
```

## 核心功能特性

### 1. 树形结构支持

- 支持无限层级的树形结构
- 通过 `parent_id` 字段建立父子关系
- `parent_id = 0` 表示顶级分类
- 查询时自动构建树形结构，包含 `children` 字段

### 2. 分类排序

- 通过 `sort_order` 字段控制同级分类的显示顺序
- 数值越小，排序越靠前
- 查询时自动按 `sort_order` 升序排列

### 3. 启用/禁用状态

- `status = 1`: 启用
- `status = 0`: 禁用
- 支持单独更新状态接口
- 查询时可按状态筛选

### 4. 多校区数据隔离

- `campus_id = null`: 表示全部校区可用
- `campus_id = 具体值`: 表示仅该校区可用
- 查询时可按校区筛选

### 5. 数据完整性校验

- **新增/修改**: 同级分类名称唯一性校验
- **修改**: 不能将自己设置为父分类
- **删除**: 检查是否有子分类
- **删除**: 检查是否有关联课程

### 6. 软删除

- 使用 MyBatis-Plus 的 `@TableLogic` 注解
- 删除操作实际上是更新 `deleted` 字段
- 查询时自动过滤已删除数据

## 技术实现

### 核心类文件

1. **Entity**: `/edu-teaching/src/main/java/com/edu/teaching/domain/entity/CourseCategory.java`
   - 课程分类实体类
   - 继承 `BaseEntity`（包含 id、创建时间、更新时间等公共字段）
   - 包含 `children` 字段用于树形结构

2. **Mapper**: `/edu-teaching/src/main/java/com/edu/teaching/mapper/CourseCategoryMapper.java`
   - MyBatis-Plus Mapper 接口
   - 继承 `BaseMapper<CourseCategory>`

3. **Service**: `/edu-teaching/src/main/java/com/edu/teaching/service/CourseCategoryService.java`
   - 服务接口
   - 定义业务方法

4. **ServiceImpl**: `/edu-teaching/src/main/java/com/edu/teaching/service/impl/CourseCategoryServiceImpl.java`
   - 服务实现类
   - 实现树形结构构建
   - 实现数据完整性校验

5. **Controller**: `/edu-teaching/src/main/java/com/edu/teaching/controller/CourseCategoryController.java`
   - REST 控制器
   - 提供 HTTP 接口

### 关键技术点

1. **树形结构构建**:
   ```java
   private List<CourseCategory> buildTree(List<CourseCategory> categories, Long parentId) {
       return categories.stream()
               .filter(category -> {
                   Long pid = category.getParentId() != null ? category.getParentId() : 0L;
                   return pid.equals(parentId);
               })
               .peek(category -> {
                   List<CourseCategory> children = buildTree(categories, category.getId());
                   category.setChildren(children.isEmpty() ? null : children);
               })
               .toList();
   }
   ```

2. **名称唯一性校验**:
   ```java
   public boolean checkNameUnique(String name, Long parentId, Long excludeId) {
       LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
       wrapper.eq(CourseCategory::getName, name)
               .eq(CourseCategory::getParentId, parentId != null ? parentId : 0L);
       if (excludeId != null) {
           wrapper.ne(CourseCategory::getId, excludeId);
       }
       return count(wrapper) == 0;
   }
   ```

3. **删除前校验**:
   ```java
   @Transactional(rollbackFor = Exception.class)
   public boolean deleteCategory(Long categoryId) {
       // 检查是否有子分类
       if (hasChildren(categoryId)) {
           throw new BusinessException("存在子分类，无法删除");
       }
       // 检查是否有关联课程
       if (hasRelatedCourses(categoryId)) {
           throw new BusinessException("该分类下存在课程，无法删除");
       }
       return removeById(categoryId);
   }
   ```

## 数据库迁移

**文件**: `/edu-admin/src/main/resources/db/migration/V1.0.9__add_course_category_fields.sql`

```sql
-- 为课程分类表添加缺失字段
ALTER TABLE tch_course_category ADD COLUMN IF NOT EXISTS icon VARCHAR(100) COMMENT '图标' AFTER sort_order;
ALTER TABLE tch_course_category ADD COLUMN IF NOT EXISTS description VARCHAR(500) COMMENT '描述' AFTER icon;
ALTER TABLE tch_course_category ADD COLUMN IF NOT EXISTS campus_id BIGINT COMMENT '校区ID（null表示全部校区可用）' AFTER description;

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_parent_id ON tch_course_category(parent_id);
CREATE INDEX IF NOT EXISTS idx_campus_id ON tch_course_category(campus_id);
CREATE INDEX IF NOT EXISTS idx_status ON tch_course_category(status);
```

## 测试建议

### 1. 单元测试

- 测试树形结构构建
- 测试名称唯一性校验
- 测试删除前的完整性校验

### 2. 集成测试

- 测试完整的 CRUD 流程
- 测试多层级树形结构
- 测试并发场景下的数据一致性

### 3. 接口测试

使用 Knife4j 文档页面（http://localhost:8080/doc.html）进行接口测试：

1. 创建顶级分类
2. 创建子分类
3. 查询树形结构
4. 更新分类信息
5. 测试删除校验（有子分类时）
6. 删除子分类后再删除父分类

## 注意事项

1. **性能优化**: 对于大量分类数据，建议在前端实现懒加载
2. **缓存策略**: 可考虑对分类树进行缓存，减少数据库查询
3. **权限控制**: 需要配合权限系统，控制不同角色的操作权限
4. **数据隔离**: 多校区场景下，需要在业务层或数据权限层面实现数据隔离
5. **事务管理**: 批量操作使用 `@Transactional` 确保数据一致性

## 后续优化建议

1. 添加分类图标上传功能
2. 支持分类拖拽排序
3. 添加分类使用统计（关联课程数量）
4. 支持分类导入导出
5. 添加分类变更历史记录
