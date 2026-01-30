package com.edu.framework.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.edu.framework.annotation.OperationLog;
import com.edu.framework.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    // private final SysOperationLogService operationLogService;

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Before("@annotation(operationLog)")
    public void doBefore(JoinPoint joinPoint, OperationLog operationLog) {
        START_TIME.set(System.currentTimeMillis());
    }

    @AfterReturning(pointcut = "@annotation(operationLog)", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, OperationLog operationLog, Object result) {
        handleLog(joinPoint, operationLog, null, result);
    }

    @AfterThrowing(pointcut = "@annotation(operationLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog operationLog, Exception e) {
        handleLog(joinPoint, operationLog, e, null);
    }

    /**
     * 处理日志
     */
    private void handleLog(JoinPoint joinPoint, OperationLog operationLog, Exception e, Object result) {
        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            // 构建日志对象
            Map<String, Object> logData = new HashMap<>();
            logData.put("module", operationLog.module());
            logData.put("type", operationLog.type().name());
            logData.put("description", operationLog.description());

            // 方法信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            logData.put("method", joinPoint.getTarget().getClass().getName() + "." + method.getName());

            // 请求信息
            if (request != null) {
                logData.put("requestUrl", request.getRequestURI());
                logData.put("requestMethod", request.getMethod());
                logData.put("ip", getIpAddress(request));
                logData.put("userAgent", request.getHeader("User-Agent"));
            }

            // 用户信息
            try {
                logData.put("userId", SecurityUtils.getUserId());
                logData.put("username", SecurityUtils.getUsername());
            } catch (Exception ex) {
                // 未登录状态
            }

            // 请求参数
            if (operationLog.saveParams()) {
                String params = getParams(joinPoint);
                if (params.length() > 2000) {
                    params = params.substring(0, 2000) + "...";
                }
                logData.put("params", params);
            }

            // 响应结果
            if (operationLog.saveResult() && result != null) {
                String resultStr = JSONUtil.toJsonStr(result);
                if (resultStr.length() > 2000) {
                    resultStr = resultStr.substring(0, 2000) + "...";
                }
                logData.put("result", resultStr);
            }

            // 执行时间
            Long startTime = START_TIME.get();
            if (startTime != null) {
                logData.put("costTime", System.currentTimeMillis() - startTime);
            }

            // 异常信息
            if (e != null) {
                logData.put("status", 1); // 失败
                logData.put("errorMsg", StrUtil.sub(e.getMessage(), 0, 2000));
            } else {
                logData.put("status", 0); // 成功
            }

            logData.put("operationTime", LocalDateTime.now());

            // 异步保存日志
            saveLog(logData);

        } catch (Exception ex) {
            log.error("Failed to save operation log", ex);
        } finally {
            START_TIME.remove();
        }
    }

    /**
     * 获取请求参数
     */
    private String getParams(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (paramNames == null || args == null) {
            return "";
        }

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            Object arg = args[i];
            // 过滤敏感参数
            if (arg != null && !isSensitiveParam(paramNames[i])) {
                params.put(paramNames[i], arg);
            }
        }

        return JSONUtil.toJsonStr(params);
    }

    /**
     * 判断是否敏感参数
     */
    private boolean isSensitiveParam(String paramName) {
        String[] sensitiveParams = {"password", "pwd", "secret", "token", "key"};
        for (String sensitive : sensitiveParams) {
            if (paramName.toLowerCase().contains(sensitive)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取IP地址
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
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 异步保存日志
     */
    @Async
    protected void saveLog(Map<String, Object> logData) {
        // TODO: 保存到数据库
        // SysOperationLog operationLog = new SysOperationLog();
        // BeanUtil.copyProperties(logData, operationLog);
        // operationLogService.save(operationLog);

        log.info("Operation Log: {}", JSONUtil.toJsonStr(logData));
    }
}
