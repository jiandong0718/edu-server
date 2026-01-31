# Flyway 数据库版本管理指南

## 目录

- [概述](#概述)
- [配置说明](#配置说明)
- [版本命名规范](#版本命名规范)
- [迁移脚本编写规范](#迁移脚本编写规范)
- [回滚策略](#回滚策略)
- [常用命令](#常用命令)
- [管理脚本](#管理脚本)
- [常见问题](#常见问题)
- [最佳实践](#最佳实践)

## 概述

本项目使用 Flyway 9.22.3 进行数据库版本管理，实现数据库结构的版本控制和自动迁移。

### 核心特性

- **版本化管理**：每个数据库变更都有明确的版本号
- **自动迁移**：应用启动时自动执行未应用的迁移脚本
- **校验机制**：确保数据库状态与迁移脚本一致
- **回滚支持**：通过撤销脚本实现数据库回滚
- **多环境支持**：开发、测试、生产环境配置隔离

### 目录结构

```
edu-admin/src/main/resources/db/migration/
├── V1.0.0__init.sql                    # 初始化脚本
├── V1.0.1__init_data.sql               # 初始数据
├── V1.0.2__business_tables.sql         # 业务表
├── V1.0.3__finance_marketing_tables.sql
├── ...
├── test_*.sql                          # 测试数据（不会被 Flyway 执行）
└── U1.0.x__*.sql                       # 撤销脚本（可选）
```

## 配置说明

### 开发环境配置 (application-dev.yml)

```yaml
spring:
  flyway:
    enabled: true                        # 启用 Flyway
    baseline-on-migrate: true            # 首次运行时创建基线
    baseline-version: 1.0.0              # 基线版本号
    baseline-description: "Initial baseline"
    locations: classpath:db/migration    # 迁移脚本位置
    table: flyway_schema_history         # 元数据表名
    validate-on-migrate: true            # 迁移前验证
    validate-migration-naming: true      # 验证命名规范
    out-of-order: true                   # 允许乱序执行（开发环境）
    placeholder-replacement: true        # 启用占位符替换
    placeholders:
      db_name: edu_admin
      env: dev
    encoding: UTF-8
    sql-migration-prefix: V              # SQL 迁移前缀
    sql-migration-separator: __          # 版本号与描述分隔符
    sql-migration-suffixes: .sql         # SQL 文件后缀
    clean-disabled: true                 # 禁用清理命令
    ignore-missing-migrations: false     # 不忽略缺失的迁移
    ignore-future-migrations: false      # 不忽略未来的迁移
```

### 生产环境配置 (application-prod.yml)

```yaml
spring:
  flyway:
    enabled: ${FLYWAY_ENABLED:true}      # 可通过环境变量控制
    baseline-on-migrate: true
    baseline-version: 1.0.0
    locations: classpath:db/migration
    table: flyway_schema_history
    validate-on-migrate: true
    validate-migration-naming: true
    out-of-order: false                  # 不允许乱序（生产环境严格）
    placeholder-replacement: true
    placeholders:
      db_name: ${FLYWAY_DB_NAME:edu_admin}
      env: prod
    encoding: UTF-8
    sql-migration-prefix: V
    sql-migration-separator: __
    sql-migration-suffixes: .sql
    clean-disabled: true                 # 生产环境必须禁用清理
    ignore-missing-migrations: false
    ignore-future-migrations: false
    target: ${FLYWAY_TARGET:latest}      # 可指定目标版本
```

### 配置项说明

| 配置项 | 说明 | 开发环境 | 生产环境 |
|--------|------|----------|----------|
| `enabled` | 是否启用 Flyway | true | true（可配置） |
| `baseline-on-migrate` | 首次运行时创建基线 | true | true |
| `validate-on-migrate` | 迁移前验证 | true | true |
| `out-of-order` | 允许乱序执行 | true | false |
| `clean-disabled` | 禁用清理命令 | true | true（必须） |
| `ignore-missing-migrations` | 忽略缺失的迁移 | false | false |
| `placeholder-replacement` | 占位符替换 | true | true |

## 版本命名规范

### 版本号格式

```
V{major}.{minor}.{patch}__{description}.sql
```

**示例：**
```
V1.0.0__init.sql
V1.0.1__init_data.sql
V1.0.2__business_tables.sql
V1.0.10__add_student_fields.sql
V1.1.0__add_notification_module.sql
V2.0.0__major_refactor.sql
```

### 版本号规则

1. **主版本号 (major)**：重大架构变更、不兼容的修改
   - 示例：`V2.0.0__major_refactor.sql`

2. **次版本号 (minor)**：新增功能模块、向后兼容的功能
   - 示例：`V1.1.0__add_notification_module.sql`

3. **修订号 (patch)**：Bug 修复、小的改进、字段调整
   - 示例：`V1.0.10__add_student_fields.sql`

### 描述命名规范

使用小写字母和下划线，清晰描述变更内容：

- `init` - 初始化
- `init_data` - 初始数据
- `add_xxx_table` - 添加表
- `add_xxx_fields` - 添加字段
- `update_xxx_table` - 修改表结构
- `drop_xxx_column` - 删除列
- `create_xxx_index` - 创建索引
- `alter_xxx_constraint` - 修改约束

### 特殊文件命名

1. **撤销脚本**（可选）：
   ```
   U1.0.10__undo_add_student_fields.sql
   ```

2. **可重复执行脚本**：
   ```
   R__update_views.sql
   R__refresh_procedures.sql
   ```

3. **测试数据**（不会被 Flyway 执行）：
   ```
   test_holiday_data.sql
   test_course_package_data.sql
   ```

## 迁移脚本编写规范

### 基本原则

1. **幂等性**：脚本可以安全地重复执行
2. **原子性**：一个脚本完成一个完整的变更
3. **向后兼容**：尽量保持向后兼容
4. **可回滚**：考虑回滚方案

### 脚本模板

#### 1. 创建表

```sql
-- 教育机构学生管理系统 - 添加 XXX 表
-- V1.0.x__add_xxx_table.sql

-- 检查表是否存在
CREATE TABLE IF NOT EXISTS tch_xxx (
    id BIGINT NOT NULL COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    campus_id BIGINT NOT NULL COMMENT '校区ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_campus_id (campus_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='XXX表';

-- 添加索引
CREATE INDEX idx_name ON tch_xxx(name);
```

#### 2. 添加字段

```sql
-- 教育机构学生管理系统 - 添加 XXX 字段
-- V1.0.x__add_xxx_fields.sql

-- 检查字段是否存在后再添加
ALTER TABLE stu_student
ADD COLUMN IF NOT EXISTS school_name VARCHAR(100) COMMENT '学校名称' AFTER contact_phone,
ADD COLUMN IF NOT EXISTS grade VARCHAR(50) COMMENT '年级' AFTER school_name;

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_school_name ON stu_student(school_name);

-- 添加注释
ALTER TABLE stu_student COMMENT = '学生表（已添加学校信息）';
```

#### 3. 修改字段

```sql
-- 教育机构学生管理系统 - 修改 XXX 字段
-- V1.0.x__update_xxx_fields.sql

-- 修改字段类型
ALTER TABLE fin_contract
MODIFY COLUMN total_amount DECIMAL(12,2) NOT NULL COMMENT '合同总金额（元）';

-- 修改字段默认值
ALTER TABLE stu_student
ALTER COLUMN status SET DEFAULT 1;

-- 重命名字段
ALTER TABLE tch_course
CHANGE COLUMN old_name new_name VARCHAR(100) COMMENT '新字段名';
```

#### 4. 删除字段/表

```sql
-- 教育机构学生管理系统 - 删除 XXX 字段
-- V1.0.x__drop_xxx_column.sql

-- 删除字段前先备份数据（可选）
-- CREATE TABLE backup_xxx_data AS SELECT id, old_column FROM xxx_table;

-- 删除字段
ALTER TABLE xxx_table DROP COLUMN IF EXISTS old_column;

-- 删除表（谨慎操作）
-- DROP TABLE IF EXISTS old_table;
```

#### 5. 创建索引

```sql
-- 教育机构学生管理系统 - 创建 XXX 索引
-- V1.0.x__create_xxx_index.sql

-- 创建普通索引
CREATE INDEX IF NOT EXISTS idx_student_name ON stu_student(name);

-- 创建唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_student_code ON stu_student(student_code);

-- 创建复合索引
CREATE INDEX IF NOT EXISTS idx_campus_status ON stu_student(campus_id, status);

-- 创建全文索引
CREATE FULLTEXT INDEX IF NOT EXISTS ft_student_remark ON stu_student(remark);
```

#### 6. 初始数据

```sql
-- 教育机构学生管理系统 - 初始化 XXX 数据
-- V1.0.x__init_xxx_data.sql

-- 使用 INSERT IGNORE 避免重复插入
INSERT IGNORE INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_time) VALUES
(1, '学生状态', 'student_status', 1, '学生状态字典', NOW()),
(2, '课程类型', 'course_type', 1, '课程类型字典', NOW());

-- 使用 ON DUPLICATE KEY UPDATE 处理冲突
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, sort_order, status, create_time)
VALUES (1, 'student_status', '在读', '1', 1, 1, NOW())
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label), update_time = NOW();
```

### 使用占位符

```sql
-- 使用配置的占位符
USE ${db_name};

-- 根据环境执行不同逻辑
-- 注意：Flyway 不支持条件语句，需要通过不同的脚本文件实现
```

### 注意事项

1. **必须添加注释**：每个表、字段都要有清晰的中文注释
2. **使用 IF EXISTS/IF NOT EXISTS**：确保脚本幂等性
3. **字符集统一**：使用 `utf8mb4` 和 `utf8mb4_unicode_ci`
4. **时间字段**：使用 `DATETIME` 类型，默认值 `CURRENT_TIMESTAMP`
5. **逻辑删除**：使用 `deleted` 字段，0-未删除，1-已删除
6. **主键类型**：使用 `BIGINT` 类型
7. **索引命名**：
   - 主键：`PRIMARY KEY`
   - 唯一索引：`uk_xxx`
   - 普通索引：`idx_xxx`
   - 外键：`fk_xxx`

## 回滚策略

### 方案一：撤销脚本（推荐）

为每个迁移脚本创建对应的撤销脚本：

```sql
-- V1.0.10__add_student_fields.sql
ALTER TABLE stu_student
ADD COLUMN school_name VARCHAR(100) COMMENT '学校名称';

-- U1.0.10__undo_add_student_fields.sql
ALTER TABLE stu_student
DROP COLUMN IF EXISTS school_name;
```

**执行回滚：**
```bash
# 使用管理脚本
./scripts/flyway-undo.sh 1.0.10

# 或手动执行撤销脚本
mysql -u root -p edu_admin < U1.0.10__undo_add_student_fields.sql
```

### 方案二：数据库备份恢复

**迁移前备份：**
```bash
# 备份数据库
mysqldump -u root -p edu_admin > backup_before_v1.0.10_$(date +%Y%m%d_%H%M%S).sql

# 执行迁移
mvn spring-boot:run -pl edu-admin
```

**回滚操作：**
```bash
# 恢复数据库
mysql -u root -p edu_admin < backup_before_v1.0.10_20260131_100000.sql

# 修复 Flyway 元数据
./scripts/flyway-repair.sh
```

### 方案三：版本回退

```bash
# 回退到指定版本
export FLYWAY_TARGET=1.0.9
mvn spring-boot:run -pl edu-admin
```

### 回滚最佳实践

1. **生产环境必须备份**：迁移前必须完整备份数据库
2. **测试回滚脚本**：在测试环境验证撤销脚本
3. **记录回滚步骤**：文档化回滚操作步骤
4. **保留备份**：至少保留最近 3 个版本的备份
5. **灰度发布**：重大变更采用灰度发布策略

## 常用命令

### Maven 命令

```bash
# 查看 Flyway 信息
mvn flyway:info -pl edu-admin

# 验证迁移脚本
mvn flyway:validate -pl edu-admin

# 执行迁移
mvn flyway:migrate -pl edu-admin

# 修复元数据
mvn flyway:repair -pl edu-admin

# 清理数据库（危险操作，生产环境禁用）
# mvn flyway:clean -pl edu-admin
```

### 应用启动时自动迁移

```bash
# 启动应用（自动执行迁移）
mvn spring-boot:run -pl edu-admin

# 禁用 Flyway 启动
mvn spring-boot:run -pl edu-admin -Dspring.flyway.enabled=false
```

### Docker 环境

```bash
# 启动服务（自动执行迁移）
docker-compose up -d

# 查看迁移日志
docker-compose logs -f edu-admin-api

# 进入容器执行命令
docker-compose exec edu-admin-api bash
```

## 管理脚本

项目提供了三个便捷的管理脚本：

### 1. flyway-info.sh - 查看版本信息

```bash
# 查看所有迁移版本信息
./scripts/flyway-info.sh

# 输出示例：
# +-----------+---------+---------------------+------+---------------------+
# | Category  | Version | Description         | Type | Installed On        |
# +-----------+---------+---------------------+------+---------------------+
# | Versioned | 1.0.0   | init                | SQL  | 2026-01-30 10:00:00 |
# | Versioned | 1.0.1   | init data           | SQL  | 2026-01-30 10:00:01 |
# | Versioned | 1.0.2   | business tables     | SQL  | 2026-01-30 10:00:02 |
# +-----------+---------+---------------------+------+---------------------+
```

### 2. flyway-validate.sh - 验证脚本

```bash
# 验证迁移脚本的完整性和一致性
./scripts/flyway-validate.sh

# 输出示例：
# Successfully validated 21 migrations (execution time 00:00.123s)
```

### 3. flyway-repair.sh - 修复元数据

```bash
# 修复 Flyway 元数据表
./scripts/flyway-repair.sh

# 使用场景：
# - 手动修改了数据库但未更新元数据
# - 删除了失败的迁移记录
# - 修复校验和不匹配的问题
```

## 常见问题

### 1. 校验和不匹配

**问题：**
```
Migration checksum mismatch for migration version 1.0.10
```

**原因：**
- 迁移脚本被修改
- 文件编码改变
- 换行符改变

**解决方案：**
```bash
# 方案一：修复元数据（如果确认脚本正确）
./scripts/flyway-repair.sh

# 方案二：删除错误的迁移记录
DELETE FROM flyway_schema_history WHERE version = '1.0.10';

# 方案三：重新执行迁移
mvn flyway:migrate -pl edu-admin
```

### 2. 迁移脚本执行失败

**问题：**
```
Migration V1.0.10__add_student_fields.sql failed
SQL State  : 42S21
Error Code : 1060
Message    : Duplicate column name 'school_name'
```

**解决方案：**
```bash
# 1. 修复数据库（手动删除重复字段）
ALTER TABLE stu_student DROP COLUMN school_name;

# 2. 删除失败的迁移记录
DELETE FROM flyway_schema_history WHERE version = '1.0.10' AND success = 0;

# 3. 修复脚本（添加 IF NOT EXISTS）
ALTER TABLE stu_student
ADD COLUMN IF NOT EXISTS school_name VARCHAR(100);

# 4. 重新执行迁移
mvn flyway:migrate -pl edu-admin
```

### 3. 版本号冲突

**问题：**
两个开发者创建了相同版本号的迁移脚本。

**解决方案：**
```bash
# 开发环境：启用 out-of-order
spring.flyway.out-of-order=true

# 生产环境：重命名脚本文件
mv V1.0.10__feature_a.sql V1.0.11__feature_a.sql
```

### 4. 基线问题

**问题：**
```
Found non-empty schema(s) "edu_admin" but no schema history table.
```

**解决方案：**
```bash
# 启用 baseline-on-migrate
spring.flyway.baseline-on-migrate=true

# 或手动创建基线
mvn flyway:baseline -pl edu-admin
```

### 5. 占位符未替换

**问题：**
```
No value provided for placeholder: ${db_name}
```

**解决方案：**
```yaml
# 确保配置了占位符
spring:
  flyway:
    placeholder-replacement: true
    placeholders:
      db_name: edu_admin
```

### 6. 迁移脚本未执行

**问题：**
新添加的迁移脚本没有被执行。

**排查步骤：**
```bash
# 1. 检查 Flyway 是否启用
spring.flyway.enabled=true

# 2. 检查脚本位置
spring.flyway.locations=classpath:db/migration

# 3. 检查文件命名
# 必须符合：V{version}__{description}.sql

# 4. 检查版本号
# 必须大于当前已执行的最大版本号（除非启用 out-of-order）

# 5. 查看 Flyway 信息
./scripts/flyway-info.sh
```

## 最佳实践

### 1. 开发流程

```bash
# 1. 创建新的迁移脚本
cd edu-admin/src/main/resources/db/migration
vim V1.0.22__add_new_feature.sql

# 2. 本地测试
mvn spring-boot:run -pl edu-admin

# 3. 验证迁移
./scripts/flyway-info.sh
./scripts/flyway-validate.sh

# 4. 提交代码
git add edu-admin/src/main/resources/db/migration/V1.0.22__add_new_feature.sql
git commit -m "feat: add new feature migration script"
git push
```

### 2. 团队协作

1. **版本号分配**：
   - 使用 Git 分支管理版本号
   - 主分支：V1.0.x
   - 功能分支：V1.1.x, V1.2.x

2. **代码审查**：
   - 迁移脚本必须经过 Code Review
   - 检查幂等性、向后兼容性
   - 验证索引和约束

3. **测试验证**：
   - 在测试环境完整测试
   - 验证回滚脚本
   - 性能测试（大表变更）

### 3. 生产部署

```bash
# 1. 备份数据库
mysqldump -u root -p edu_admin > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 验证迁移脚本
./scripts/flyway-validate.sh

# 3. 查看待执行的迁移
./scripts/flyway-info.sh

# 4. 执行迁移（方式一：应用启动）
docker-compose up -d

# 4. 执行迁移（方式二：手动执行）
mvn flyway:migrate -pl edu-admin -Pproduction

# 5. 验证结果
./scripts/flyway-info.sh
# 检查应用日志
docker-compose logs -f edu-admin-api
```

### 4. 性能优化

1. **大表变更**：
   ```sql
   -- 使用 ALGORITHM=INPLACE 避免锁表
   ALTER TABLE large_table
   ADD COLUMN new_column VARCHAR(100),
   ALGORITHM=INPLACE, LOCK=NONE;

   -- 分批更新数据
   UPDATE large_table SET new_column = 'default' WHERE id BETWEEN 1 AND 10000;
   UPDATE large_table SET new_column = 'default' WHERE id BETWEEN 10001 AND 20000;
   ```

2. **索引创建**：
   ```sql
   -- 在业务低峰期创建索引
   CREATE INDEX idx_xxx ON large_table(column) ALGORITHM=INPLACE, LOCK=NONE;
   ```

3. **数据迁移**：
   ```sql
   -- 使用临时表
   CREATE TABLE temp_table AS SELECT * FROM old_table;
   -- 处理数据
   -- 重命名表
   RENAME TABLE old_table TO old_table_backup, temp_table TO old_table;
   ```

### 5. 安全建议

1. **禁用危险命令**：
   ```yaml
   spring.flyway.clean-disabled=true  # 必须禁用
   ```

2. **权限控制**：
   - 生产环境使用只读账号验证
   - 迁移账号仅授予必要权限

3. **审计日志**：
   - 记录所有迁移操作
   - 保留迁移日志至少 6 个月

4. **变更窗口**：
   - 重大变更在维护窗口执行
   - 提前通知相关人员

### 6. 监控告警

1. **迁移失败告警**：
   - 监控应用启动日志
   - 迁移失败立即通知

2. **性能监控**：
   - 监控迁移执行时间
   - 大表变更提前评估

3. **元数据检查**：
   - 定期验证 Flyway 元数据
   - 检查校验和一致性

## 参考资料

- [Flyway 官方文档](https://flywaydb.org/documentation/)
- [Spring Boot Flyway 集成](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
- [MySQL 在线 DDL](https://dev.mysql.com/doc/refman/8.0/en/innodb-online-ddl.html)

## 更新日志

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2026-01-31 | 初始版本，完善 Flyway 配置和文档 |

---

**维护者**：开发团队
**最后更新**：2026-01-31
