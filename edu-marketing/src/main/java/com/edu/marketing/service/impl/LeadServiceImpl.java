package com.edu.marketing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.framework.mybatis.CampusContextHolder;
import com.edu.marketing.domain.dto.LeadImportDTO;
import com.edu.marketing.domain.dto.LeadImportResultDTO;
import com.edu.marketing.domain.entity.FollowUp;
import com.edu.marketing.domain.entity.Lead;
import com.edu.marketing.mapper.FollowUpMapper;
import com.edu.marketing.mapper.LeadMapper;
import com.edu.marketing.service.LeadService;
import com.edu.student.api.StudentApi;
import com.edu.student.api.dto.StudentDTO;
import com.edu.student.domain.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 线索服务实现
 */
@Slf4j
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
        StudentDTO existStudent = studentApi.getStudentByPhone(lead.getPhone());
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
    public LeadImportResultDTO batchImportLeads(byte[] fileData) {
        LeadImportResultDTO result = LeadImportResultDTO.builder()
                .total(0)
                .successCount(0)
                .failureCount(0)
                .errors(new ArrayList<>())
                .build();

        try {
            // 使用 EasyExcel 读取数据
            List<LeadImportDTO> importList = EasyExcel.read(new ByteArrayInputStream(fileData))
                    .head(LeadImportDTO.class)
                    .sheet()
                    .doReadSync();

            if (CollUtil.isEmpty(importList)) {
                throw new BusinessException("导入文件为空");
            }

            result.setTotal(importList.size());

            // 获取当前校区ID
            Long campusId = CampusContextHolder.getCampusId();

            // 用于检查重复的手机号
            Set<String> phoneSet = new HashSet<>();

            // 逐行处理数据
            for (int i = 0; i < importList.size(); i++) {
                LeadImportDTO dto = importList.get(i);
                int rowIndex = i + 2; // Excel行号从2开始（第1行是表头）

                try {
                    // 验证数据
                    String validationError = validateLeadImportData(dto, phoneSet);
                    if (StrUtil.isNotBlank(validationError)) {
                        result.getErrors().add(LeadImportResultDTO.ImportError.builder()
                                .rowIndex(rowIndex)
                                .leadName(dto.getName())
                                .errorMessage(validationError)
                                .build());
                        result.setFailureCount(result.getFailureCount() + 1);
                        continue;
                    }

                    // 检查手机号是否已存在于数据库
                    if (checkPhoneExists(dto.getPhone(), null)) {
                        result.getErrors().add(LeadImportResultDTO.ImportError.builder()
                                .rowIndex(rowIndex)
                                .leadName(dto.getName())
                                .errorMessage("手机号已存在")
                                .build());
                        result.setFailureCount(result.getFailureCount() + 1);
                        continue;
                    }

                    // 转换并保存线索
                    Lead lead = convertImportDTOToLead(dto, campusId);
                    save(lead);

                    // 记录成功的手机号
                    phoneSet.add(dto.getPhone());
                    result.setSuccessCount(result.getSuccessCount() + 1);

                } catch (Exception e) {
                    log.error("导入线索失败，行号：{}，错误：{}", rowIndex, e.getMessage(), e);
                    result.getErrors().add(LeadImportResultDTO.ImportError.builder()
                            .rowIndex(rowIndex)
                            .leadName(dto.getName())
                            .errorMessage("导入失败：" + e.getMessage())
                            .build());
                    result.setFailureCount(result.getFailureCount() + 1);
                }
            }

            return result;
        } catch (Exception e) {
            log.error("批量导入线索失败", e);
            throw new BusinessException("导入失败：" + e.getMessage());
        }
    }

    /**
     * 验证线索导入数据
     */
    private String validateLeadImportData(LeadImportDTO dto, Set<String> phoneSet) {
        // 验证姓名
        if (StrUtil.isBlank(dto.getName())) {
            return "姓名不能为空";
        }
        if (dto.getName().length() > 50) {
            return "姓名长度不能超过50个字符";
        }

        // 验证手机号
        if (StrUtil.isBlank(dto.getPhone())) {
            return "手机号不能为空";
        }
        if (!Pattern.matches("^1[3-9]\\d{9}$", dto.getPhone())) {
            return "手机号格式不正确";
        }
        if (phoneSet.contains(dto.getPhone())) {
            return "手机号在导入文件中重复";
        }

        // 验证性别
        if (StrUtil.isNotBlank(dto.getGender())) {
            if (!"男".equals(dto.getGender()) && !"女".equals(dto.getGender())) {
                return "性别只能填写：男 或 女";
            }
        }

        // 验证年龄
        if (StrUtil.isNotBlank(dto.getAge())) {
            try {
                int age = Integer.parseInt(dto.getAge());
                if (age < 0 || age > 150) {
                    return "年龄必须在0-150之间";
                }
            } catch (NumberFormatException e) {
                return "年龄格式不正确";
            }
        }

        // 验证来源
        if (StrUtil.isNotBlank(dto.getSource())) {
            String source = dto.getSource().toLowerCase();
            if (!Arrays.asList("offline", "referral", "online_ad", "walk_in", "phone").contains(source)) {
                return "来源只能填写：offline、referral、online_ad、walk_in、phone";
            }
        }

        // 验证意向程度
        if (StrUtil.isNotBlank(dto.getIntentLevel())) {
            String intentLevel = dto.getIntentLevel().toLowerCase();
            if (!Arrays.asList("high", "medium", "low").contains(intentLevel)) {
                return "意向程度只能填写：high、medium、low";
            }
        }

        return null;
    }

    /**
     * 转换导入DTO为线索实体
     */
    private Lead convertImportDTOToLead(LeadImportDTO dto, Long campusId) {
        Lead lead = new Lead();

        // 生成线索编号
        lead.setLeadNo(generateLeadNo());

        // 基本信息
        lead.setName(dto.getName());
        lead.setPhone(dto.getPhone());

        // 性别转换
        if (StrUtil.isNotBlank(dto.getGender())) {
            if ("男".equals(dto.getGender())) {
                lead.setGender(1);
            } else if ("女".equals(dto.getGender())) {
                lead.setGender(2);
            } else {
                lead.setGender(0);
            }
        } else {
            lead.setGender(0);
        }

        // 年龄
        if (StrUtil.isNotBlank(dto.getAge())) {
            try {
                lead.setAge(Integer.parseInt(dto.getAge()));
            } catch (NumberFormatException e) {
                // 忽略
            }
        }

        // 来源
        if (StrUtil.isNotBlank(dto.getSource())) {
            lead.setSource(dto.getSource().toLowerCase());
        } else {
            lead.setSource("offline"); // 默认地推
        }

        lead.setSourceDetail(dto.getSourceDetail());

        // 意向程度
        if (StrUtil.isNotBlank(dto.getIntentLevel())) {
            lead.setIntentLevel(dto.getIntentLevel().toLowerCase());
        } else {
            lead.setIntentLevel("medium"); // 默认中等意向
        }

        // 学校和年级
        lead.setSchool(dto.getSchool());
        lead.setGrade(dto.getGrade());

        // 备注
        lead.setRemark(dto.getRemark());

        // 状态和校区
        lead.setStatus("new");
        lead.setCampusId(campusId);
        lead.setFollowCount(0);

        return lead;
    }

    @Override
    public byte[] downloadImportTemplate() {
        try {
            // 创建模板数据（空数据，只有表头）
            List<LeadImportDTO> templateData = new ArrayList<>();

            // 使用 EasyExcel 生成模板
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            EasyExcel.write(out, LeadImportDTO.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("线索导入模板")
                    .doWrite(templateData);

            return out.toByteArray();
        } catch (Exception e) {
            log.error("生成导入模板失败", e);
            throw new BusinessException("生成模板失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoAssignLeads(List<Long> leadIds, Long campusId) {
        if (CollUtil.isEmpty(leadIds)) {
            throw new BusinessException("线索ID列表不能为空");
        }
        if (campusId == null) {
            throw new BusinessException("校区ID不能为空");
        }

        // 查询该校区所有顾问的线索数量
        // 使用简单的轮询策略：查询每个顾问当前的线索数量，分配给数量最少的顾问
        List<Map<String, Object>> advisorLeadCounts = baseMapper.selectAdvisorLeadCounts(campusId);

        if (CollUtil.isEmpty(advisorLeadCounts)) {
            throw new BusinessException("该校区暂无可分配的顾问");
        }

        // 按照线索数量排序，优先分配给线索数量少的顾问
        advisorLeadCounts.sort((a, b) -> {
            Long countA = (Long) a.get("leadCount");
            Long countB = (Long) b.get("leadCount");
            return countA.compareTo(countB);
        });

        // 轮询分配
        int advisorIndex = 0;
        for (Long leadId : leadIds) {
            Long advisorId = (Long) advisorLeadCounts.get(advisorIndex).get("advisorId");
            assignLead(leadId, advisorId);

            // 更新该顾问的线索数量
            Long currentCount = (Long) advisorLeadCounts.get(advisorIndex).get("leadCount");
            advisorLeadCounts.get(advisorIndex).put("leadCount", currentCount + 1);

            // 移动到下一个顾问
            advisorIndex = (advisorIndex + 1) % advisorLeadCounts.size();
        }

        return true;
    }
}
