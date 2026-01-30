package com.edu.marketing.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.marketing.domain.entity.FollowUp;
import com.edu.marketing.domain.entity.Lead;
import com.edu.marketing.mapper.FollowUpMapper;
import com.edu.marketing.mapper.LeadMapper;
import com.edu.marketing.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 线索服务实现
 */
@Service
@RequiredArgsConstructor
public class LeadServiceImpl extends ServiceImpl<LeadMapper, Lead> implements LeadService {

    private final FollowUpMapper followUpMapper;

    @Override
    public IPage<Lead> pageList(IPage<Lead> page, Lead query) {
        return baseMapper.selectLeadPage(page, query);
    }

    @Override
    public boolean createLead(Lead lead) {
        if (StrUtil.isBlank(lead.getLeadNo())) {
            lead.setLeadNo(generateLeadNo());
        }
        if (StrUtil.isBlank(lead.getStatus())) {
            lead.setStatus("new");
        }
        lead.setFollowCount(0);
        return save(lead);
    }

    @Override
    public boolean assignLead(Long leadId, Long advisorId) {
        Lead lead = getById(leadId);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        lead.setAdvisorId(advisorId);
        if ("new".equals(lead.getStatus())) {
            lead.setStatus("following");
        }
        return updateById(lead);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAssignLead(List<Long> leadIds, Long advisorId) {
        for (Long leadId : leadIds) {
            assignLead(leadId, advisorId);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFollowUp(FollowUp followUp) {
        Lead lead = getById(followUp.getLeadId());
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }

        // 保存跟进记录
        followUpMapper.insert(followUp);

        // 更新线索信息
        lead.setLastFollowTime(LocalDateTime.now());
        lead.setFollowCount(lead.getFollowCount() + 1);
        if (followUp.getNextFollowTime() != null) {
            lead.setNextFollowTime(followUp.getNextFollowTime());
        }
        if ("new".equals(lead.getStatus())) {
            lead.setStatus("following");
        }
        return updateById(lead);
    }

    @Override
    public List<FollowUp> getFollowUpList(Long leadId) {
        return followUpMapper.selectList(
                new LambdaQueryWrapper<FollowUp>()
                        .eq(FollowUp::getLeadId, leadId)
                        .orderByDesc(FollowUp::getCreateTime)
        );
    }

    @Override
    public boolean updateStatus(Long id, String status, String lostReason) {
        Lead lead = getById(id);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        lead.setStatus(status);
        if ("lost".equals(status) && StrUtil.isNotBlank(lostReason)) {
            lead.setLostReason(lostReason);
        }
        return updateById(lead);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long convertToStudent(Long leadId) {
        Lead lead = getById(leadId);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        if ("converted".equals(lead.getStatus())) {
            throw new BusinessException("该线索已转化");
        }

        // TODO: 调用学员模块创建学员
        // Student student = new Student();
        // student.setName(lead.getName());
        // student.setPhone(lead.getPhone());
        // student.setGender(lead.getGender());
        // student.setSource(lead.getSource());
        // student.setCampusId(lead.getCampusId());
        // student.setAdvisorId(lead.getAdvisorId());
        // studentService.addStudent(student);

        // 更新线索状态
        lead.setStatus("converted");
        updateById(lead);

        // 返回学员ID
        return null; // TODO: 返回实际学员ID
    }

    @Override
    public boolean checkPhoneExists(String phone, Long excludeId) {
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Lead::getPhone, phone);
        if (excludeId != null) {
            wrapper.ne(Lead::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    private String generateLeadNo() {
        String prefix = "XS" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Lead::getLeadNo, prefix)
                .orderByDesc(Lead::getLeadNo)
                .last("LIMIT 1");
        Lead lastLead = getOne(wrapper);

        int seq = 1;
        if (lastLead != null && lastLead.getLeadNo() != null) {
            String lastNo = lastLead.getLeadNo();
            if (lastNo.length() > prefix.length()) {
                seq = Integer.parseInt(lastNo.substring(prefix.length())) + 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }
}
