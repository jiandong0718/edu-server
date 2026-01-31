package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.teaching.domain.entity.CoursePackageItem;
import com.edu.teaching.mapper.CoursePackageItemMapper;
import com.edu.teaching.service.CoursePackageItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程包明细服务实现
 */
@Service
public class CoursePackageItemServiceImpl extends ServiceImpl<CoursePackageItemMapper, CoursePackageItem> implements CoursePackageItemService {

    @Override
    public List<CoursePackageItem> listByPackageId(Long packageId) {
        LambdaQueryWrapper<CoursePackageItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoursePackageItem::getPackageId, packageId)
                .orderByAsc(CoursePackageItem::getSortOrder);
        return list(wrapper);
    }

    @Override
    public boolean deleteByPackageId(Long packageId) {
        LambdaQueryWrapper<CoursePackageItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoursePackageItem::getPackageId, packageId);
        return remove(wrapper);
    }
}
