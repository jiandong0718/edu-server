package com.edu.marketing.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.marketing.domain.dto.TrialAppointmentDTO;
import com.edu.marketing.domain.dto.TrialFeedbackDTO;
import com.edu.marketing.domain.dto.TrialLessonQueryDTO;
import com.edu.marketing.domain.dto.TrialSignInDTO;
import com.edu.marketing.domain.entity.Lead;
import com.edu.marketing.domain.entity.TrialLesson;
import com.edu.marketing.domain.vo.AdvisorPerformanceVO;
import com.edu.marketing.domain.vo.ConversionFunnelVO;
import com.edu.marketing.domain.vo.TrialLessonVO;
import com.edu.marketing.mapper.LeadMapper;
import com.edu.marketing.mapper.TrialLessonMapper;
import com.edu.marketing.service.TrialLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 试听记录服务实现
 *
 * @author edu
 * @since 2024-01-30
 */
@Service
@RequiredArgsConstructor
public class TrialLessonServiceImpl extends ServiceImpl<TrialLessonMapper, TrialLesson> implements TrialLessonService {

    private final LeadMapper leadMapper;

    @Override
    public IPage<TrialLesson> pageList(IPage<TrialLesson> page, TrialLesson query) {
        return baseMapper.selectTrialLessonPage(page, query);
    }

    @Override
    public IPage<TrialLessonVO> pageListVO(IPage<TrialLessonVO> page, TrialLessonQueryDTO query) {
        return baseMapper.selectTrialLessonVOPage(page, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAppointment(TrialAppointmentDTO dto) {
        // 验证线索或学员ID至少有一个
        if (dto.getLeadId() == null && dto.getStudentId() == null) {
            throw new BusinessException("线索ID和学员ID至少需要提供一个");
        }

        // 如果有线索ID，更新线索状态
        if (dto.getLeadId() != null) {
            Lead lead = leadMapper.selectById(dto.getLeadId());
            if (lead == null) {
                throw new BusinessException("线索不存在");
            }
            // 更新线索状态为已预约
            if ("new".equals(lead.getStatus()) || "following".equals(lead.getStatus())) {
                lead.setStatus("appointed");
                leadMapper.updateById(lead);
            }
        }

        // 创建试听记录
        TrialLesson trialLesson = new TrialLesson();
        trialLesson.setLeadId(dto.getLeadId());
        trialLesson.setStudentId(dto.getStudentId());
        trialLesson.setCourseId(dto.getCourseId());
        trialLesson.setClassId(dto.getClassId());
        trialLesson.setScheduleId(dto.getScheduleId());
        trialLesson.setCampusId(dto.getCampusId());
        trialLesson.setTrialDate(dto.getTrialDate());
        trialLesson.setTrialTime(dto.getTrialTime());
        trialLesson.setAdvisorId(dto.getAdvisorId());
        trialLesson.setStatus("appointed");
        trialLesson.setRemark(dto.getRemark());

        save(trialLesson);
        return trialLesson.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signIn(TrialSignInDTO dto) {
        TrialLesson trialLesson = getById(dto.getTrialId());
        if (trialLesson == null) {
            throw new BusinessException("试听记录不存在");
        }

        if (!"appointed".equals(trialLesson.getStatus())) {
            throw new BusinessException("只有已预约状态的试听记录才能签到");
        }

        // 更新试听记录状态
        trialLesson.setStatus(dto.getStatus());
        if (dto.getRemark() != null) {
            trialLesson.setRemark(dto.getRemark());
        }

        // 如果签到成功，更新线索状态
        if ("attended".equals(dto.getStatus()) && trialLesson.getLeadId() != null) {
            Lead lead = leadMapper.selectById(trialLesson.getLeadId());
            if (lead != null && "appointed".equals(lead.getStatus())) {
                lead.setStatus("trialed");
                leadMapper.updateById(lead);
            }
        }

        return updateById(trialLesson);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitFeedback(TrialFeedbackDTO dto) {
        TrialLesson trialLesson = getById(dto.getTrialId());
        if (trialLesson == null) {
            throw new BusinessException("试听记录不存在");
        }

        if (!"attended".equals(trialLesson.getStatus())) {
            throw new BusinessException("只有已到场状态的试听记录才能提交反馈");
        }

        // 验证评分范围
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }

        // 更新试听记录
        trialLesson.setFeedback(dto.getFeedback());
        trialLesson.setRating(dto.getRating());
        if (dto.getRemark() != null) {
            trialLesson.setRemark(dto.getRemark());
        }

        return updateById(trialLesson);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAppointment(Long trialId) {
        TrialLesson trialLesson = getById(trialId);
        if (trialLesson == null) {
            throw new BusinessException("试听记录不存在");
        }

        if (!"appointed".equals(trialLesson.getStatus())) {
            throw new BusinessException("只有已预约状态的试听记录才能取消");
        }

        // 删除试听记录
        boolean result = removeById(trialId);

        // 如果有关联线索，恢复线索状态
        if (result && trialLesson.getLeadId() != null) {
            Lead lead = leadMapper.selectById(trialLesson.getLeadId());
            if (lead != null && "appointed".equals(lead.getStatus())) {
                lead.setStatus("following");
                leadMapper.updateById(lead);
            }
        }

        return result;
    }

    @Override
    public List<TrialLesson> getByLeadId(Long leadId) {
        LambdaQueryWrapper<TrialLesson> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrialLesson::getLeadId, leadId)
                .orderByDesc(TrialLesson::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<TrialLesson> getByStudentId(Long studentId) {
        LambdaQueryWrapper<TrialLesson> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrialLesson::getStudentId, studentId)
                .orderByDesc(TrialLesson::getCreateTime);
        return list(wrapper);
    }

    @Override
    public ConversionFunnelVO getConversionFunnel(Long campusId, LocalDate startDate, LocalDate endDate) {
        // 构建查询条件
        LambdaQueryWrapper<Lead> leadWrapper = new LambdaQueryWrapper<>();
        leadWrapper.eq(campusId != null, Lead::getCampusId, campusId);
        if (startDate != null) {
            leadWrapper.ge(Lead::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            leadWrapper.le(Lead::getCreateTime, endDate.plusDays(1).atStartOfDay());
        }

        // 查询所有线索
        List<Lead> leads = leadMapper.selectList(leadWrapper);

        // 统计各状态数量
        Map<String, Long> statusCount = leads.stream()
                .collect(Collectors.groupingBy(Lead::getStatus, Collectors.counting()));

        ConversionFunnelVO vo = new ConversionFunnelVO();
        vo.setNewLeadCount(statusCount.getOrDefault("new", 0L).intValue());
        vo.setFollowingCount(statusCount.getOrDefault("following", 0L).intValue());
        vo.setAppointedCount(statusCount.getOrDefault("appointed", 0L).intValue());
        vo.setTrialedCount(statusCount.getOrDefault("trialed", 0L).intValue());
        vo.setConvertedCount(statusCount.getOrDefault("converted", 0L).intValue());
        vo.setLostCount(statusCount.getOrDefault("lost", 0L).intValue());

        // 计算转化率
        int totalLeads = leads.size();
        if (totalLeads > 0) {
            vo.setAppointmentRate(BigDecimal.valueOf(vo.getAppointedCount())
                    .divide(BigDecimal.valueOf(totalLeads), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            vo.setTrialRate(BigDecimal.valueOf(vo.getTrialedCount())
                    .divide(BigDecimal.valueOf(totalLeads), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            vo.setConversionRate(BigDecimal.valueOf(vo.getConvertedCount())
                    .divide(BigDecimal.valueOf(totalLeads), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            vo.setOverallRate(vo.getConversionRate());
        } else {
            vo.setAppointmentRate(BigDecimal.ZERO);
            vo.setTrialRate(BigDecimal.ZERO);
            vo.setConversionRate(BigDecimal.ZERO);
            vo.setOverallRate(BigDecimal.ZERO);
        }

        return vo;
    }

    @Override
    public List<AdvisorPerformanceVO> getAdvisorPerformance(Long advisorId, Long campusId, LocalDate startDate, LocalDate endDate) {
        // 构建查询条件
        LambdaQueryWrapper<Lead> leadWrapper = new LambdaQueryWrapper<>();
        leadWrapper.eq(advisorId != null, Lead::getAdvisorId, advisorId)
                .eq(campusId != null, Lead::getCampusId, campusId);
        if (startDate != null) {
            leadWrapper.ge(Lead::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            leadWrapper.le(Lead::getCreateTime, endDate.plusDays(1).atStartOfDay());
        }

        // 查询线索
        List<Lead> leads = leadMapper.selectList(leadWrapper);

        // 按顾问分组统计
        Map<Long, List<Lead>> leadsByAdvisor = leads.stream()
                .filter(lead -> lead.getAdvisorId() != null)
                .collect(Collectors.groupingBy(Lead::getAdvisorId));

        List<AdvisorPerformanceVO> result = new ArrayList<>();

        for (Map.Entry<Long, List<Lead>> entry : leadsByAdvisor.entrySet()) {
            Long currentAdvisorId = entry.getKey();
            List<Lead> advisorLeads = entry.getValue();

            AdvisorPerformanceVO vo = new AdvisorPerformanceVO();
            vo.setAdvisorId(currentAdvisorId);
            // TODO: 查询顾问姓名
            vo.setAdvisorName("顾问" + currentAdvisorId);

            // 统计线索总数
            vo.setTotalLeadCount(advisorLeads.size());

            // 统计跟进次数
            int totalFollowUpCount = advisorLeads.stream()
                    .mapToInt(Lead::getFollowCount)
                    .sum();
            vo.setFollowUpCount(totalFollowUpCount);

            // 统计各状态数量
            Map<String, Long> statusCount = advisorLeads.stream()
                    .collect(Collectors.groupingBy(Lead::getStatus, Collectors.counting()));

            vo.setAppointmentCount(statusCount.getOrDefault("appointed", 0L).intValue());
            vo.setTrialCount(statusCount.getOrDefault("trialed", 0L).intValue());
            vo.setConversionCount(statusCount.getOrDefault("converted", 0L).intValue());

            // 计算转化率
            if (vo.getTotalLeadCount() > 0) {
                vo.setConversionRate(BigDecimal.valueOf(vo.getConversionCount())
                        .divide(BigDecimal.valueOf(vo.getTotalLeadCount()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)));
            } else {
                vo.setConversionRate(BigDecimal.ZERO);
            }

            // 计算平均跟进次数
            if (vo.getTotalLeadCount() > 0) {
                vo.setAvgFollowUpCount(BigDecimal.valueOf(totalFollowUpCount)
                        .divide(BigDecimal.valueOf(vo.getTotalLeadCount()), 2, RoundingMode.HALF_UP));
            } else {
                vo.setAvgFollowUpCount(BigDecimal.ZERO);
            }

            // TODO: 查询成交金额
            vo.setConversionAmount(BigDecimal.ZERO);

            result.add(vo);
        }

        return result;
    }
}
