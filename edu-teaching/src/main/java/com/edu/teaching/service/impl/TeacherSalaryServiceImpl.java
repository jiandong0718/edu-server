package com.edu.teaching.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.dto.TeacherSalaryDTO;
import com.edu.teaching.domain.entity.Course;
import com.edu.teaching.domain.entity.Teacher;
import com.edu.teaching.domain.entity.TeacherSalary;
import com.edu.teaching.domain.vo.TeacherSalaryVO;
import com.edu.teaching.mapper.CourseMapper;
import com.edu.teaching.mapper.TeacherMapper;
import com.edu.teaching.mapper.TeacherSalaryMapper;
import com.edu.teaching.service.TeacherSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 教师课酬配置服务实现
 */
@Service
@RequiredArgsConstructor
public class TeacherSalaryServiceImpl extends ServiceImpl<TeacherSalaryMapper, TeacherSalary> implements TeacherSalaryService {

    private final TeacherMapper teacherMapper;
    private final CourseMapper courseMapper;

    private static final Map<String, String> CLASS_TYPE_MAP = new HashMap<>();
    private static final Map<String, String> SALARY_TYPE_MAP = new HashMap<>();

    static {
        CLASS_TYPE_MAP.put("one_to_one", "一对一");
        CLASS_TYPE_MAP.put("small_class", "小班课");
        CLASS_TYPE_MAP.put("large_class", "大班课");

        SALARY_TYPE_MAP.put("per_hour", "按课时");
        SALARY_TYPE_MAP.put("per_class", "按课次");
        SALARY_TYPE_MAP.put("fixed", "固定");
    }

    @Override
    public Page<TeacherSalaryVO> pageQuery(Integer pageNum, Integer pageSize, Long teacherId, Long courseId, String classType, Long campusId) {
        Page<TeacherSalary> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TeacherSalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(teacherId != null, TeacherSalary::getTeacherId, teacherId)
                .eq(courseId != null, TeacherSalary::getCourseId, courseId)
                .eq(StrUtil.isNotBlank(classType), TeacherSalary::getClassType, classType)
                .orderByDesc(TeacherSalary::getEffectiveDate)
                .orderByDesc(TeacherSalary::getCreateTime);

        // 如果指定了校区，需要通过教师关联查询
        if (campusId != null) {
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getCampusId, campusId);
            List<Teacher> teachers = teacherMapper.selectList(teacherWrapper);
            if (teachers.isEmpty()) {
                return new Page<>(pageNum, pageSize, 0);
            }
            List<Long> teacherIds = teachers.stream().map(Teacher::getId).collect(Collectors.toList());
            wrapper.in(TeacherSalary::getTeacherId, teacherIds);
        }

        page(page, wrapper);

        // 转换为VO
        Page<TeacherSalaryVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<TeacherSalaryVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<TeacherSalaryVO> getByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherSalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherSalary::getTeacherId, teacherId)
                .orderByDesc(TeacherSalary::getEffectiveDate);
        List<TeacherSalary> salaries = list(wrapper);
        return salaries.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherSalaryVO> getCurrentValidSalaries(Long teacherId) {
        LocalDate now = LocalDate.now();
        LambdaQueryWrapper<TeacherSalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherSalary::getTeacherId, teacherId)
                .eq(TeacherSalary::getStatus, 1)
                .le(TeacherSalary::getEffectiveDate, now)
                .and(w -> w.isNull(TeacherSalary::getExpiryDate)
                        .or()
                        .ge(TeacherSalary::getExpiryDate, now))
                .orderByDesc(TeacherSalary::getEffectiveDate);
        List<TeacherSalary> salaries = list(wrapper);
        return salaries.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherSalaryVO getDetailById(Long id) {
        TeacherSalary salary = getById(id);
        if (salary == null) {
            throw new BusinessException("课酬配置不存在");
        }
        return convertToVO(salary);
    }

    @Override
    public TeacherSalaryVO getEffectiveSalary(Long teacherId, Long courseId, String classType) {
        LocalDate now = LocalDate.now();
        LambdaQueryWrapper<TeacherSalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherSalary::getTeacherId, teacherId)
                .eq(TeacherSalary::getStatus, 1)
                .le(TeacherSalary::getEffectiveDate, now)
                .and(w -> w.isNull(TeacherSalary::getExpiryDate)
                        .or()
                        .ge(TeacherSalary::getExpiryDate, now));

        // 优先级：课程+班级类型 > 课程 > 班级类型 > 通用配置
        // 1. 先查找指定课程和班级类型的配置
        if (courseId != null && StrUtil.isNotBlank(classType)) {
            LambdaQueryWrapper<TeacherSalary> specificWrapper = wrapper.clone();
            specificWrapper.eq(TeacherSalary::getCourseId, courseId)
                    .eq(TeacherSalary::getClassType, classType)
                    .orderByDesc(TeacherSalary::getEffectiveDate)
                    .last("LIMIT 1");
            TeacherSalary salary = getOne(specificWrapper);
            if (salary != null) {
                return convertToVO(salary);
            }
        }

        // 2. 查找指定课程的配置
        if (courseId != null) {
            LambdaQueryWrapper<TeacherSalary> courseWrapper = wrapper.clone();
            courseWrapper.eq(TeacherSalary::getCourseId, courseId)
                    .isNull(TeacherSalary::getClassType)
                    .orderByDesc(TeacherSalary::getEffectiveDate)
                    .last("LIMIT 1");
            TeacherSalary salary = getOne(courseWrapper);
            if (salary != null) {
                return convertToVO(salary);
            }
        }

        // 3. 查找指定班级类型的配置
        if (StrUtil.isNotBlank(classType)) {
            LambdaQueryWrapper<TeacherSalary> classTypeWrapper = wrapper.clone();
            classTypeWrapper.isNull(TeacherSalary::getCourseId)
                    .eq(TeacherSalary::getClassType, classType)
                    .orderByDesc(TeacherSalary::getEffectiveDate)
                    .last("LIMIT 1");
            TeacherSalary salary = getOne(classTypeWrapper);
            if (salary != null) {
                return convertToVO(salary);
            }
        }

        // 4. 查找通用配置
        LambdaQueryWrapper<TeacherSalary> defaultWrapper = wrapper.clone();
        defaultWrapper.isNull(TeacherSalary::getCourseId)
                .isNull(TeacherSalary::getClassType)
                .orderByDesc(TeacherSalary::getEffectiveDate)
                .last("LIMIT 1");
        TeacherSalary salary = getOne(defaultWrapper);
        return salary != null ? convertToVO(salary) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSalary(TeacherSalaryDTO dto) {
        // 验证教师是否存在
        Teacher teacher = teacherMapper.selectById(dto.getTeacherId());
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }

        // 验证课程是否存在
        if (dto.getCourseId() != null) {
            Course course = courseMapper.selectById(dto.getCourseId());
            if (course == null) {
                throw new BusinessException("课程不存在");
            }
        }

        // 验证班级类型
        if (StrUtil.isNotBlank(dto.getClassType()) && !CLASS_TYPE_MAP.containsKey(dto.getClassType())) {
            throw new BusinessException("无效的班级类型");
        }

        // 验证课酬类型
        if (!SALARY_TYPE_MAP.containsKey(dto.getSalaryType())) {
            throw new BusinessException("无效的课酬类型");
        }

        // 验证日期
        if (dto.getExpiryDate() != null && dto.getEffectiveDate() != null) {
            if (dto.getExpiryDate().isBefore(dto.getEffectiveDate())) {
                throw new BusinessException("失效日期不能早于生效日期");
            }
        }

        // 检查是否存在冲突的配置
        checkConflict(dto.getTeacherId(), dto.getCourseId(), dto.getClassType(),
                     dto.getEffectiveDate(), dto.getExpiryDate(), null);

        TeacherSalary salary = new TeacherSalary();
        BeanUtil.copyProperties(dto, salary);

        // 默认状态为启用
        if (salary.getStatus() == null) {
            salary.setStatus(1);
        }

        save(salary);
        return salary.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSalary(TeacherSalaryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("课酬配置ID不能为空");
        }

        TeacherSalary salary = getById(dto.getId());
        if (salary == null) {
            throw new BusinessException("课酬配置不存在");
        }

        // 验证课程是否存在
        if (dto.getCourseId() != null) {
            Course course = courseMapper.selectById(dto.getCourseId());
            if (course == null) {
                throw new BusinessException("课程不存在");
            }
        }

        // 验证班级类型
        if (StrUtil.isNotBlank(dto.getClassType()) && !CLASS_TYPE_MAP.containsKey(dto.getClassType())) {
            throw new BusinessException("无效的班级类型");
        }

        // 验证课酬类型
        if (StrUtil.isNotBlank(dto.getSalaryType()) && !SALARY_TYPE_MAP.containsKey(dto.getSalaryType())) {
            throw new BusinessException("无效的课酬类型");
        }

        // 验证日期
        if (dto.getExpiryDate() != null && dto.getEffectiveDate() != null) {
            if (dto.getExpiryDate().isBefore(dto.getEffectiveDate())) {
                throw new BusinessException("失效日期不能早于生效日期");
            }
        }

        // 检查是否存在冲突的配置
        checkConflict(salary.getTeacherId(), dto.getCourseId(), dto.getClassType(),
                     dto.getEffectiveDate(), dto.getExpiryDate(), dto.getId());

        BeanUtil.copyProperties(dto, salary, "id", "teacherId", "createTime", "createBy");
        return updateById(salary);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSalary(Long id) {
        TeacherSalary salary = getById(id);
        if (salary == null) {
            throw new BusinessException("课酬配置不存在");
        }
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return removeByIds(ids);
    }

    @Override
    public List<TeacherSalaryVO> getSalaryHistory(Long teacherId, Long courseId, String classType) {
        LambdaQueryWrapper<TeacherSalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherSalary::getTeacherId, teacherId)
                .eq(courseId != null, TeacherSalary::getCourseId, courseId)
                .eq(StrUtil.isNotBlank(classType), TeacherSalary::getClassType, classType)
                .orderByDesc(TeacherSalary::getEffectiveDate);
        List<TeacherSalary> salaries = list(wrapper);
        return salaries.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 检查是否存在冲突的配置
     */
    private void checkConflict(Long teacherId, Long courseId, String classType,
                              LocalDate effectiveDate, LocalDate expiryDate, Long excludeId) {
        LambdaQueryWrapper<TeacherSalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherSalary::getTeacherId, teacherId)
                .eq(courseId != null, TeacherSalary::getCourseId, courseId)
                .eq(StrUtil.isNotBlank(classType), TeacherSalary::getClassType, classType)
                .eq(TeacherSalary::getStatus, 1)
                .ne(excludeId != null, TeacherSalary::getId, excludeId);

        // 检查日期是否重叠
        wrapper.and(w -> {
            // 新配置的生效日期在现有配置的有效期内
            w.and(w1 -> w1.le(TeacherSalary::getEffectiveDate, effectiveDate)
                    .and(w2 -> w2.isNull(TeacherSalary::getExpiryDate)
                            .or()
                            .ge(TeacherSalary::getExpiryDate, effectiveDate)));

            // 或者新配置的失效日期在现有配置的有效期内
            if (expiryDate != null) {
                w.or(w1 -> w1.le(TeacherSalary::getEffectiveDate, expiryDate)
                        .and(w2 -> w2.isNull(TeacherSalary::getExpiryDate)
                                .or()
                                .ge(TeacherSalary::getExpiryDate, expiryDate)));
            }

            // 或者现有配置的生效日期在新配置的有效期内
            w.or(w1 -> {
                w1.ge(TeacherSalary::getEffectiveDate, effectiveDate);
                if (expiryDate != null) {
                    w1.le(TeacherSalary::getEffectiveDate, expiryDate);
                }
            });
        });

        long count = count(wrapper);
        if (count > 0) {
            throw new BusinessException("该时间段内已存在相同配置，请检查日期范围");
        }
    }

    /**
     * 转换为VO
     */
    private TeacherSalaryVO convertToVO(TeacherSalary salary) {
        TeacherSalaryVO vo = new TeacherSalaryVO();
        BeanUtil.copyProperties(salary, vo);

        // 设置班级类型名称
        if (StrUtil.isNotBlank(salary.getClassType())) {
            vo.setClassTypeName(CLASS_TYPE_MAP.getOrDefault(salary.getClassType(), "未知"));
        }

        // 设置课酬类型名称
        vo.setSalaryTypeName(SALARY_TYPE_MAP.getOrDefault(salary.getSalaryType(), "未知"));

        // 设置是否当前有效
        LocalDate now = LocalDate.now();
        boolean isValid = salary.getStatus() == 1
                && !salary.getEffectiveDate().isAfter(now)
                && (salary.getExpiryDate() == null || !salary.getExpiryDate().isBefore(now));
        vo.setCurrentlyValid(isValid);

        // 查询教师姓名
        if (salary.getTeacherId() != null) {
            Teacher teacher = teacherMapper.selectById(salary.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getName());
            }
        }

        // 查询课程名称
        if (salary.getCourseId() != null) {
            Course course = courseMapper.selectById(salary.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getName());
            }
        }

        return vo;
    }
}
