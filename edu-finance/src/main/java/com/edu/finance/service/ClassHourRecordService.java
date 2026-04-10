package com.edu.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.entity.ClassHourRecord;
import com.edu.finance.domain.vo.ClassHourRecordVO;

import java.util.List;

/**
 * 课时消耗记录服务接口
 */
public interface ClassHourRecordService extends IService<ClassHourRecord> {

    /**
     * 查询学员的课时消耗记录
     *
     * @param studentId 学员ID
     * @param accountId 账户ID（可选）
     * @return 课时消耗记录列表
     */
    List<ClassHourRecord> getByStudentId(Long studentId, Long accountId);

    /**
     * 分页查询课时记录（含学员、课程、操作人等扩展信息）
     *
     * @param page 分页参数
     * @param studentId 学员ID（可选）
     * @param accountId 账户ID（可选）
     * @return 分页记录
     */
    Page<ClassHourRecordVO> pageRecords(Page<ClassHourRecordVO> page, Long studentId, Long accountId);
}
