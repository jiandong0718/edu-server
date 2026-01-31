package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.CoursePackageDTO;
import com.edu.teaching.domain.entity.CoursePackage;
import com.edu.teaching.domain.vo.CoursePackageVO;

/**
 * 课程包服务接口
 */
public interface CoursePackageService extends IService<CoursePackage> {

    /**
     * 创建课程包
     */
    boolean createPackage(CoursePackageDTO dto);

    /**
     * 更新课程包
     */
    boolean updatePackage(CoursePackageDTO dto);

    /**
     * 获取课程包详情（包含明细）
     */
    CoursePackageVO getPackageDetail(Long id);

    /**
     * 分页查询课程包
     */
    Page<CoursePackage> pagePackages(Integer pageNum, Integer pageSize, String name, Integer status);

    /**
     * 上架课程包
     */
    boolean publishPackage(Long id);

    /**
     * 下架课程包
     */
    boolean unpublishPackage(Long id);

    /**
     * 删除课程包（级联删除明细）
     */
    boolean deletePackage(Long id);

    /**
     * 验证课程包中的课程是否存在
     */
    boolean validateCourses(CoursePackageDTO dto);
}
