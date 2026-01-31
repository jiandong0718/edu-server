package com.edu.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.finance.domain.entity.ContractPrintTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 合同打印模板Mapper
 */
@Mapper
public interface ContractPrintTemplateMapper extends BaseMapper<ContractPrintTemplate> {

    /**
     * 查询默认模板
     *
     * @return 默认模板
     */
    ContractPrintTemplate selectDefaultTemplate();
}
