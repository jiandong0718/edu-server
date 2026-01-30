package com.edu.marketing.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.marketing.domain.entity.FollowUp;
import com.edu.marketing.domain.entity.Lead;
import com.edu.marketing.mapper.FollowUpMapper;
import com.edu.marketing.mapper.LeadMapper;
import com.edu.marketing.service.LeadService;
import com.edu.student.api.StudentApi;
import com.edu.student.domain.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 线索服务实现
 */
@Service
@RequiredArgsConstructor
public class LeadServiceImpl extends ServiceImpl<LeadMapper, Lead> implements LeadService {

    private final FollowUpMapper followUpMapper;

    @Autowired(required = false)
    private StudentApi studentApi;

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

        // 检查学员API是否可用
        if (studentApi == null) {
            throw new BusinessException("学员服务不可用");
        }

        // 检查手机号是否已存在学员
        Student existStudent = studentApi.getByPhone(lead.getPhone());
        if (existStudent != null) {
            throw new BusinessException("该手机号已存在学员记录");
        }

        // 创建学员
        Student student = new Student();
        student.setName(lead.getName());
        student.setPhone(lead.getPhone());
        student.setGender(lead.getGender());
        student.setSource(lead.getSource());
        student.setCampusId(lead.getCampusId());
        student.setAdvisorId(lead.getAdvisorId());
        student.setStatus("trial"); // 设置为试听状态
        student.setRemark("由线索转化：" + lead.getLeadNo());

        // 这里需要调用学员服务保存学员
        // 由于StudentApi没有save方法，需要通过其他方式创建
        // 暂时返回null，实际应该调用学员服务的创建接口
        Long studentId = null; // TODO: 调用学员服务创建学员并返回ID

        // 更新线索状态
        lead.setStatus("converted");
        updateById(lead);

        return studentId;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importFromExcel(byte[] fileData) {
        try {
            ExcelReader reader = ExcelUtil.getReader(new ByteArrayInputStream(fileData));

            // 设置表头别名
            reader.addHeaderAlias("姓名", "name");
            reader.addHeaderAlias("手机号", "phone");
            reader.addHeaderAlias("性别", "gender");
            reader.addHeaderAlias("年龄", "age");
            reader.addHeaderAlias("来源", "source");
            reader.addHeaderAlias("来源详情", "sourceDetail");
            reader.addHeaderAlias("意向程度", "intentLevel");
            reader.addHeaderAlias("备注", "remark");

            // 读取数据
            List<Map<String, Object>> rows = reader.readAll();
            List<Lead> leads = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                Lead lead = new Lead();
                lead.setName((String) row.get("name"));
                lead.setPhone((String) row.get("phone"));

                // 性别转换
                Object genderObj = row.get("gender");
                if (genderObj != null) {
                    String genderStr = genderObj.toString();
                    if ("男".equals(genderStr)) {
                        lead.setGender(1);
                    } else if ("女".equals(genderStr)) {
                        lead.setGender(2);
                    } else {
                        lead.setGender(0);
                    }
                }

                // 年龄转换
                Object ageObj = row.get("age");
                if (ageObj != null) {
                    try {
                        lead.setAge(Integer.parseInt(ageObj.toString()));
                    } catch (Exception e) {
                        // 忽略年龄解析错误
                    }
                }

                lead.setSource((String) row.get("source"));
                lead.setSourceDetail((String) row.get("sourceDetail"));
                lead.setIntentLevel((String) row.get("intentLevel"));
                lead.setRemark((String) row.get("remark"));

                // 验证必填字段
                if (StrUtil.isBlank(lead.getName()) || StrUtil.isBlank(lead.getPhone())) {
                    continue; // 跳过姓名或手机号为空的记录
                }

                // 生成线索编号
                lead.setLeadNo(generateLeadNo());
                lead.setStatus("new");
                lead.setFollowCount(0);

                leads.add(lead);
            }

            // 批量保存
            if (!leads.isEmpty()) {
                return saveBatch(leads);
            }
            return true;
        } catch (Exception e) {
            throw new BusinessException("导入失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoAssignLeads(List<Long> leadIds, Long campusId) {
        // TODO: 实现自动分配逻辑
        // 1. 查询该校区的所有顾问
        // 2. 按照一定规则（如轮询、负载均衡等）分配线索
        // 3. 更新线索的顾问ID

        // 简单实现：查询该校区顾问数量最少的顾问
        // 这里需要查询系统用户表，获取顾问列表
        // 暂时抛出异常，提示需要实现
        throw new BusinessException("自动分配功能待实现");
    }
}
