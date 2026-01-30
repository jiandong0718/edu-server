package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.entity.TeacherAvailableTime;

import java.util.List;

/**
 * 教师可用时间配置服务接口
 */
public interface TeacherAvailableTimeService extends IService<TeacherAvailableTime> {

    /**
     * 获取教师的可用时间列表
     */
    List<TeacherAvailableTime> getByTeacherId(Long teacherId);

    /**
     * 批量保存教师可用时间
     */
    boolean batchSave(Long teacherId, List<TeacherAvailableTime> timeList);
}
