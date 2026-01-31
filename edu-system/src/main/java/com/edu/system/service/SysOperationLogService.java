package com.edu.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.dto.OperationLogQueryDTO;
import com.edu.system.domain.entity.SysOperationLog;
import com.edu.system.domain.vo.OperationLogVO;

/**
 * 操作日志服务接口
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    /**
     * 分页查询操作日志
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<OperationLogVO> pageQuery(OperationLogQueryDTO queryDTO);

    /**
     * 获取操作日志详情
     *
     * @param id 日志ID
     * @return 日志详情
     */
    OperationLogVO getDetail(Long id);

    /**
     * 清空操作日志（保留最近N天）
     *
     * @param days 保留天数
     * @return 是否成功
     */
    Boolean cleanLogs(Integer days);
}
