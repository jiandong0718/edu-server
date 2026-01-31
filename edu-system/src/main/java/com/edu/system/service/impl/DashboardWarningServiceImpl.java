package com.edu.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.system.domain.dto.WarningConfigDTO;
import com.edu.system.domain.entity.WarningConfig;
import com.edu.system.domain.vo.WarningVO;
import com.edu.system.mapper.DashboardWarningMapper;
import com.edu.system.service.DashboardWarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据预警服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardWarningServiceImpl extends ServiceImpl<DashboardWarningMapper, WarningConfig>
        implements DashboardWarningService {

    private final DashboardWarningMapper dashboardWarningMapper;

    @Override
    public Page<WarningVO> getWarningList(Integer pageNum, Integer pageSize, Long campusId,
                                          String warningType, String warningLevel) {
        // 获取所有预警数据
        List<WarningVO> allWarnings = getAllWarnings(campusId);

        // 过滤
        List<WarningVO> filteredWarnings = allWarnings.stream()
                .filter(w -> warningType == null || warningType.equals(w.getWarningType()))
                .filter(w -> warningLevel == null || warningLevel.equals(w.getWarningLevel()))
                .collect(Collectors.toList());

        // 手动分页
        int total = filteredWarnings.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<WarningVO> pageData = start < total ? filteredWarnings.subList(start, end) : new ArrayList<>();

        Page<WarningVO> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(pageData);

        return page;
    }

    @Override
    public WarningVO.WarningSummary getWarningSummary(Long campusId) {
        List<WarningVO> allWarnings = getAllWarnings(campusId);

        // 统计各级别预警数
        int urgentCount = (int) allWarnings.stream()
                .filter(w -> "urgent".equals(w.getWarningLevel())).count();
        int warningCount = (int) allWarnings.stream()
                .filter(w -> "warning".equals(w.getWarningLevel())).count();
        int normalCount = (int) allWarnings.stream()
                .filter(w -> "normal".equals(w.getWarningLevel())).count();

        // 统计各类别预警数
        Set<String> businessTypes = Set.of("course_hour_low", "course_hour_expire", "overdue",
                "contract_expire", "student_loss");
        Set<String> operationTypes = Set.of("class_full", "schedule_conflict", "classroom_conflict",
                "trial_conversion_low");
        Set<String> financeTypes = Set.of("income_abnormal", "refund_rate_high");

        int businessWarningCount = (int) allWarnings.stream()
                .filter(w -> businessTypes.contains(w.getWarningType())).count();
        int operationWarningCount = (int) allWarnings.stream()
                .filter(w -> operationTypes.contains(w.getWarningType())).count();
        int financeWarningCount = (int) allWarnings.stream()
                .filter(w -> financeTypes.contains(w.getWarningType())).count();

        // 统计各类型分布
        Map<String, List<WarningVO>> typeGroups = allWarnings.stream()
                .collect(Collectors.groupingBy(WarningVO::getWarningType));

        List<WarningVO.WarningTypeDistribution> typeDistribution = typeGroups.entrySet().stream()
                .map(entry -> {
                    String type = entry.getKey();
                    List<WarningVO> warnings = entry.getValue();
                    String level = warnings.isEmpty() ? "normal" : warnings.get(0).getWarningLevel();
                    String name = warnings.isEmpty() ? type : warnings.get(0).getWarningName();

                    return WarningVO.WarningTypeDistribution.builder()
                            .warningType(type)
                            .warningName(name)
                            .count(warnings.size())
                            .warningLevel(level)
                            .build();
                })
                .sorted(Comparator.comparingInt(WarningVO.WarningTypeDistribution::getCount).reversed())
                .collect(Collectors.toList());

        return WarningVO.WarningSummary.builder()
                .totalCount(allWarnings.size())
                .urgentCount(urgentCount)
                .warningCount(warningCount)
                .normalCount(normalCount)
                .businessWarningCount(businessWarningCount)
                .operationWarningCount(operationWarningCount)
                .financeWarningCount(financeWarningCount)
                .typeDistribution(typeDistribution)
                .build();
    }

    @Override
    public Long configWarning(WarningConfigDTO dto) {
        // 验证阈值配置格式
        validateThresholdConfig(dto.getThresholdConfig());

        WarningConfig config = new WarningConfig();
        BeanUtils.copyProperties(dto, config);

        if (!save(config)) {
            throw new BusinessException("配置预警规则失败");
        }

        return config.getId();
    }

    @Override
    public Boolean updateWarningConfig(Long id, WarningConfigDTO dto) {
        WarningConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("预警配置不存在");
        }

        // 验证阈值配置格式
        validateThresholdConfig(dto.getThresholdConfig());

        BeanUtils.copyProperties(dto, config);
        config.setId(id);

        return updateById(config);
    }

    @Override
    public List<WarningConfig> getWarningConfigs(Long campusId) {
        LambdaQueryWrapper<WarningConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(campusId != null, WarningConfig::getCampusId, campusId)
                .or()
                .isNull(WarningConfig::getCampusId)
                .orderByDesc(WarningConfig::getCreateTime);

        return list(wrapper);
    }

    @Override
    public Boolean deleteWarningConfig(Long id) {
        return removeById(id);
    }

    /**
     * 获取所有预警数据
     */
    private List<WarningVO> getAllWarnings(Long campusId) {
        List<WarningVO> allWarnings = new ArrayList<>();

        // 获取启用的预警配置
        Map<String, WarningConfig> configMap = getEnabledConfigs(campusId);

        // 1. 课时不足预警
        if (configMap.containsKey("course_hour_low")) {
            allWarnings.addAll(getCourseHourLowWarnings(campusId, configMap.get("course_hour_low")));
        }

        // 2. 课时即将到期预警
        if (configMap.containsKey("course_hour_expire")) {
            allWarnings.addAll(getCourseHourExpireWarnings(campusId, configMap.get("course_hour_expire")));
        }

        // 3. 欠费预警
        if (configMap.containsKey("overdue")) {
            allWarnings.addAll(getOverdueWarnings(campusId, configMap.get("overdue")));
        }

        // 4. 合同即将到期预警
        if (configMap.containsKey("contract_expire")) {
            allWarnings.addAll(getContractExpireWarnings(campusId, configMap.get("contract_expire")));
        }

        // 5. 学员流失预警
        if (configMap.containsKey("student_loss")) {
            allWarnings.addAll(getStudentLossWarnings(campusId, configMap.get("student_loss")));
        }

        // 6. 班级满员预警
        if (configMap.containsKey("class_full")) {
            allWarnings.addAll(getClassFullWarnings(campusId, configMap.get("class_full")));
        }

        // 7. 教师排课冲突预警
        if (configMap.containsKey("schedule_conflict")) {
            allWarnings.addAll(getTeacherScheduleConflictWarnings(campusId, configMap.get("schedule_conflict")));
        }

        // 8. 教室使用冲突预警
        if (configMap.containsKey("classroom_conflict")) {
            allWarnings.addAll(getClassroomConflictWarnings(campusId, configMap.get("classroom_conflict")));
        }

        // 9. 试听转化率低预警
        if (configMap.containsKey("trial_conversion_low")) {
            allWarnings.addAll(getTrialConversionLowWarnings(campusId, configMap.get("trial_conversion_low")));
        }

        // 10. 收入异常预警
        if (configMap.containsKey("income_abnormal")) {
            allWarnings.addAll(getIncomeAbnormalWarnings(campusId, configMap.get("income_abnormal")));
        }

        // 11. 退费率高预警
        if (configMap.containsKey("refund_rate_high")) {
            allWarnings.addAll(getRefundRateHighWarnings(campusId, configMap.get("refund_rate_high")));
        }

        return allWarnings;
    }

    /**
     * 获取启用的预警配置
     */
    private Map<String, WarningConfig> getEnabledConfigs(Long campusId) {
        LambdaQueryWrapper<WarningConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarningConfig::getEnabled, 1);

        if (campusId != null) {
            wrapper.and(w -> w.eq(WarningConfig::getCampusId, campusId)
                    .or()
                    .isNull(WarningConfig::getCampusId));
        } else {
            wrapper.isNull(WarningConfig::getCampusId);
        }

        List<WarningConfig> configs = list(wrapper);

        return configs.stream()
                .collect(Collectors.toMap(
                        WarningConfig::getWarningType,
                        config -> config,
                        (existing, replacement) -> {
                            // 优先使用校区级配置
                            if (replacement.getCampusId() != null) {
                                return replacement;
                            }
                            return existing;
                        }
                ));
    }

    /**
     * 课时不足预警
     */
    private List<WarningVO> getCourseHourLowWarnings(Long campusId, WarningConfig config) {
        JSONObject thresholdConfig = JSON.parseObject(config.getThresholdConfig());
        Integer threshold = thresholdConfig.getInteger("courseHourThreshold");
        if (threshold == null) {
            threshold = 5; // 默认阈值
        }

        List<WarningVO.CourseHourLowWarning> details =
                dashboardWarningMapper.getCourseHourLowWarnings(campusId, threshold);

        return details.stream()
                .map(detail -> WarningVO.builder()
                        .warningId("course_hour_low_" + detail.getStudentId() + "_" + detail.getCourseId())
                        .warningType("course_hour_low")
                        .warningName("课时不足预警")
                        .warningLevel(config.getWarningLevel())
                        .description(String.format("学员 %s 的 %s 课程剩余课时仅 %.1f 小时，低于阈值 %d 小时",
                                detail.getStudentName(), detail.getCourseName(),
                                detail.getRemainingHours(), threshold))
                        .businessId(detail.getStudentId())
                        .businessName(detail.getStudentName())
                        .campusId(campusId)
                        .campusName(detail.getCampusName())
                        .currentValue(String.format("%.1f小时", detail.getRemainingHours()))
                        .thresholdValue(threshold + "小时")
                        .warningTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 课时即将到期预警
     */
    private List<WarningVO> getCourseHourExpireWarnings(Long campusId, WarningConfig config) {
        JSONObject thresholdConfig = JSON.parseObject(config.getThresholdConfig());
        Integer daysThreshold = thresholdConfig.getInteger("daysThreshold");
        if (daysThreshold == null) {
            daysThreshold = 30; // 默认30天
        }

        List<WarningVO.CourseHourExpireWarning> details =
                dashboardWarningMapper.getCourseHourExpireWarnings(campusId, daysThreshold);

        return details.stream()
                .map(detail -> WarningVO.builder()
                        .warningId("course_hour_expire_" + detail.getStudentId() + "_" + detail.getCourseId())
                        .warningType("course_hour_expire")
                        .warningName("课时即将到期预警")
                        .warningLevel(config.getWarningLevel())
                        .description(String.format("学员 %s 的 %s 课程将在 %d 天后到期（%s）",
                                detail.getStudentName(), detail.getCourseName(),
                                detail.getRemainingDays(), detail.getExpireDate()))
                        .businessId(detail.getStudentId())
                        .businessName(detail.getStudentName())
                        .campusId(campusId)
                        .campusName(detail.getCampusName())
                        .currentValue(detail.getRemainingDays() + "天")
                        .thresholdValue(daysThreshold + "天")
                        .warningTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 欠费预警
     */
    private List<WarningVO> getOverdueWarnings(Long campusId, WarningConfig config) {
        JSONObject thresholdConfig = JSON.parseObject(config.getThresholdConfig());
        Integer daysThreshold = thresholdConfig.getInteger("daysThreshold");
        if (daysThreshold == null) {
            daysThreshold = 7; // 默认7天
        }

        List<WarningVO.OverdueWarning> details =
                dashboardWarningMapper.getOverdueWarnings(campusId, daysThreshold);

        return details.stream()
                .map(detail -> WarningVO.builder()
                        .warningId("overdue_" + detail.getContractId())
                        .warningType("overdue")
                        .warningName("欠费预警")
                        .warningLevel(config.getWarningLevel())
                        .description(String.format("合同 %s（学员：%s）已欠费 %d 天，欠费金额 %.2f 元",
                                detail.getContractNo(), detail.getStudentName(),
                                detail.getOverdueDays(), detail.getOverdueAmount()))
                        .businessId(detail.getContractId())
                        .businessName(detail.getContractNo())
                        .campusId(campusId)
                        .campusName(detail.getCampusName())
                        .currentValue(detail.getOverdueDays() + "天")
                        .thresholdValue(daysThreshold + "天")
                        .warningTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 合同即将到期预警
     */
    private List<WarningVO> getContractExpireWarnings(Long campusId, WarningConfig config) {
        JSONObject thresholdConfig = JSON.parseObject(config.getThresholdConfig());
        Integer daysThreshold = thresholdConfig.getInteger("daysThreshold");
        if (daysThreshold == null) {
            daysThreshold = 30; // 默认30天
        }

        List<WarningVO.ContractExpireWarning> details =
                dashboardWarningMapper.getContractExpireWarnings(campusId, daysThreshold);

        return details.stream()
                .map(detail -> WarningVO.builder()
                        .warningId("contract_expire_" + detail.getContractId())
                        .warningType("contract_expire")
                        .warningName("合同即将到期预警")
                        .warningLevel(config.getWarningLevel())
                        .description(String.format("合同 %s（学员：%s）将在 %d 天后到期（%s）",
                                detail.getContractNo(), detail.getStudentName(),
                                detail.getRemainingDays(), detail.getExpireDate()))
                        .businessId(detail.getContractId())
                        .businessName(detail.getContractNo())
                        .campusId(campusId)
                        .campusName(detail.getCampusName())
                        .currentValue(detail.getRemainingDays() + "天")
                        .thresholdValue(daysThreshold + "天")
                        .warningTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 学员流失预警
     */
    private List<WarningVO> getStudentLossWarnings(Long campusId, WarningConfig config) {
        JSONObject thresholdConfig = JSON.parseObject(config.getThresholdConfig());
        Integer daysThreshold = thresholdConfig.getInteger("daysThreshold");
        if (daysThreshold == null) {
            daysThreshold = 30; // 默认30天
        }

        List<WarningVO.StudentLossWarning> details =
                dashboardWarningMapper.getStudentLossWarnings(campusId, daysThreshold);

        return details.stream()
                .map(detail -> WarningVO.builder()
                        .warningId("student_loss_" + detail.getStudentId())
                        .warningType("student_loss")
                        .warningName("学员流失预警")
                        .warningLevel(config.getWarningLevel())
                        .description(String.format("学员 %s 已 %d 天未上课，最后上课日期：%s",
                                detail.getStudentName(), detail.getNoAttendanceDays(),
                                detail.getLastAttendanceDate()))
                        .businessId(detail.getStudentId())
                        .businessName(detail.getStudentName())
                        .campusId(campusId)
                        .campusName(detail.getCampusName())
                        .currentValue(detail.getNoAttendanceDays() + "天")
                        .thresholdValue(daysThreshold + "天")
                        .warningTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 班级满员预警
     */
    private List<WarningVO> getClassFullWarnings(Long campusId, WarningConfig config) {
        List<WarningVO> warnings = dashboardWarningMapper.getClassFullWarnings(campusId);
        warnings.forEach(w -> {
            w.setWarningLevel(config.getWarningLevel());
            w.setWarningTime(LocalDateTime.now());
        });
        return warnings;
    }

    /**
     * 教师排课冲突预警
     */
    private List<WarningVO> getTeacherScheduleConflictWarnings(Long campusId, WarningConfig config) {
        List<WarningVO> warnings = dashboardWarningMapper.getTeacherScheduleConflictWarnings(campusId);
        warnings.forEach(w -> {
            w.setWarningLevel(config.getWarningLevel());
            w.setWarningTime(LocalDateTime.now());
        });
        return warnings;
    }

    /**
     * 教室使用冲突预警
     */
    private List<WarningVO> getClassroomConflictWarnings(Long campusId, WarningConfig config) {
        List<WarningVO> warnings = dashboardWarningMapper.getClassroomConflictWarnings(campusId);
        warnings.forEach(w -> {
            w.setWarningLevel(config.getWarningLevel());
            w.setWarningTime(LocalDateTime.now());
        });
        return warnings;
    }

    /**
     * 试听转化率低预警
     */
    private List<WarningVO> getTrialConversionLowWarnings(Long campusId, WarningConfig config) {
        JSONObject thresholdConfig = JSON.parseObject(config.getThresholdConfig());
        Double rateThreshold = thresholdConfig.getDouble("rateThreshold");
        if (rateThreshold == null) {
            rateThreshold = 0.3; // 默认30%
        }

        List<WarningVO> warnings = dashboardWarningMapper.getTrialConversionLowWarnings(campusId, rateThreshold);
        warnings.forEach(w -> {
            w.setWarningLevel(config.getWarningLevel());
            w.setWarningTime(LocalDateTime.now());
        });
        return warnings;
    }

    /**
     * 收入异常预警
     */
    private List<WarningVO> getIncomeAbnormalWarnings(Long campusId, WarningConfig config) {
        JSONObject thresholdConfig = JSON.parseObject(config.getThresholdConfig());
        Double rateThreshold = thresholdConfig.getDouble("rateThreshold");
        if (rateThreshold == null) {
            rateThreshold = 0.8; // 默认80%
        }

        List<WarningVO> warnings = dashboardWarningMapper.getIncomeAbnormalWarnings(campusId, rateThreshold);
        warnings.forEach(w -> {
            w.setWarningLevel(config.getWarningLevel());
            w.setWarningTime(LocalDateTime.now());
        });
        return warnings;
    }

    /**
     * 退费率高预警
     */
    private List<WarningVO> getRefundRateHighWarnings(Long campusId, WarningConfig config) {
        JSONObject thresholdConfig = JSON.parseObject(config.getThresholdConfig());
        Double rateThreshold = thresholdConfig.getDouble("rateThreshold");
        if (rateThreshold == null) {
            rateThreshold = 0.1; // 默认10%
        }

        List<WarningVO> warnings = dashboardWarningMapper.getRefundRateHighWarnings(campusId, rateThreshold);
        warnings.forEach(w -> {
            w.setWarningLevel(config.getWarningLevel());
            w.setWarningTime(LocalDateTime.now());
        });
        return warnings;
    }

    /**
     * 验证阈值配置格式
     */
    private void validateThresholdConfig(String thresholdConfig) {
        try {
            JSON.parseObject(thresholdConfig);
        } catch (Exception e) {
            throw new BusinessException("阈值配置格式错误，必须是有效的JSON格式");
        }
    }
}
