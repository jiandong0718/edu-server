# 学员导出接口和 StudentApi 实现文档

## 概述

本文档描述了任务 10.8（学员导出接口）和任务 10.9（StudentApi 定义）的实现细节。

## 任务 10.8：学员导出接口

### 功能说明

实现了基于 EasyExcel 的学员数据导出功能，支持按条件筛选导出。

### 实现文件

#### 1. StudentExportDTO.java
**路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/domain/dto/StudentExportDTO.java`

**功能**: 定义导出 Excel 的数据结构

**字段说明**:
- 学员编号、姓名、性别、出生日期
- 手机号、身份证号、就读学校、年级
- 状态、来源、校区、顾问
- 联系人姓名、联系人关系、联系人电话
- 地址、备注

**特性**:
- 使用 `@ExcelProperty` 注解定义列名和顺序
- 使用 `@ColumnWidth` 注解设置列宽
- 使用 `@ContentRowHeight` 和 `@HeadRowHeight` 设置行高

#### 2. StudentServiceImpl.exportToExcel() 方法增强
**路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/service/impl/StudentServiceImpl.java`

**功能**: 实现学员数据导出逻辑

**实现要点**:
```java
@Override
public byte[] exportToExcel(Student query) {
    // 1. 根据查询条件获取学员列表（包含关联信息）
    // 2. 转换为导出DTO（包含数据格式化）
    // 3. 使用 EasyExcel 生成 Excel 文件
    // 4. 返回字节数组
}
```

**数据转换**:
- 性别：1→男, 2→女, 0→未知
- 状态：potential→潜在, trial→试听, enrolled→在读, suspended→休学, graduated→结业, refunded→退费
- 来源：offline→地推, referral→转介绍, online_ad→线上广告, walk_in→自然到访, phone→电话咨询
- 关系：father→父亲, mother→母亲, grandpa→爷爷, grandma→奶奶, other→其他
- 日期格式化：yyyy-MM-dd
- 联系人信息：自动获取主要联系人，如无则取第一个

**支持的筛选条件**:
- 姓名（模糊查询）
- 学员编号（模糊查询）
- 手机号（模糊查询）
- 状态（精确匹配）
- 来源（精确匹配）
- 校区ID（精确匹配）
- 顾问ID（精确匹配）

#### 3. StudentController.export() 方法
**路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/controller/StudentController.java`

**API 端点**: `GET /student/export`

**功能**: 导出学员数据为 Excel 文件

**请求参数**:
```
name: 姓名（可选）
studentNo: 学员编号（可选）
phone: 手机号（可选）
status: 状态（可选）
source: 来源（可选）
campusId: 校区ID（可选）
advisorId: 顾问ID（可选）
```

**响应**:
- Content-Type: application/octet-stream
- Content-Disposition: attachment; filename="students_[timestamp].xlsx"
- Body: Excel 文件字节流

**使用示例**:
```bash
# 导出所有学员
curl -O -J "http://localhost:8080/student/export"

# 导出在读学员
curl -O -J "http://localhost:8080/student/export?status=enrolled"

# 导出指定校区的学员
curl -O -J "http://localhost:8080/student/export?campusId=1"
```

### 技术特点

1. **使用 EasyExcel**
   - 高性能：支持大数据量导出
   - 低内存占用：流式写入
   - 样式支持：自动列宽调整

2. **数据完整性**
   - 包含学员基本信息
   - 包含关联的校区、顾问信息
   - 包含主要联系人信息

3. **多校区数据隔离**
   - 自动应用校区数据权限
   - 只导出当前用户有权限的数据

---

## 任务 10.9：StudentApi 定义

### 功能说明

定义了供其他模块（合同、排课、考勤等）调用的学员 API 接口，使用 DTO 传输数据，避免直接暴露 Entity。

### 实现文件

#### 1. StudentDTO.java
**路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/dto/StudentDTO.java`

**功能**: 学员信息传输对象

**字段**: 包含学员所有基本信息和关联信息
- 基本信息：ID、编号、姓名、性别、出生日期、手机号、身份证号等
- 扩展信息：头像、学校、年级、状态、来源、地址、备注
- 关联信息：校区ID/名称、顾问ID/名称、联系人列表、标签列表

#### 2. StudentContactDTO.java
**路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/dto/StudentContactDTO.java`

**功能**: 学员联系人传输对象

**字段**: 联系人ID、姓名、关系、手机号、是否主要联系人、是否接收通知、备注

#### 3. StudentQueryDTO.java
**路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/dto/StudentQueryDTO.java`

**功能**: 学员查询条件传输对象

**字段**: 姓名、编号、手机号、状态、校区ID、顾问ID、班级ID、标签ID

#### 4. StudentApi.java（接口定义）
**路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/StudentApi.java`

**功能**: 学员 API 接口定义

**方法列表**:

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| getStudentById | Long id | StudentDTO | 根据ID获取学员信息 |
| getStudentsByIds | List\<Long\> ids | List\<StudentDTO\> | 批量获取学员信息 |
| checkStudentExists | Long id | boolean | 检查学员是否存在 |
| getStudentsByClassId | Long classId | List\<StudentDTO\> | 获取班级的学员列表 |
| searchStudents | StudentQueryDTO query | List\<StudentDTO\> | 搜索学员 |
| getStudentByNo | String studentNo | StudentDTO | 根据学员编号获取 |
| getStudentByPhone | String phone | StudentDTO | 根据手机号获取 |
| getStudentsByCampusId | Long campusId | List\<StudentDTO\> | 获取校区的学员列表 |
| getStudentsByAdvisorId | Long advisorId | List\<StudentDTO\> | 获取顾问的学员列表 |
| updateStudentStatus | Long id, String status | boolean | 更新学员状态 |
| checkPhoneExists | String phone, Long excludeId | boolean | 检查手机号是否存在 |

#### 5. StudentApiImpl.java（接口实现）
**路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/impl/StudentApiImpl.java`

**功能**: 学员 API 接口实现

**实现要点**:
- 使用 `@Service` 注解注册为 Spring Bean
- 依赖注入 `StudentService` 进行业务处理
- 使用 `BeanUtil.copyProperties()` 进行 Entity 到 DTO 的转换
- 包含联系人列表的转换
- 支持多校区数据隔离

**数据转换示例**:
```java
private StudentDTO convertToDTO(Student student) {
    if (student == null) {
        return null;
    }
    StudentDTO dto = new StudentDTO();
    BeanUtil.copyProperties(student, dto);

    // 转换联系人列表
    if (CollUtil.isNotEmpty(student.getContacts())) {
        List<StudentContactDTO> contactDTOs = student.getContacts().stream()
                .map(this::convertContactToDTO)
                .collect(Collectors.toList());
        dto.setContacts(contactDTOs);
    }

    return dto;
}
```

### 使用示例

#### 在其他模块中使用 StudentApi

**1. 添加依赖**

在需要调用学员 API 的模块（如 edu-finance、edu-teaching）的 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.edu</groupId>
    <artifactId>edu-student</artifactId>
</dependency>
```

**2. 注入并使用**

```java
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final StudentApi studentApi;

    public void createContract(Long studentId) {
        // 检查学员是否存在
        if (!studentApi.checkStudentExists(studentId)) {
            throw new BusinessException("学员不存在");
        }

        // 获取学员信息
        StudentDTO student = studentApi.getStudentById(studentId);

        // 创建合同逻辑...
    }

    public List<StudentDTO> getClassStudents(Long classId) {
        // 获取班级学员列表
        return studentApi.getStudentsByClassId(classId);
    }
}
```

**3. 搜索学员示例**

```java
// 按条件搜索学员
StudentQueryDTO query = new StudentQueryDTO();
query.setStatus("enrolled");  // 在读学员
query.setCampusId(1L);        // 指定校区
List<StudentDTO> students = studentApi.searchStudents(query);
```

### 设计原则

1. **模块解耦**
   - 使用 DTO 而非 Entity，避免模块间的强依赖
   - 其他模块不需要了解学员模块的内部实现

2. **数据安全**
   - DTO 只包含必要的业务数据
   - 不暴露敏感的内部字段（如 deleted、createBy 等）

3. **扩展性**
   - 接口方法设计考虑了常见的业务场景
   - 易于添加新的查询方法

4. **性能优化**
   - 批量查询方法减少数据库访问
   - 支持条件查询避免全表扫描

---

## 数据库表结构

### stu_student（学员表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| student_no | VARCHAR(50) | 学员编号 |
| name | VARCHAR(50) | 姓名 |
| gender | TINYINT | 性别：0-未知，1-男，2-女 |
| birthday | DATE | 出生日期 |
| phone | VARCHAR(20) | 手机号 |
| id_card | VARCHAR(18) | 身份证号 |
| avatar | VARCHAR(255) | 头像 |
| school | VARCHAR(100) | 学校 |
| grade | VARCHAR(20) | 年级 |
| status | VARCHAR(20) | 状态 |
| source | VARCHAR(20) | 来源 |
| campus_id | BIGINT | 校区ID |
| advisor_id | BIGINT | 顾问ID |
| address | VARCHAR(255) | 地址 |
| remark | TEXT | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | BIGINT | 创建人 |
| update_by | BIGINT | 更新人 |
| deleted | TINYINT | 删除标志 |

### stu_contact（学员联系人表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| student_id | BIGINT | 学员ID |
| name | VARCHAR(50) | 联系人姓名 |
| relation | VARCHAR(20) | 关系 |
| phone | VARCHAR(20) | 手机号 |
| is_primary | TINYINT | 是否主要联系人 |
| receive_notify | TINYINT | 是否接收通知 |
| remark | VARCHAR(255) | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | BIGINT | 创建人 |
| update_by | BIGINT | 更新人 |
| deleted | TINYINT | 删除标志 |

---

## 测试建议

### 1. 导出功能测试

```bash
# 测试基本导出
curl -O -J "http://localhost:8080/student/export"

# 测试条件导出
curl -O -J "http://localhost:8080/student/export?status=enrolled&campusId=1"

# 测试大数据量导出（性能测试）
# 先创建大量测试数据，然后导出
```

### 2. API 接口测试

创建单元测试类：

```java
@SpringBootTest
class StudentApiTest {

    @Autowired
    private StudentApi studentApi;

    @Test
    void testGetStudentById() {
        StudentDTO student = studentApi.getStudentById(1L);
        assertNotNull(student);
        assertEquals("张三", student.getName());
    }

    @Test
    void testSearchStudents() {
        StudentQueryDTO query = new StudentQueryDTO();
        query.setStatus("enrolled");
        List<StudentDTO> students = studentApi.searchStudents(query);
        assertTrue(students.size() > 0);
    }

    @Test
    void testCheckStudentExists() {
        assertTrue(studentApi.checkStudentExists(1L));
        assertFalse(studentApi.checkStudentExists(999999L));
    }
}
```

---

## 注意事项

1. **多校区数据隔离**
   - 导出和查询都会自动应用校区数据权限
   - 确保在请求上下文中设置了正确的校区ID

2. **性能考虑**
   - 导出大量数据时，EasyExcel 使用流式写入，内存占用低
   - 建议对导出数量设置上限（如 10000 条）

3. **数据一致性**
   - StudentApi 返回的数据包含关联信息（校区名称、顾问名称等）
   - 确保关联数据的准确性

4. **扩展性**
   - 如需添加新的导出字段，在 StudentExportDTO 中添加即可
   - 如需添加新的 API 方法，在 StudentApi 接口中定义并实现

---

## 文件清单

### 新增文件

1. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/dto/StudentDTO.java`
2. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/dto/StudentContactDTO.java`
3. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/dto/StudentQueryDTO.java`
4. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/domain/dto/StudentExportDTO.java`
5. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/dto/package-info.java`

### 修改文件

1. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/StudentApi.java`
2. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/api/impl/StudentApiImpl.java`
3. `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/service/impl/StudentServiceImpl.java`

---

## 总结

本次实现完成了以下功能：

1. ✅ **任务 10.8**：实现了基于 EasyExcel 的学员导出功能
   - 支持多种筛选条件
   - 包含完整的学员信息和联系人信息
   - 自动格式化数据（性别、状态、来源等）
   - 支持多校区数据隔离

2. ✅ **任务 10.9**：定义了 StudentApi 接口
   - 提供 11 个常用的学员查询方法
   - 使用 DTO 传输数据，避免直接暴露 Entity
   - 支持单个查询、批量查询、条件搜索
   - 便于其他模块（合同、排课、考勤）调用

所有代码遵循 Spring Boot 3.2.x 规范，使用了项目中已有的技术栈（EasyExcel、MyBatis-Plus、Hutool 等），并保持了与现有代码风格的一致性。
