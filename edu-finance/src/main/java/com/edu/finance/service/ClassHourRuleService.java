package com.edu.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.dto.ClassHourRuleCreateDTO;
import com.edu.finance.domain.dto.ClassHourRuleQueryDTO;
import com.edu.finance.domain.dto.ClassHourRuleUpdateDTO;
import com.edu.finance.domain.entity.ClassHourRule;
import com.edu.finance.domain.vo.ClassHourRuleVO;

import java.math.BigDecimal;

/**
 * 课时消课规则服务接口
 */
public interface ClassHourRuleService extends IService<ClassHourRule> {

    /**
     * 根据课程和班级类型获取消课规则
     *
     * @param courseId  课程ID
     * @param classType 班级类型
     * @param campusId  校区ID
     * @return 消课规则
     */
    ClassHourRule getRule(Long courseId, String classType, Long campusId);

    /**
     * 计算应扣减的课时数
     *
     * @param courseId   课程ID
     * @param classType  班级类型
     * @param campusId   校区ID
     * @param classHours 实际课时数
     * @return 应扣减的课时数
     */
    BigDecimal calculateDeductHours(Long courseId, String classType, Long campusId, Integer classHours);

    /**
     * 创建消课规则
     *
     * @param dto 创建DTO
     * @return 是否成功
     */
    boolean createRule(ClassHourRuleCreateDTO dto);

    /**
     * 更新消课规则
     *
     * @param dto 更新DTO
     * @return 是否成功
     */
    boolean updateRule(ClassHourRuleUpdateDTO dto);

    /**
     * 分页查询消课规则
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<ClassHourRuleVO> pageQuery(Page<ClassHourRule> page, ClassHourRuleQueryDTO query);

    /**
     * 根据ID查询规则详情
     *
     * @param id 规则ID
     * @return 规则详情
     */
    ClassHourRuleVO getDetailById(Long id);

    /**
     * 启用规则
     *
     * @param id 规则ID
     * @return 是否成功
     */
    boolean enableRule(Long id);

    /**
     * 停用规则
     *
     * @param id 规则ID
     * @return 是否成功
     */
    boolean disableRule(Long id);
}
