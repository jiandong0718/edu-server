package com.edu.teaching.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.teaching.domain.dto.TeacherStatusChangeDTO;
import com.edu.teaching.domain.entity.TeacherStatusLog;
import com.edu.teaching.domain.vo.TeacherStatusLogVO;

import java.util.List;

/**
 * 教师状态管理服务接口
 */
public interface TeacherStatusService extends IService<TeacherStatusLog> {

    /**
     * 变更教师状态
     *
     * @param dto 状态变更DTO
     * @return 是否成功
     */
    boolean changeStatus(TeacherStatusChangeDTO dto);

    /**
     * 查询教师当前状态
     *
     * @param teacherId 教师ID
     * @return 当前状态代码
     */
    String getCurrentStatus(Long teacherId);

    /**
     * 分页查询教师状态变更历史
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param teacherId 教师ID（可选）
     * @param teacherName 教师姓名（可选）
     * @param status 状态（可选）
     * @param campusId 校区ID（可选）
     * @return 分页结果
     */
    Page<TeacherStatusLogVO> pageStatusLog(Integer pageNum, Integer pageSize,
                                           Long teacherId, String teacherName,
                                           String status, Long campusId);

    /**
     * 查询教师状态变更历史列表
     *
     * @param teacherId 教师ID
     * @return 状态变更历史列表
     */
    List<TeacherStatusLogVO> getStatusLogList(Long teacherId);

    /**
     * 获取教师最新的状态变更记录
     *
     * @param teacherId 教师ID
     * @return 最新状态变更记录
     */
    TeacherStatusLogVO getLatestStatusLog(Long teacherId);

    /**
     * 批量查询教师状态
     *
     * @param teacherIds 教师ID列表
     * @return 教师ID与状态的映射
     */
    java.util.Map<Long, String> batchGetStatus(List<Long> teacherIds);
}
