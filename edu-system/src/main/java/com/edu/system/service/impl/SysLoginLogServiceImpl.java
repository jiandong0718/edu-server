package com.edu.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.system.domain.entity.SysLoginLog;
import com.edu.system.mapper.SysLoginLogMapper;
import com.edu.system.service.SysLoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
