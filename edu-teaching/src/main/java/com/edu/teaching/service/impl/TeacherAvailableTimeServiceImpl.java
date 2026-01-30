package com.edu.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.teaching.domain.entity.TeacherAvailableTime;
import com.edu.teaching.mapper.TeacherAvailableTimeMapper;
import com.edu.teaching.service.TeacherAvailableTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教师可用时间配置服务实现
 */
@Service
@RequiredArgsConstructor
public class TeacherAvailableTimeServiceImpl extends ServiceImpl<TeacherAvailableTimeMapper, TeacherAvailableTime> implements TeacherAvailableTimeService {

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
}
