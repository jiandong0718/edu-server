package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.BatchSaveAvailableTimeDTO;
import com.edu.teaching.domain.dto.TeacherAvailableTimeDTO;
import com.edu.teaching.domain.entity.TeacherAvailableTime;
import com.edu.teaching.domain.vo.TeacherAvailableTimeVO;

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
     * 获取教师的可用时间列表（VO）
     */
    List<TeacherAvailableTimeVO> getByTeacherIdVO(Long teacherId);

    /**
     * 批量保存教师可用时间（旧版本，保持兼容）
     */
    boolean batchSave(Long teacherId, List<TeacherAvailableTime> timeList);

    /**
     * 批量保存教师可用时间（新版本，带验证）
     */
    boolean batchSaveWithValidation(BatchSaveAvailableTimeDTO dto);

    /**
     * 新增可用时间（带验证）
     */
    boolean addWithValidation(TeacherAvailableTimeDTO dto);

    /**
     * 修改可用时间（带验证）
     */
    boolean updateWithValidation(TeacherAvailableTimeDTO dto);

    /**
     * 验证时间段是否有效
     */
    void validateTimeSlot(Integer dayOfWeek, String startTime, String endTime);

    /**
     * 检查时间段是否冲突
     */
    boolean checkTimeConflict(Long teacherId, Integer dayOfWeek, String startTime, String endTime, Long excludeId);

    /**
     * 检查教师在指定时间是否可用
     */
    boolean isTeacherAvailable(Long teacherId, Integer dayOfWeek, String startTime, String endTime);
}
