package com.edu.student.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.framework.mybatis.CampusContextHolder;
import com.edu.student.domain.dto.StudentImportDTO;
import com.edu.student.domain.dto.StudentImportResultDTO;
import com.edu.student.domain.entity.Student;
import com.edu.student.domain.entity.StudentContact;
import com.edu.student.domain.entity.StudentTagRelation;
import com.edu.student.mapper.StudentContactMapper;
import com.edu.student.mapper.StudentMapper;
import com.edu.student.mapper.StudentTagRelationMapper;
import com.edu.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 学员服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final StudentContactMapper contactMapper;
    private final StudentTagRelationMapper tagRelationMapper;

    @Override
    public IPage<Student> pageList(IPage<Student> page, Student query) {
        return baseMapper.selectStudentPage(page, query);
    }

    @Override
    public Student getDetail(Long id) {
        Student student = baseMapper.selectStudentDetail(id);
        if (student != null) {
            // 获取联系人
            List<StudentContact> contacts = contactMapper.selectList(
                    new LambdaQueryWrapper<StudentContact>()
                            .eq(StudentContact::getStudentId, id)
                            .orderByDesc(StudentContact::getIsPrimary)
            );
            student.setContacts(contacts);
        }
        return student;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStudent(Student student) {
        // 检查手机号
        if (StrUtil.isNotBlank(student.getPhone()) && checkPhoneExists(student.getPhone(), null)) {
            throw new BusinessException("手机号已存在");
        }

        // 生成学员编号
        if (StrUtil.isBlank(student.getStudentNo())) {
            student.setStudentNo(generateStudentNo());
        }

        // 默认状态为潜在
        if (StrUtil.isBlank(student.getStatus())) {
            student.setStatus("potential");
        }

        boolean result = save(student);

        // 保存联系人
        if (result && CollUtil.isNotEmpty(student.getContacts())) {
            for (StudentContact contact : student.getContacts()) {
                contact.setStudentId(student.getId());
                contactMapper.insert(contact);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudent(Student student) {
        // 检查手机号
        if (StrUtil.isNotBlank(student.getPhone()) && checkPhoneExists(student.getPhone(), student.getId())) {
            throw new BusinessException("手机号已存在");
        }

        return updateById(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudent(Long id) {
        // 删除联系人
        contactMapper.delete(new LambdaQueryWrapper<StudentContact>()
                .eq(StudentContact::getStudentId, id));
        // 删除学员
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudents(List<Long> ids) {
        // 删除联系人
        contactMapper.delete(new LambdaQueryWrapper<StudentContact>()
                .in(StudentContact::getStudentId, ids));
        // 删除学员
        return removeByIds(ids);
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        Student student = new Student();
        student.setId(id);
        student.setStatus(status);
        return updateById(student);
    }

    @Override
    public List<StudentContact> getContacts(Long studentId) {
        return contactMapper.selectList(
                new LambdaQueryWrapper<StudentContact>()
                        .eq(StudentContact::getStudentId, studentId)
                        .orderByDesc(StudentContact::getIsPrimary)
        );
    }

    @Override
    public boolean saveContact(StudentContact contact) {
        if (contact.getId() != null) {
            return contactMapper.updateById(contact) > 0;
        } else {
            return contactMapper.insert(contact) > 0;
        }
    }

    @Override
    public boolean deleteContact(Long contactId) {
        return contactMapper.deleteById(contactId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setTags(Long studentId, List<Long> tagIds) {
        // 删除原有标签关联
        tagRelationMapper.delete(new LambdaQueryWrapper<StudentTagRelation>()
                .eq(StudentTagRelation::getStudentId, studentId));

        // 添加新的标签关联
        if (CollUtil.isNotEmpty(tagIds)) {
            for (Long tagId : tagIds) {
                StudentTagRelation relation = new StudentTagRelation();
                relation.setStudentId(studentId);
                relation.setTagId(tagId);
                tagRelationMapper.insert(relation);
            }
        }
        return true;
    }

    @Override
    public boolean checkPhoneExists(String phone, Long excludeId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getPhone, phone);
        if (excludeId != null) {
            wrapper.ne(Student::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    /**
     * 生成学员编号
     */
    private String generateStudentNo() {
        // 格式：STU + 年月日 + 4位序号，如 STU202401300001
        String prefix = "STU" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 查询当天最大编号
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Student::getStudentNo, prefix)
                .orderByDesc(Student::getStudentNo)
                .last("LIMIT 1");
        Student lastStudent = getOne(wrapper);

        int seq = 1;
        if (lastStudent != null && lastStudent.getStudentNo() != null) {
            String lastNo = lastStudent.getStudentNo();
            if (lastNo.length() > prefix.length()) {
                seq = Integer.parseInt(lastNo.substring(prefix.length())) + 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }

    @Override
    public byte[] exportToExcel(Student query) {
        try {
            // 查询数据（包含关联信息）
            LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
            if (query != null) {
                wrapper.like(StrUtil.isNotBlank(query.getName()), Student::getName, query.getName())
                        .like(StrUtil.isNotBlank(query.getStudentNo()), Student::getStudentNo, query.getStudentNo())
                        .like(StrUtil.isNotBlank(query.getPhone()), Student::getPhone, query.getPhone())
                        .eq(StrUtil.isNotBlank(query.getStatus()), Student::getStatus, query.getStatus())
                        .eq(StrUtil.isNotBlank(query.getSource()), Student::getSource, query.getSource())
                        .eq(query.getCampusId() != null, Student::getCampusId, query.getCampusId())
                        .eq(query.getAdvisorId() != null, Student::getAdvisorId, query.getAdvisorId());
            }
            List<Student> students = baseMapper.selectStudentPage(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10000), query).getRecords();

            // 转换为导出DTO
            List<com.edu.student.domain.dto.StudentExportDTO> exportList = students.stream()
                    .map(this::convertToExportDTO)
                    .collect(java.util.stream.Collectors.toList());

            // 使用 EasyExcel 导出
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            EasyExcel.write(out, com.edu.student.domain.dto.StudentExportDTO.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("学员列表")
                    .doWrite(exportList);

            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出学员数据失败", e);
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }

    /**
     * 转换为导出DTO
     */
    private com.edu.student.domain.dto.StudentExportDTO convertToExportDTO(Student student) {
        com.edu.student.domain.dto.StudentExportDTO dto = new com.edu.student.domain.dto.StudentExportDTO();

        // 基本信息
        dto.setStudentNo(student.getStudentNo());
        dto.setName(student.getName());

        // 性别转换
        if (student.getGender() != null) {
            switch (student.getGender()) {
                case 1:
                    dto.setGender("男");
                    break;
                case 2:
                    dto.setGender("女");
                    break;
                default:
                    dto.setGender("未知");
                    break;
            }
        }

        // 出生日期
        if (student.getBirthday() != null) {
            dto.setBirthday(student.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        dto.setPhone(student.getPhone());
        dto.setIdCard(student.getIdCard());
        dto.setSchool(student.getSchool());
        dto.setGrade(student.getGrade());

        // 状态转换
        dto.setStatus(convertStatusToText(student.getStatus()));

        // 来源转换
        dto.setSource(convertSourceToText(student.getSource()));

        dto.setCampusName(student.getCampusName());
        dto.setAdvisorName(student.getAdvisorName());
        dto.setAddress(student.getAddress());
        dto.setRemark(student.getRemark());

        // 获取主要联系人信息
        if (CollUtil.isNotEmpty(student.getContacts())) {
            StudentContact primaryContact = student.getContacts().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsPrimary()))
                    .findFirst()
                    .orElse(student.getContacts().get(0));

            dto.setContactName(primaryContact.getName());
            dto.setContactRelation(convertRelationToText(primaryContact.getRelation()));
            dto.setContactPhone(primaryContact.getPhone());
        }

        return dto;
    }

    /**
     * 状态转换为文本
     */
    private String convertStatusToText(String status) {
        if (StrUtil.isBlank(status)) {
            return "";
        }
        switch (status) {
            case "potential":
                return "潜在";
            case "trial":
                return "试听";
            case "enrolled":
                return "在读";
            case "suspended":
                return "休学";
            case "graduated":
                return "结业";
            case "refunded":
                return "退费";
            default:
                return status;
        }
    }

    /**
     * 来源转换为文本
     */
    private String convertSourceToText(String source) {
        if (StrUtil.isBlank(source)) {
            return "";
        }
        switch (source) {
            case "offline":
                return "地推";
            case "referral":
                return "转介绍";
            case "online_ad":
                return "线上广告";
            case "walk_in":
                return "自然到访";
            case "phone":
                return "电话咨询";
            default:
                return source;
        }
    }

    /**
     * 关系转换为文本
     */
    private String convertRelationToText(String relation) {
        if (StrUtil.isBlank(relation)) {
            return "";
        }
        switch (relation) {
            case "father":
                return "父亲";
            case "mother":
                return "母亲";
            case "grandpa":
                return "爷爷";
            case "grandma":
                return "奶奶";
            case "other":
                return "其他";
            default:
                return relation;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importFromExcel(byte[] fileData) {
        try {
            ExcelReader reader = ExcelUtil.getReader(new ByteArrayInputStream(fileData));

            // 设置表头别名
            reader.addHeaderAlias("学员编号", "studentNo");
            reader.addHeaderAlias("姓名", "name");
            reader.addHeaderAlias("性别", "gender");
            reader.addHeaderAlias("出生日期", "birthday");
            reader.addHeaderAlias("手机号", "phone");
            reader.addHeaderAlias("学校", "school");
            reader.addHeaderAlias("年级", "grade");
            reader.addHeaderAlias("状态", "status");
            reader.addHeaderAlias("来源", "source");
            reader.addHeaderAlias("备注", "remark");

            // 读取数据
            List<Map<String, Object>> rows = reader.readAll();
            List<Student> students = new ArrayList<>();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Map<String, Object> row : rows) {
                Student student = new Student();
                student.setStudentNo((String) row.get("studentNo"));
                student.setName((String) row.get("name"));

                // 性别转换
                Object genderObj = row.get("gender");
                if (genderObj != null) {
                    String genderStr = genderObj.toString();
                    if ("男".equals(genderStr)) {
                        student.setGender(1);
                    } else if ("女".equals(genderStr)) {
                        student.setGender(2);
                    } else {
                        student.setGender(0);
                    }
                }

                // 出生日期转换
                Object birthdayObj = row.get("birthday");
                if (birthdayObj != null) {
                    try {
                        student.setBirthday(LocalDate.parse(birthdayObj.toString(), dateFormatter));
                    } catch (Exception e) {
                        // 忽略日期解析错误
                    }
                }

                student.setPhone((String) row.get("phone"));
                student.setSchool((String) row.get("school"));
                student.setGrade((String) row.get("grade"));
                student.setStatus((String) row.get("status"));
                student.setSource((String) row.get("source"));
                student.setRemark((String) row.get("remark"));

                // 验证必填字段
                if (StrUtil.isBlank(student.getName())) {
                    continue; // 跳过姓名为空的记录
                }

                // 生成学员编号（如果为空）
                if (StrUtil.isBlank(student.getStudentNo())) {
                    student.setStudentNo(generateStudentNo());
                }

                // 默认状态
                if (StrUtil.isBlank(student.getStatus())) {
                    student.setStatus("potential");
                }

                students.add(student);
            }

            // 批量保存
            if (!students.isEmpty()) {
                return saveBatch(students);
            }
            return true;
        } catch (Exception e) {
            throw new BusinessException("导入失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentImportResultDTO batchImportStudents(byte[] fileData) {
        StudentImportResultDTO result = StudentImportResultDTO.builder()
                .total(0)
                .successCount(0)
                .failureCount(0)
                .errors(new ArrayList<>())
                .build();

        try {
            // 使用 EasyExcel 读取数据
            List<StudentImportDTO> importList = EasyExcel.read(new ByteArrayInputStream(fileData))
                    .head(StudentImportDTO.class)
                    .sheet()
                    .doReadSync();

            if (CollUtil.isEmpty(importList)) {
                throw new BusinessException("导入文件为空");
            }

            result.setTotal(importList.size());

            // 获取当前校区ID
            Long campusId = CampusContextHolder.getCampusId();

            // 用于检查重复的手机号
            Set<String> phoneSet = new HashSet<>();
            Set<String> idCardSet = new HashSet<>();

            // 逐行处理数据
            for (int i = 0; i < importList.size(); i++) {
                StudentImportDTO dto = importList.get(i);
                int rowIndex = i + 2; // Excel行号从2开始（第1行是表头）

                try {
                    // 验证数据
                    String validationError = validateStudentImportData(dto, phoneSet, idCardSet);
                    if (StrUtil.isNotBlank(validationError)) {
                        result.getErrors().add(StudentImportResultDTO.ImportError.builder()
                                .rowIndex(rowIndex)
                                .studentName(dto.getName())
                                .errorMessage(validationError)
                                .build());
                        result.setFailureCount(result.getFailureCount() + 1);
                        continue;
                    }

                    // 检查手机号是否已存在于数据库
                    if (checkPhoneExists(dto.getPhone(), null)) {
                        result.getErrors().add(StudentImportResultDTO.ImportError.builder()
                                .rowIndex(rowIndex)
                                .studentName(dto.getName())
                                .errorMessage("手机号已存在于系统中")
                                .build());
                        result.setFailureCount(result.getFailureCount() + 1);
                        continue;
                    }

                    // 转换为学员实体
                    Student student = convertToStudent(dto, campusId);

                    // 保存学员
                    save(student);

                    // 保存联系人（如果有）
                    if (StrUtil.isNotBlank(dto.getContactName()) && StrUtil.isNotBlank(dto.getContactPhone())) {
                        StudentContact contact = convertToContact(dto, student.getId());
                        contactMapper.insert(contact);
                    }

                    // 记录成功的手机号和身份证号
                    phoneSet.add(dto.getPhone());
                    if (StrUtil.isNotBlank(dto.getIdCard())) {
                        idCardSet.add(dto.getIdCard());
                    }

                    result.setSuccessCount(result.getSuccessCount() + 1);

                } catch (Exception e) {
                    log.error("导入第{}行数据失败", rowIndex, e);
                    result.getErrors().add(StudentImportResultDTO.ImportError.builder()
                            .rowIndex(rowIndex)
                            .studentName(dto.getName())
                            .errorMessage("导入失败: " + e.getMessage())
                            .build());
                    result.setFailureCount(result.getFailureCount() + 1);
                }
            }

            return result;

        } catch (Exception e) {
            log.error("批量导入学员失败", e);
            throw new BusinessException("批量导入失败：" + e.getMessage());
        }
    }

    @Override
    public byte[] downloadImportTemplate() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // 创建示例数据
            List<StudentImportDTO> templateData = new ArrayList<>();
            StudentImportDTO example = new StudentImportDTO();
            example.setName("张三");
            example.setGender("男");
            example.setBirthday("2010-01-01");
            example.setPhone("13800138000");
            example.setIdCard("110101201001011234");
            example.setSchool("XX小学");
            example.setGrade("三年级");
            example.setContactName("张父");
            example.setContactRelation("父亲");
            example.setContactPhone("13900139000");
            example.setAddress("北京市朝阳区XX街道XX号");
            example.setRemark("示例数据");
            templateData.add(example);

            // 使用 EasyExcel 生成模板
            EasyExcel.write(out, StudentImportDTO.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("学员导入模板")
                    .doWrite(templateData);

            return out.toByteArray();

        } catch (Exception e) {
            log.error("生成导入模板失败", e);
            throw new BusinessException("生成导入模板失败：" + e.getMessage());
        }
    }

    /**
     * 验证导入数据
     */
    private String validateStudentImportData(StudentImportDTO dto, Set<String> phoneSet, Set<String> idCardSet) {
        // 手机号正则
        Pattern phonePattern = Pattern.compile("^1[3-9]\\d{9}$");

        // 1. 验证必填字段
        if (StrUtil.isBlank(dto.getName())) {
            return "姓名不能为空";
        }
        if (StrUtil.isBlank(dto.getGender())) {
            return "性别不能为空";
        }
        if (StrUtil.isBlank(dto.getBirthday())) {
            return "出生日期不能为空";
        }
        if (StrUtil.isBlank(dto.getPhone())) {
            return "手机号不能为空";
        }

        // 2. 验证性别格式
        if (!"男".equals(dto.getGender()) && !"女".equals(dto.getGender())) {
            return "性别格式错误，只能是'男'或'女'";
        }

        // 3. 验证出生日期格式
        try {
            LocalDate.parse(dto.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return "出生日期格式错误，应为yyyy-MM-dd格式";
        }

        // 4. 验证手机号格式
        if (!phonePattern.matcher(dto.getPhone()).matches()) {
            return "手机号格式错误，应为11位数字";
        }

        // 5. 验证身份证号格式（如果填写）
        if (StrUtil.isNotBlank(dto.getIdCard())) {
            if (!IdcardUtil.isValidCard(dto.getIdCard())) {
                return "身份证号格式错误";
            }
        }

        // 6. 检查本次导入中的重复数据
        if (phoneSet.contains(dto.getPhone())) {
            return "手机号在导入文件中重复";
        }
        if (StrUtil.isNotBlank(dto.getIdCard()) && idCardSet.contains(dto.getIdCard())) {
            return "身份证号在导入文件中重复";
        }

        // 7. 验证联系人信息（如果填写了联系人姓名，则联系人电话必填）
        if (StrUtil.isNotBlank(dto.getContactName()) && StrUtil.isBlank(dto.getContactPhone())) {
            return "填写了联系人姓名，联系人电话不能为空";
        }
        if (StrUtil.isNotBlank(dto.getContactPhone()) && !phonePattern.matcher(dto.getContactPhone()).matches()) {
            return "联系人电话格式错误，应为11位数字";
        }

        // 8. 验证联系人关系（如果填写）
        if (StrUtil.isNotBlank(dto.getContactRelation())) {
            if (!"父亲".equals(dto.getContactRelation()) &&
                    !"母亲".equals(dto.getContactRelation()) &&
                    !"其他".equals(dto.getContactRelation())) {
                return "联系人关系格式错误，只能是'父亲'、'母亲'或'其他'";
            }
        }

        return null;
    }

    /**
     * 转换为学员实体
     */
    private Student convertToStudent(StudentImportDTO dto, Long campusId) {
        Student student = new Student();

        // 基本信息
        student.setName(dto.getName());
        student.setGender("男".equals(dto.getGender()) ? 1 : 2);
        student.setBirthday(LocalDate.parse(dto.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        student.setPhone(dto.getPhone());
        student.setIdCard(dto.getIdCard());
        student.setSchool(dto.getSchool());
        student.setGrade(dto.getGrade());
        student.setAddress(dto.getAddress());
        student.setRemark(dto.getRemark());

        // 生成学员编号
        student.setStudentNo(generateStudentNo());

        // 默认状态为潜在
        student.setStatus("potential");

        // 设置校区ID
        if (campusId != null) {
            student.setCampusId(campusId);
        }

        return student;
    }

    /**
     * 转换为联系人实体
     */
    private StudentContact convertToContact(StudentImportDTO dto, Long studentId) {
        StudentContact contact = new StudentContact();
        contact.setStudentId(studentId);
        contact.setName(dto.getContactName());
        contact.setPhone(dto.getContactPhone());

        // 转换关系
        if (StrUtil.isNotBlank(dto.getContactRelation())) {
            switch (dto.getContactRelation()) {
                case "父亲":
                    contact.setRelation("father");
                    break;
                case "母亲":
                    contact.setRelation("mother");
                    break;
                default:
                    contact.setRelation("other");
                    break;
            }
        } else {
            contact.setRelation("other");
        }

        // 设置为主要联系人
        contact.setIsPrimary(true);
        contact.setReceiveNotify(true);

        return contact;
    }
}
