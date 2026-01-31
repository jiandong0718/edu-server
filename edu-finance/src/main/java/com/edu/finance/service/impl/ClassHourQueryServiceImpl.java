package com.edu.finance.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.finance.domain.dto.ClassHourBalanceQueryDTO;
import com.edu.finance.domain.vo.ClassHourBalanceVO;
import com.edu.finance.mapper.ClassHourAccountMapper;
import com.edu.finance.service.ClassHourQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 课时查询服务实现
 */
@Service
@RequiredArgsConstructor
public class ClassHourQueryServiceImpl implements ClassHourQueryService {

    private final ClassHourAccountMapper classHourAccountMapper;

    @Override
    public ClassHourBalanceVO getBalanceByStudent(Long studentId) {
        return classHourAccountMapper.getBalanceByStudent(studentId);
    }

    @Override
    public ClassHourBalanceVO getBalanceDetail(Long accountId) {
        return classHourAccountMapper.getBalanceDetail(accountId);
    }

    @Override
    public IPage<ClassHourBalanceVO> pageBalance(Page<ClassHourBalanceVO> page, ClassHourBalanceQueryDTO query) {
        return classHourAccountMapper.pageBalance(page, query);
    }
}
