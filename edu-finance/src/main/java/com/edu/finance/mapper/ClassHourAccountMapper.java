package com.edu.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.finance.domain.entity.ClassHourAccount;
import com.edu.finance.domain.vo.ClassHourStatisticsVO;
import org.apache.ibatis.annotations.Param;

/**
 * 课时账户 Mapper
 */
public interface ClassHourAccountMapper extends BaseMapper<ClassHourAccount> {

    /**
     * 统计学员课时使用情况
     *
     * @param studentId 学员ID
     * @return 统计结果
     */
    ClassHourStatisticsVO statisticsByStudent(@Param("studentId") Long studentId);

    /**
     * 统计课程消课情况
     *
     * @param courseId 课程ID
     * @return 统计结果
     */
    ClassHourStatisticsVO statisticsByCourse(@Param("courseId") Long courseId);

    /**
     * 统计校区课时数据
     *
     * @param campusId 校区ID
     * @return 统计结果
     */
    ClassHourStatisticsVO statisticsByCampus(@Param("campusId") Long campusId);
}
