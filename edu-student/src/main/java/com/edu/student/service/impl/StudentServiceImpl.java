package com.edu.student.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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

import java.util.List;

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
}
