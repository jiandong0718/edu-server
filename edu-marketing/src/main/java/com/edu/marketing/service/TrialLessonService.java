package com.edu.marketing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.marketing.domain.dto.TrialAppointmentDTO;
import com.edu.marketing.domain.dto.TrialFeedbackDTO;
import com.edu.marketing.domain.dto.TrialLessonQueryDTO;
import com.edu.marketing.domain.dto.TrialSignInDTO;
import com.edu.marketing.domain.entity.TrialLesson;
import com.edu.marketing.domain.vo.AdvisorPerformanceVO;
import com.edu.marketing.domain.vo.ConversionFunnelVO;
import com.edu.marketing.domain.vo.TrialLessonVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 试听记录服务接口
 *
 * @author edu
 * @since 2024-01-30
 */
public interface TrialLessonService extends IService<TrialLesson> {

    /**
     * 分页查询试听记录列表
     *
     * @param page 分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<TrialLesson> pageList(IPage<TrialLesson> page, TrialLesson query);

    /**
     * 分页查询试听记录VO列表（包含关联信息）
     *
     * @param page 分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<TrialLessonVO> pageListVO(IPage<TrialLessonVO> page, TrialLessonQueryDTO query);

    /**
     * 创建试听预约
     *
     * @param dto 试听预约信息
     * @return 试听记录ID
     */
    Long createAppointment(TrialAppointmentDTO dto);

    /**
     * 试听签到
     *
     * @param dto 签到信息
     * @return 是否成功
     */
    boolean signIn(TrialSignInDTO dto);

    /**
     * 提交试听反馈
     *
     * @param dto 反馈信息
     * @return 是否成功
     */
    boolean submitFeedback(TrialFeedbackDTO dto);

    /**
     * 取消试听预约
     *
     * @param trialId 试听记录ID
     * @return 是否成功
     */
    boolean cancelAppointment(Long trialId);

    /**
     * 获取线索的试听记录列表
     *
     * @param leadId 线索ID
     * @return 试听记录列表
     */
    List<TrialLesson> getByLeadId(Long leadId);

    /**
     * 获取学员的试听记录列表
     *
     * @param studentId 学员ID
     * @return 试听记录列表
     */
    List<TrialLesson> getByStudentId(Long studentId);

    /**
     * 获取招生转化漏斗统计
     *
     * @param campusId 校区ID（可选）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 转化漏斗统计
     */
    ConversionFunnelVO getConversionFunnel(Long campusId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取顾问业绩统计
     *
     * @param advisorId 顾问ID（可选，为空则查询所有顾问）
     * @param campusId 校区ID（可选）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 顾问业绩统计列表
     */
    List<AdvisorPerformanceVO> getAdvisorPerformance(Long advisorId, Long campusId, LocalDate startDate, LocalDate endDate);
}
