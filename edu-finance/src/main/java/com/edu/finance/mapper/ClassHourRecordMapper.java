package com.edu.finance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.finance.domain.entity.ClassHourRecord;
import com.edu.finance.domain.vo.ClassHourRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课时消耗记录 Mapper
 */
@DS("finance")
public interface ClassHourRecordMapper extends BaseMapper<ClassHourRecord> {

    /**
     * 查询学员的课时消耗记录
     *
     * @param studentId 学员ID
     * @param accountId 账户ID（可选）
     * @return 课时消耗记录列表
     */
    List<ClassHourRecord> selectByStudentId(@Param("studentId") Long studentId,
                                            @Param("accountId") Long accountId);

    /**
     * 分页查询课时记录（含学员、课程、操作人）
     *
     * @param page 分页
     * @param studentId 学员ID（可选）
     * @param accountId 账户ID（可选）
     * @return 分页结果
     */
    IPage<ClassHourRecordVO> selectRecordPage(Page<ClassHourRecordVO> page,
                                              @Param("studentId") Long studentId,
                                              @Param("accountId") Long accountId);
}
