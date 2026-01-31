package com.edu.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.dto.WarningConfigDTO;
import com.edu.system.domain.entity.WarningConfig;
import com.edu.system.domain.vo.WarningVO;

import java.util.List;

/**
 * 数据预警服务接口
 */
public interface DashboardWarningService extends IService<WarningConfig> {

    /**
     * 获取预警列表（分页）
     *
     * @param pageNum      页码
     * @param pageSize     每页大小
     * @param campusId     校区ID
     * @param warningType  预警类型
     * @param warningLevel 预警级别
     * @return 预警列表
     */
    Page<WarningVO> getWarningList(Integer pageNum, Integer pageSize, Long campusId,
                                    String warningType, String warningLevel);

    /**
     * 获取预警汇总
     *
     * @param campusId 校区ID
     * @return 预警汇总
     */
    WarningVO.WarningSummary getWarningSummary(Long campusId);

    /**
     * 配置预警规则
     *
     * @param dto 预警配置DTO
     * @return 配置ID
     */
    Long configWarning(WarningConfigDTO dto);

    /**
     * 更新预警规则
     *
     * @param id  配置ID
     * @param dto 预警配置DTO
     * @return 是否成功
     */
    Boolean updateWarningConfig(Long id, WarningConfigDTO dto);

    /**
     * 获取预警配置列表
     *
     * @param campusId 校区ID
     * @return 配置列表
     */
    List<WarningConfig> getWarningConfigs(Long campusId);

    /**
     * 删除预警配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    Boolean deleteWarningConfig(Long id);
}
