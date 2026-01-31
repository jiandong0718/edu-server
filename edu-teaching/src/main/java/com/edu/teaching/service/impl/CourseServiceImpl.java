package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.entity.Course;
import com.edu.teaching.domain.entity.TeachClass;
import com.edu.teaching.mapper.CourseMapper;
import com.edu.teaching.mapper.TeachClassMapper;
import com.edu.teaching.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final TeachClassMapper teachClassMapper;

    @Override
    public boolean checkCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCode, code);
        if (excludeId != null) {
            wrapper.ne(Course::getId, excludeId);
        }
        return count(wrapper) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean onSale(Long id) {
        Course course = getById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 检查课程状态
        if ("ON_SALE".equals(course.getStatus())) {
            throw new BusinessException("课程已经在售，无需重复上架");
        }

        // 上架前检查：课程信息完整性
        validateCourseForSale(course);

        // 更新状态为在售
        Course updateCourse = new Course();
        updateCourse.setId(id);
        updateCourse.setStatus("ON_SALE");
        updateCourse.setOnSaleTime(LocalDateTime.now());
        return updateById(updateCourse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean offSale(Long id) {
        Course course = getById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 检查课程状态
        if ("OFF_SALE".equals(course.getStatus())) {
            throw new BusinessException("课程已经下架，无需重复下架");
        }

        if ("DRAFT".equals(course.getStatus())) {
            throw new BusinessException("草稿状态的课程无法下架");
        }

        // 下架前检查：是否有进行中的班级使用该课程
        checkActiveClasses(id);

        // 更新状态为已下架
        Course updateCourse = new Course();
        updateCourse.setId(id);
        updateCourse.setStatus("OFF_SALE");
        updateCourse.setOffSaleTime(LocalDateTime.now());
        return updateById(updateCourse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchOnSale(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            throw new BusinessException("请选择要上架的课程");
        }

        for (Long courseId : courseIds) {
            try {
                onSale(courseId);
            } catch (BusinessException e) {
                // 记录失败的课程，继续处理其他课程
                log.warn("课程上架失败: courseId={}, error={}", courseId, e.getMessage());
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchOffSale(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            throw new BusinessException("请选择要下架的课程");
        }

        for (Long courseId : courseIds) {
            try {
                offSale(courseId);
            } catch (BusinessException e) {
                // 记录失败的课程，继续处理其他课程
                log.warn("课程下架失败: courseId={}, error={}", courseId, e.getMessage());
            }
        }
        return true;
    }

    @Override
    public List<Course> getOnSaleCourses() {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, "ON_SALE")
                .orderByDesc(Course::getOnSaleTime)
                .orderByAsc(Course::getSortOrder);
        return list(wrapper);
    }

    /**
     * 验证课程是否满足上架条件
     */
    private void validateCourseForSale(Course course) {
        // 检查课程名称
        if (!StringUtils.hasText(course.getName())) {
            throw new BusinessException("课程名称不能为空");
        }

        // 检查课程分类
        if (course.getCategoryId() == null) {
            throw new BusinessException("请选择课程分类");
        }

        // 检查课程价格
        if (course.getPrice() == null || course.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("请设置课程价格");
        }

        // 检查课程课时
        if (course.getTotalHours() == null || course.getTotalHours() <= 0) {
            throw new BusinessException("请设置课程总课时数");
        }

        // 检查单次课时长
        if (course.getDuration() == null || course.getDuration() <= 0) {
            throw new BusinessException("请设置单次课时长");
        }
    }

    /**
     * 检查是否有进行中的班级使用该课程
     */
    private void checkActiveClasses(Long courseId) {
        LambdaQueryWrapper<TeachClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachClass::getCourseId, courseId)
                .in(TeachClass::getStatus, "recruiting", "in_progress");
        long count = teachClassMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该课程有进行中的班级，无法下架");
        }
    }

    @Override
    @Deprecated
    public boolean publish(Long id) {
        return onSale(id);
    }

    @Override
    @Deprecated
    public boolean unpublish(Long id) {
        return offSale(id);
    }
}
