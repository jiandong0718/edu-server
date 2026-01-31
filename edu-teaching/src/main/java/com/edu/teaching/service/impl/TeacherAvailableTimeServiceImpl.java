package com.edu.teaching.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.teaching.domain.dto.BatchSaveAvailableTimeDTO;
import com.edu.teaching.domain.dto.TeacherAvailableTimeDTO;
import com.edu.teaching.domain.entity.TeacherAvailableTime;
import com.edu.teaching.domain.vo.TeacherAvailableTimeVO;
import com.edu.teaching.mapper.TeacherAvailableTimeMapper;
import com.edu.teaching.service.TeacherAvailableTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师可用时间配置服务实现
 */
@Service
@RequiredArgsConstructor
public class TeacherAvailableTimeServiceImpl extends ServiceImpl<TeacherAvailableTimeMapper, TeacherAvailableTime> implements TeacherAvailableTimeService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] DAY_NAMES = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Override
    public List<TeacherAvailableTime> getByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherAvailableTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherAvailableTime::getTeacherId, teacherId)
                .eq(TeacherAvailableTime::getStatus, 1)
                .orderByAsc(TeacherAvailableTime::getDayOfWeek)
                .orderByAsc(TeacherAvailableTime::getStartTime);
        return list(wrapper);
    }

    @Override
    public List<TeacherAvailableTimeVO> getByTeacherIdVO(Long teacherId) {
        List<TeacherAvailableTime> list = getByTeacherId(teacherId);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSave(Long teacherId, List<TeacherAvailableTime> timeList) {
        // 先删除该教师的所有可用时间配置
        LambdaQueryWrapper<TeacherAvailableTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherAvailableTime::getTeacherId, teacherId);
        remove(wrapper);

        // 批量保存新的配置
        if (timeList != null && !timeList.isEmpty()) {
            timeList.forEach(time -> time.setTeacherId(teacherId));
            return saveBatch(timeList);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveWithValidation(BatchSaveAvailableTimeDTO dto) {
        Long teacherId = dto.getTeacherId();
        List<BatchSaveAvailableTimeDTO.TimeSlotDTO> timeSlots = dto.getTimeSlots();

        // 验证每个时间段
        for (BatchSaveAvailableTimeDTO.TimeSlotDTO slot : timeSlots) {
            validateTimeSlot(slot.getDayOfWeek(), slot.getStartTime(), slot.getEndTime());
        }

        // 检查时间段之间是否有冲突
        for (int i = 0; i < timeSlots.size(); i++) {
            BatchSaveAvailableTimeDTO.TimeSlotDTO slot1 = timeSlots.get(i);
            for (int j = i + 1; j < timeSlots.size(); j++) {
                BatchSaveAvailableTimeDTO.TimeSlotDTO slot2 = timeSlots.get(j);
                // 同一天的时间段检查冲突
                if (slot1.getDayOfWeek().equals(slot2.getDayOfWeek())) {
                    if (isTimeOverlap(slot1.getStartTime(), slot1.getEndTime(),
                                     slot2.getStartTime(), slot2.getEndTime())) {
                        throw new BusinessException(String.format("%s的时间段存在冲突：%s-%s 与 %s-%s",
                            DAY_NAMES[slot1.getDayOfWeek()],
                            slot1.getStartTime(), slot1.getEndTime(),
                            slot2.getStartTime(), slot2.getEndTime()));
                    }
                }
            }
        }

        // 先删除该教师的所有可用时间配置
        LambdaQueryWrapper<TeacherAvailableTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherAvailableTime::getTeacherId, teacherId);
        remove(wrapper);

        // 批量保存新的配置
        List<TeacherAvailableTime> timeList = new ArrayList<>();
        for (BatchSaveAvailableTimeDTO.TimeSlotDTO slot : timeSlots) {
            TeacherAvailableTime time = new TeacherAvailableTime();
            time.setTeacherId(teacherId);
            time.setDayOfWeek(slot.getDayOfWeek());
            time.setStartTime(slot.getStartTime());
            time.setEndTime(slot.getEndTime());
            time.setStatus(1); // 默认启用
            time.setRemark(slot.getRemark());
            timeList.add(time);
        }

        return saveBatch(timeList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addWithValidation(TeacherAvailableTimeDTO dto) {
        // 验证时间段
        validateTimeSlot(dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());

        // 检查时间冲突
        if (checkTimeConflict(dto.getTeacherId(), dto.getDayOfWeek(),
                             dto.getStartTime(), dto.getEndTime(), null)) {
            throw new BusinessException(String.format("%s的时间段%s-%s与已有时间段冲突",
                DAY_NAMES[dto.getDayOfWeek()], dto.getStartTime(), dto.getEndTime()));
        }

        // 转换并保存
        TeacherAvailableTime entity = BeanUtil.copyProperties(dto, TeacherAvailableTime.class);
        if (entity.getStatus() == null) {
            entity.setStatus(1); // 默认启用
        }
        return save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithValidation(TeacherAvailableTimeDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("可用时间ID不能为空");
        }

        // 验证时间段
        validateTimeSlot(dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());

        // 检查时间冲突（排除自己）
        if (checkTimeConflict(dto.getTeacherId(), dto.getDayOfWeek(),
                             dto.getStartTime(), dto.getEndTime(), dto.getId())) {
            throw new BusinessException(String.format("%s的时间段%s-%s与已有时间段冲突",
                DAY_NAMES[dto.getDayOfWeek()], dto.getStartTime(), dto.getEndTime()));
        }

        // 转换并更新
        TeacherAvailableTime entity = BeanUtil.copyProperties(dto, TeacherAvailableTime.class);
        return updateById(entity);
    }

    @Override
    public void validateTimeSlot(Integer dayOfWeek, String startTime, String endTime) {
        // 验证星期几
        if (dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7) {
            throw new BusinessException("星期几必须在1-7之间");
        }

        // 验证时间格式
        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(startTime, TIME_FORMATTER);
            end = LocalTime.parse(endTime, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException("时间格式错误，必须为HH:mm格式（如：09:00）");
        }

        // 验证开始时间必须小于结束时间
        if (!start.isBefore(end)) {
            throw new BusinessException("开始时间必须小于结束时间");
        }
    }

    @Override
    public boolean checkTimeConflict(Long teacherId, Integer dayOfWeek,
                                    String startTime, String endTime, Long excludeId) {
        LambdaQueryWrapper<TeacherAvailableTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherAvailableTime::getTeacherId, teacherId)
                .eq(TeacherAvailableTime::getDayOfWeek, dayOfWeek)
                .eq(TeacherAvailableTime::getStatus, 1);

        if (excludeId != null) {
            wrapper.ne(TeacherAvailableTime::getId, excludeId);
        }

        List<TeacherAvailableTime> existingTimes = list(wrapper);

        // 检查是否与已有时间段重叠
        for (TeacherAvailableTime existing : existingTimes) {
            if (isTimeOverlap(startTime, endTime, existing.getStartTime(), existing.getEndTime())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isTeacherAvailable(Long teacherId, Integer dayOfWeek,
                                     String startTime, String endTime) {
        List<TeacherAvailableTime> availableTimes = getByTeacherId(teacherId);

        // 检查是否有可用时间段完全包含指定时间段
        for (TeacherAvailableTime availableTime : availableTimes) {
            if (availableTime.getDayOfWeek().equals(dayOfWeek)) {
                LocalTime availStart = LocalTime.parse(availableTime.getStartTime(), TIME_FORMATTER);
                LocalTime availEnd = LocalTime.parse(availableTime.getEndTime(), TIME_FORMATTER);
                LocalTime reqStart = LocalTime.parse(startTime, TIME_FORMATTER);
                LocalTime reqEnd = LocalTime.parse(endTime, TIME_FORMATTER);

                // 可用时间段完全包含请求时间段
                if (!availStart.isAfter(reqStart) && !availEnd.isBefore(reqEnd)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检查两个时间段是否重叠
     */
    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        LocalTime s1 = LocalTime.parse(start1, TIME_FORMATTER);
        LocalTime e1 = LocalTime.parse(end1, TIME_FORMATTER);
        LocalTime s2 = LocalTime.parse(start2, TIME_FORMATTER);
        LocalTime e2 = LocalTime.parse(end2, TIME_FORMATTER);

        // 两个时间段重叠的条件：start1 < end2 && start2 < end1
        return s1.isBefore(e2) && s2.isBefore(e1);
    }

    /**
     * 转换为VO
     */
    private TeacherAvailableTimeVO convertToVO(TeacherAvailableTime entity) {
        TeacherAvailableTimeVO vo = BeanUtil.copyProperties(entity, TeacherAvailableTimeVO.class);

        // 设置星期几名称
        if (entity.getDayOfWeek() != null && entity.getDayOfWeek() >= 1 && entity.getDayOfWeek() <= 7) {
            vo.setDayOfWeekName(DAY_NAMES[entity.getDayOfWeek()]);
        }

        // 设置状态名称
        vo.setStatusName(entity.getStatus() == 1 ? "启用" : "禁用");

        return vo;
    }
}
