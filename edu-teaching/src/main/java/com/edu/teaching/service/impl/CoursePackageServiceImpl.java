package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.teaching.domain.dto.CoursePackageDTO;
import com.edu.teaching.domain.entity.Course;
import com.edu.teaching.domain.entity.CoursePackage;
import com.edu.teaching.domain.entity.CoursePackageItem;
import com.edu.teaching.domain.vo.CoursePackageVO;
import com.edu.teaching.mapper.CoursePackageMapper;
import com.edu.teaching.service.CoursePackageItemService;
import com.edu.teaching.service.CoursePackageService;
import com.edu.teaching.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程包服务实现
 */
@Service
@RequiredArgsConstructor
public class CoursePackageServiceImpl extends ServiceImpl<CoursePackageMapper, CoursePackage> implements CoursePackageService {

    private final CoursePackageItemService packageItemService;
    private final CourseService courseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPackage(CoursePackageDTO dto) {
        // 创建课程包
        CoursePackage coursePackage = new CoursePackage();
        BeanUtils.copyProperties(dto, coursePackage);
        if (!save(coursePackage)) {
            return false;
        }

        // 创建课程包明细
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<CoursePackageItem> items = new ArrayList<>();
            for (CoursePackageDTO.CoursePackageItemDTO itemDTO : dto.getItems()) {
                CoursePackageItem item = new CoursePackageItem();
                item.setPackageId(coursePackage.getId());
                item.setCourseId(itemDTO.getCourseId());
                item.setCourseCount(itemDTO.getCourseCount());
                item.setSortOrder(itemDTO.getSortOrder());
                items.add(item);
            }
            return packageItemService.saveBatch(items);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePackage(CoursePackageDTO dto) {
        // 更新课程包
        CoursePackage coursePackage = new CoursePackage();
        BeanUtils.copyProperties(dto, coursePackage);
        if (!updateById(coursePackage)) {
            return false;
        }

        // 删除旧的明细
        packageItemService.deleteByPackageId(dto.getId());

        // 创建新的明细
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<CoursePackageItem> items = new ArrayList<>();
            for (CoursePackageDTO.CoursePackageItemDTO itemDTO : dto.getItems()) {
                CoursePackageItem item = new CoursePackageItem();
                item.setPackageId(dto.getId());
                item.setCourseId(itemDTO.getCourseId());
                item.setCourseCount(itemDTO.getCourseCount());
                item.setSortOrder(itemDTO.getSortOrder());
                items.add(item);
            }
            return packageItemService.saveBatch(items);
        }

        return true;
    }

    @Override
    public CoursePackageVO getPackageDetail(Long id) {
        CoursePackage coursePackage = getById(id);
        if (coursePackage == null) {
            return null;
        }

        CoursePackageVO vo = new CoursePackageVO();
        BeanUtils.copyProperties(coursePackage, vo);

        // 查询明细
        List<CoursePackageItem> items = packageItemService.listByPackageId(id);
        if (!items.isEmpty()) {
            // 查询课程信息
            List<Long> courseIds = items.stream()
                    .map(CoursePackageItem::getCourseId)
                    .collect(Collectors.toList());
            List<Course> courses = courseService.listByIds(courseIds);
            Map<Long, Course> courseMap = courses.stream()
                    .collect(Collectors.toMap(Course::getId, c -> c));

            // 组装明细VO
            List<CoursePackageVO.CoursePackageItemVO> itemVOs = new ArrayList<>();
            for (CoursePackageItem item : items) {
                CoursePackageVO.CoursePackageItemVO itemVO = new CoursePackageVO.CoursePackageItemVO();
                itemVO.setId(item.getId());
                itemVO.setCourseId(item.getCourseId());
                itemVO.setCourseCount(item.getCourseCount());
                itemVO.setSortOrder(item.getSortOrder());

                Course course = courseMap.get(item.getCourseId());
                if (course != null) {
                    itemVO.setCourseName(course.getName());
                    itemVO.setCourseCode(course.getCode());
                }
                itemVOs.add(itemVO);
            }
            vo.setItems(itemVOs);
        }

        return vo;
    }

    @Override
    public Page<CoursePackage> pagePackages(Integer pageNum, Integer pageSize, String name, Integer status) {
        Page<CoursePackage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CoursePackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, CoursePackage::getName, name)
                .eq(status != null, CoursePackage::getStatus, status)
                .orderByAsc(CoursePackage::getSortOrder);
        return page(page, wrapper);
    }

    @Override
    public boolean publishPackage(Long id) {
        CoursePackage coursePackage = new CoursePackage();
        coursePackage.setId(id);
        coursePackage.setStatus(1);
        return updateById(coursePackage);
    }

    @Override
    public boolean unpublishPackage(Long id) {
        CoursePackage coursePackage = new CoursePackage();
        coursePackage.setId(id);
        coursePackage.setStatus(0);
        return updateById(coursePackage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePackage(Long id) {
        // 删除明细
        packageItemService.deleteByPackageId(id);
        // 删除课程包
        return removeById(id);
    }

    @Override
    public boolean validateCourses(CoursePackageDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            return true;
        }

        List<Long> courseIds = dto.getItems().stream()
                .map(CoursePackageDTO.CoursePackageItemDTO::getCourseId)
                .collect(Collectors.toList());

        // 查询课程是否存在
        long count = courseService.count(new LambdaQueryWrapper<Course>()
                .in(Course::getId, courseIds));

        return count == courseIds.size();
    }
}
