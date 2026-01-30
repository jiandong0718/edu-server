# edu-admin-api

教育机构学生管理系统 - 后端服务

## 技术栈

- Java 17+
- Spring Boot 3.2.x
- MyBatis-Plus 3.5.x
- MySQL 8.0+
- Redis 6.0+
- Knife4j (API 文档)
- Flyway (数据库版本管理)

## 模块说明

```
edu-admin-api/
├── edu-common/      # 公共模块（工具类、常量、异常）
├── edu-framework/   # 框架模块（安全、缓存、配置）
├── edu-system/      # 系统管理（用户、角色、菜单、字典）
├── edu-student/     # 学员管理
├── edu-teaching/    # 教学管理（课程、班级、排课、考勤）
├── edu-finance/     # 财务管理（合同、收费、课消）
├── edu-marketing/   # 营销运营（CRM、活动、通知）
└── edu-admin/       # 启动模块
```

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE edu_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 修改配置

编辑 `edu-admin/src/main/resources/application-dev.yml`，配置数据库和 Redis 连接信息。

### 3. 编译运行

```bash
# 编译
mvn clean install -DskipTests

# 运行
mvn spring-boot:run -pl edu-admin
```

### 4. 访问

- API 文档：http://localhost:8080/doc.html
- 健康检查：http://localhost:8080/actuator/health

## 开发规范

### 包结构

每个业务模块遵循以下包结构：

```
com.edu.{module}/
├── api/           # 对外暴露的接口（供其他模块调用）
│   └── dto/       # 对外 DTO
├── controller/    # REST 控制器
├── service/       # 业务逻辑
│   └── impl/      # 实现类
├── mapper/        # MyBatis Mapper
├── domain/        # 实体类
│   ├── entity/    # 数据库实体
│   ├── dto/       # 数据传输对象
│   └── vo/        # 视图对象
└── event/         # 领域事件
```

### 数据库表命名

| 前缀 | 模块 | 示例 |
|------|------|------|
| sys_ | edu-system | sys_user, sys_role |
| stu_ | edu-student | stu_student, stu_contact |
| tch_ | edu-teaching | tch_course, tch_class |
| fin_ | edu-finance | fin_contract, fin_payment |
| mkt_ | edu-marketing | mkt_lead, mkt_campaign |

### 模块间调用

- 通过 `api` 包中的接口调用，不直接依赖 `service`
- 使用 Spring Event 进行异步解耦

## License

MIT License
