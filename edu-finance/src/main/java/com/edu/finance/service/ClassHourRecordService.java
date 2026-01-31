package com.edu.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.entity.ClassHourRecord;

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
}
