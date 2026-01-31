package com.edu.finance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.finance.domain.dto.ClassHourBalanceQueryDTO;
import com.edu.finance.domain.dto.ClassHourWarningQueryDTO;
import com.edu.finance.domain.entity.ClassHourAccount;
import com.edu.finance.domain.vo.*;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 课时账户 Mapper
 */
@DS("finance")
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

    /**
     * 查询学员课时余额
     *
     * @param studentId 学员ID
     * @return 课时余额
     */
    ClassHourBalanceVO getBalanceByStudent(@Param("studentId") Long studentId);

    /**
     * 查询账户详情
     *
     * @param accountId 账户ID
     * @return 账户详情
     */
    ClassHourBalanceVO getBalanceDetail(@Param("accountId") Long accountId);

    /**
     * 分页查询课时余额
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<ClassHourBalanceVO> pageBalance(Page<ClassHourBalanceVO> page, @Param("query") ClassHourBalanceQueryDTO query);

    /**
     * 查询预警列表
     *
     * @param query 查询条件
     * @return 预警列表
     */
    List<ClassHourWarningVO> getWarningList(@Param("query") ClassHourWarningQueryDTO query);

    /**
     * 课时汇总统计
     *
     * @param campusId 校区ID
     * @return 汇总统计
     */
    ClassHourSummaryVO getSummary(@Param("campusId") Long campusId);

    /**
     * 按课程统计
     *
     * @param campusId 校区ID
     * @return 课程统计列表
     */
    List<ClassHourStatisticsVO> statisticsListByCourse(@Param("campusId") Long campusId);

    /**
     * 按学员统计
     *
     * @param campusId 校区ID
     * @return 学员统计列表
     */
    List<ClassHourStatisticsVO> statisticsListByStudent(@Param("campusId") Long campusId);

    /**
     * 消课统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param campusId  校区ID
     * @return 消课统计列表
     */
    List<ClassHourConsumptionVO> statisticsConsumption(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate,
                                                       @Param("campusId") Long campusId);
}
