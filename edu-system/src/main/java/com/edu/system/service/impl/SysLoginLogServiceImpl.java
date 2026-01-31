package com.edu.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.system.domain.dto.LoginLogQueryDTO;
import com.edu.system.domain.entity.SysLoginLog;
import com.edu.system.domain.vo.LoginLogVO;
import com.edu.system.domain.vo.LoginStatisticsVO;
import com.edu.system.mapper.SysLoginLogMapper;
import com.edu.system.service.SysLoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志服务实现
 */
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    @Override
    public void recordLoginLog(String username, Long userId, String ip, Integer status, String msg) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUsername(username);
        loginLog.setUserId(userId);
        loginLog.setIp(ip);
        loginLog.setStatus(status);
        loginLog.setMsg(msg);
        loginLog.setLoginTime(LocalDateTime.now());

        // 解析 User-Agent（如果需要可以从 request 中获取）
        // 这里暂时设置为空，后续可以在 Controller 中传入
        loginLog.setBrowser("");
        loginLog.setOs("");
        loginLog.setLocation("");

        save(loginLog);
    }

    @Override
    public void recordLoginLog(String username, Long userId, HttpServletRequest request, Integer status, String msg) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUsername(username);
        loginLog.setUserId(userId);
        loginLog.setIp(getIpAddress(request));
        loginLog.setStatus(status);
        loginLog.setMsg(msg);
        loginLog.setLoginTime(LocalDateTime.now());

        // 解析 User-Agent
        String userAgentStr = request.getHeader("User-Agent");
        if (StrUtil.isNotBlank(userAgentStr)) {
            UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
            loginLog.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
            loginLog.setOs(userAgent.getOs().getName());
        } else {
            loginLog.setBrowser("");
            loginLog.setOs("");
        }

        // 解析 IP 地址位置（这里简化处理，实际可以使用 ip2region 等库）
        loginLog.setLocation(parseIpLocation(loginLog.getIp()));

        save(loginLog);
    }

    @Override
    public Page<LoginLogVO> pageLoginLogs(LoginLogQueryDTO queryDTO) {
        Page<SysLoginLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StrUtil.isNotBlank(queryDTO.getUsername()), SysLoginLog::getUsername, queryDTO.getUsername())
                .eq(queryDTO.getStatus() != null, SysLoginLog::getStatus, queryDTO.getStatus())
                .like(StrUtil.isNotBlank(queryDTO.getIp()), SysLoginLog::getIp, queryDTO.getIp())
                .like(StrUtil.isNotBlank(queryDTO.getLocation()), SysLoginLog::getLocation, queryDTO.getLocation())
                .ge(queryDTO.getStartTime() != null, SysLoginLog::getLoginTime, queryDTO.getStartTime())
                .le(queryDTO.getEndTime() != null, SysLoginLog::getLoginTime, queryDTO.getEndTime())
                .orderByDesc(SysLoginLog::getLoginTime);

        Page<SysLoginLog> result = page(page, wrapper);

        // 转换为 VO
        Page<LoginLogVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<LoginLogVO> voList = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public LoginStatisticsVO getLoginStatistics() {
        // 今日开始时间
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 今日登录统计
        LambdaQueryWrapper<SysLoginLog> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(SysLoginLog::getLoginTime, todayStart)
                .le(SysLoginLog::getLoginTime, todayEnd);
        List<SysLoginLog> todayLogs = list(todayWrapper);

        long todayLoginCount = todayLogs.size();
        long todaySuccessCount = todayLogs.stream().filter(log -> log.getStatus() == 1).count();
        long todayFailureCount = todayLogs.stream().filter(log -> log.getStatus() == 0).count();
        double todaySuccessRate = todayLoginCount > 0 ? (todaySuccessCount * 100.0 / todayLoginCount) : 0.0;

        // 异常IP统计（今日失败次数 >= 3 的IP）
        long abnormalIpCount = todayLogs.stream()
                .filter(log -> log.getStatus() == 0)
                .collect(Collectors.groupingBy(SysLoginLog::getIp, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= 3)
                .count();

        // 总体统计
        long totalLoginCount = count();
        LambdaQueryWrapper<SysLoginLog> successWrapper = new LambdaQueryWrapper<>();
        successWrapper.eq(SysLoginLog::getStatus, 1);
        long totalSuccessCount = count(successWrapper);
        long totalFailureCount = totalLoginCount - totalSuccessCount;
        double totalSuccessRate = totalLoginCount > 0 ? (totalSuccessCount * 100.0 / totalLoginCount) : 0.0;

        return LoginStatisticsVO.builder()
                .todayLoginCount(todayLoginCount)
                .todaySuccessCount(todaySuccessCount)
                .todayFailureCount(todayFailureCount)
                .todaySuccessRate(Math.round(todaySuccessRate * 100.0) / 100.0)
                .abnormalIpCount(abnormalIpCount)
                .totalLoginCount(totalLoginCount)
                .totalSuccessCount(totalSuccessCount)
                .totalFailureCount(totalFailureCount)
                .totalSuccessRate(Math.round(totalSuccessRate * 100.0) / 100.0)
                .build();
    }

    @Override
    public boolean clearLoginLogs(Integer days) {
        if (days == null || days <= 0) {
            // 清空所有日志
            return remove(new LambdaQueryWrapper<>());
        } else {
            // 保留最近N天的日志
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
            LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.lt(SysLoginLog::getLoginTime, beforeTime);
            return remove(wrapper);
        }
    }

    /**
     * 转换为 VO
     */
    private LoginLogVO convertToVO(SysLoginLog entity) {
        LoginLogVO vo = new LoginLogVO();
        BeanUtil.copyProperties(entity, vo);
        vo.setStatusName(entity.getStatus() == 1 ? "成功" : "失败");
        return vo;
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 解析IP地址位置（简化版本）
     * 实际项目中可以使用 ip2region 等库进行精确解析
     */
    private String parseIpLocation(String ip) {
        if (StrUtil.isBlank(ip)) {
            return "未知";
        }
        // 本地IP
        if (ip.startsWith("127.") || ip.startsWith("192.168.") || ip.equals("0:0:0:0:0:0:0:1")) {
            return "内网IP";
        }
        // 这里简化处理，实际应该使用IP库解析
        return "未知地区";
    }
}
