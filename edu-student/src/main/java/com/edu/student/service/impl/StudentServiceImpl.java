package com.edu.student.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.student.domain.entity.Student;
import com.edu.student.domain.entity.StudentContact;
import com.edu.student.mapper.StudentContactMapper;
import com.edu.student.mapper.StudentMapper;
import com.edu.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 学员服务实现
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final StudentContactMapper contactMapper;

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
    public boolean setTags(Long studentId, List<Long> tagIds) {
        // TODO: 实现标签关联
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
            // 查询数据
            LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
            if (query != null) {
                wrapper.like(StrUtil.isNotBlank(query.getName()), Student::getName, query.getName())
                        .like(StrUtil.isNotBlank(query.getStudentNo()), Student::getStudentNo, query.getStudentNo())
                        .like(StrUtil.isNotBlank(query.getPhone()), Student::getPhone, query.getPhone())
                        .eq(StrUtil.isNotBlank(query.getStatus()), Student::getStatus, query.getStatus())
                        .eq(query.getCampusId() != null, Student::getCampusId, query.getCampusId());
            }
            List<Student> students = list(wrapper);

            // 创建Excel
            ExcelWriter writer = ExcelUtil.getWriter(true);

            // 设置表头
            writer.addHeaderAlias("studentNo", "学员编号");
            writer.addHeaderAlias("name", "姓名");
            writer.addHeaderAlias("gender", "性别");
            writer.addHeaderAlias("birthday", "出生日期");
            writer.addHeaderAlias("phone", "手机号");
            writer.addHeaderAlias("school", "学校");
            writer.addHeaderAlias("grade", "年级");
            writer.addHeaderAlias("status", "状态");
            writer.addHeaderAlias("source", "来源");
            writer.addHeaderAlias("campusName", "校区");
            writer.addHeaderAlias("advisorName", "顾问");
            writer.addHeaderAlias("remark", "备注");

            // 写入数据
            writer.write(students, true);

            // 输出到字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out);
            writer.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("导出失败：" + e.getMessage());
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
}
