package com.edu.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.system.domain.dto.LoginLogQueryDTO;
import com.edu.system.domain.entity.SysLoginLog;
import com.edu.system.domain.vo.LoginLogVO;
import com.edu.system.domain.vo.LoginStatisticsVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录日志服务接口
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    /**
     * 记录登录日志
     */
    void recordLoginLog(String username, Long userId, String ip, Integer status, String msg);

    /**
     * 记录登录日志（带 User-Agent 解析）
     */
    void recordLoginLog(String username, Long userId, HttpServletRequest request, Integer status, String msg);

    /**
     * 分页查询登录日志
     */
    Page<LoginLogVO> pageLoginLogs(LoginLogQueryDTO queryDTO);

    /**
     * 获取登录统计信息
     */
    LoginStatisticsVO getLoginStatistics();

    /**
     * 清空登录日志（保留最近N天）
     */
    boolean clearLoginLogs(Integer days);
}
