package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.CoursePackageItem;

import java.util.List;

/**
 * 课程包明细服务接口
 */
public interface CoursePackageItemService extends IService<CoursePackageItem> {

    /**
     * 根据课程包ID查询明细列表
     */
    List<CoursePackageItem> listByPackageId(Long packageId);

    /**
     * 删除课程包的所有明细
     */
    boolean deleteByPackageId(Long packageId);
}
