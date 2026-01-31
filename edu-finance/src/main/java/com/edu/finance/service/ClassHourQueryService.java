package com.edu.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.finance.domain.dto.ClassHourBalanceQueryDTO;
import com.edu.finance.domain.vo.ClassHourBalanceVO;

/**
 * 课时查询服务接口
 */
public interface ClassHourQueryService {

    /**
     * 查询学员课时余额
     *
     * @param studentId 学员ID
     * @return 课时余额列表
     */
    ClassHourBalanceVO getBalanceByStudent(Long studentId);

    /**
     * 查询账户详情
     *
     * @param accountId 账户ID
     * @return 账户详情
     */
    ClassHourBalanceVO getBalanceDetail(Long accountId);

    /**
     * 分页查询课时账户
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<ClassHourBalanceVO> pageBalance(Page<ClassHourBalanceVO> page, ClassHourBalanceQueryDTO query);
}
