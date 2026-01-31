package com.edu.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.system.domain.entity.WarningConfig;
import com.edu.system.domain.vo.WarningVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据预警 Mapper
 */
@DS("system")
public interface DashboardWarningMapper extends BaseMapper<WarningConfig> {

    /**
     * 查询课时不足预警
     */
    List<WarningVO.CourseHourLowWarning> getCourseHourLowWarnings(
            @Param("campusId") Long campusId,
            @Param("threshold") Integer threshold);

    /**
     * 查询课时即将到期预警
     */
    List<WarningVO.CourseHourExpireWarning> getCourseHourExpireWarnings(
            @Param("campusId") Long campusId,
            @Param("daysThreshold") Integer daysThreshold);

    /**
     * 查询欠费预警
     */
    List<WarningVO.OverdueWarning> getOverdueWarnings(
            @Param("campusId") Long campusId,
            @Param("daysThreshold") Integer daysThreshold);

    /**
     * 查询合同即将到期预警
     */
    List<WarningVO.ContractExpireWarning> getContractExpireWarnings(
            @Param("campusId") Long campusId,
            @Param("daysThreshold") Integer daysThreshold);

    /**
     * 查询学员流失预警
     */
    List<WarningVO.StudentLossWarning> getStudentLossWarnings(
            @Param("campusId") Long campusId,
            @Param("daysThreshold") Integer daysThreshold);

    /**
     * 查询班级满员预警
     */
    List<WarningVO> getClassFullWarnings(@Param("campusId") Long campusId);

    /**
     * 查询教师排课冲突预警
     */
    List<WarningVO> getTeacherScheduleConflictWarnings(@Param("campusId") Long campusId);

    /**
     * 查询教室使用冲突预警
     */
    List<WarningVO> getClassroomConflictWarnings(@Param("campusId") Long campusId);

    /**
     * 查询试听转化率低预警
     */
    List<WarningVO> getTrialConversionLowWarnings(
            @Param("campusId") Long campusId,
            @Param("rateThreshold") Double rateThreshold);

    /**
     * 查询收入异常预警
     */
    List<WarningVO> getIncomeAbnormalWarnings(
            @Param("campusId") Long campusId,
            @Param("rateThreshold") Double rateThreshold);

    /**
     * 查询退费率高预警
     */
    List<WarningVO> getRefundRateHighWarnings(
            @Param("campusId") Long campusId,
            @Param("rateThreshold") Double rateThreshold);
}
