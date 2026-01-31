# Flyway 快速参考卡

## 快速命令

### 查看迁移信息
```bash
./scripts/flyway-info.sh              # 开发环境
./scripts/flyway-info.sh -p prod     # 生产环境
```

### 验证迁移脚本
```bash
./scripts/flyway-validate.sh         # 开发环境
./scripts/flyway-validate.sh -p prod # 生产环境
```

### 修复元数据
```bash
./scripts/flyway-repair.sh           # 开发环境
./scripts/flyway-repair.sh -p prod   # 生产环境
./scripts/flyway-repair.sh -f        # 强制执行（跳过确认）
```

## 迁移脚本命名

### 版本化脚本
```
V{major}.{minor}.{patch}__{description}.sql
示例: V1.0.22__add_student_fields.sql
```

### 可重复脚本
```
R__{description}.sql
示例: R__update_views.sql
```

### 撤销脚本
```
U{major}.{minor}.{patch}__{description}.sql
示例: U1.0.22__undo_add_student_fields.sql
```

## 常用 Maven 命令

```bash
# 查看迁移信息
mvn flyway:info -pl edu-admin

# 验证迁移脚本
mvn flyway:validate -pl edu-admin

# 执行迁移
mvn flyway:migrate -pl edu-admin

# 修复元数据
mvn flyway:repair -pl edu-admin

# 启动应用（自动迁移）
mvn spring-boot:run -pl edu-admin
```

## 脚本模板

### 创建表
```sql
CREATE TABLE IF NOT EXISTS tch_xxx (
    id BIGINT NOT NULL COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='XXX表';
```

### 添加字段
```sql
ALTER TABLE stu_student
ADD COLUMN IF NOT EXISTS school_name VARCHAR(100) COMMENT '学校名称' AFTER contact_phone;
```

### 创建索引
```sql
CREATE INDEX IF NOT EXISTS idx_student_name ON stu_student(name);
```

## 常见问题快速解决

### 校验和不匹配
```bash
./scripts/flyway-repair.sh
```

### 迁移失败
```bash
# 1. 删除失败记录
DELETE FROM flyway_schema_history WHERE version = '1.0.x' AND success = 0;

# 2. 修复脚本
# 3. 重新执行
mvn spring-boot:run -pl edu-admin
```

### 版本号冲突
```bash
# 开发环境：启用 out-of-order
spring.flyway.out-of-order=true

# 或重命名脚本
mv V1.0.10__feature_a.sql V1.0.11__feature_a.sql
```

## 生产部署检查清单

- [ ] 备份数据库
- [ ] 验证迁移脚本：`./scripts/flyway-validate.sh -p prod`
- [ ] 查看待执行迁移：`./scripts/flyway-info.sh -p prod`
- [ ] 在测试环境验证
- [ ] 准备回滚方案
- [ ] 执行迁移
- [ ] 验证结果
- [ ] 检查应用日志

## 文档位置

- **详细指南**: `/Users/liujiandong/Documents/work/package/edu/edu-server/FLYWAY_GUIDE.md`
- **实现总结**: `/Users/liujiandong/Documents/work/package/edu/edu-server/TASK_2.13_IMPLEMENTATION_SUMMARY.md`
- **管理脚本**: `/Users/liujiandong/Documents/work/package/edu/edu-server/scripts/`

## 首次使用

```bash
# 1. 添加执行权限
chmod +x scripts/*.sh

# 2. 查看状态
./scripts/flyway-info.sh

# 3. 验证脚本
./scripts/flyway-validate.sh

# 4. 启动应用
mvn spring-boot:run -pl edu-admin
```

---
更新时间: 2026-01-31
