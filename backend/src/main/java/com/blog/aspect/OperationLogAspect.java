package com.blog.aspect;

import com.blog.annotation.OperationLog;
import com.blog.entity.User;
import com.blog.service.OperationLogService;
import com.blog.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 */
@Aspect
@Component
public class OperationLogAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);
    private static final Logger operationLogger = LoggerFactory.getLogger("OPERATION_LOGGER");
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.blog.annotation.OperationLog)")
    public void operationLogPointcut() {}
    
    /**
     * 环绕通知
     */
    @Around("operationLogPointcut() && @annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        HttpServletRequest request = getHttpServletRequest();
        
        // 创建操作日志对象
        com.blog.entity.OperationLog log = new com.blog.entity.OperationLog();
        
        try {
            // 设置基本信息
            setBasicInfo(log, operationLog, joinPoint, request);
            
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 记录成功信息
            long executionTime = System.currentTimeMillis() - startTime;
            log.setExecutionTime(executionTime);
            log.setStatus(com.blog.entity.OperationLog.OperationStatus.SUCCESS);
            
            // 记录响应结果
            if (operationLog.recordResult() && result != null) {
                try {
                    String resultStr = objectMapper.writeValueAsString(result);
                    // 限制结果长度，避免过长
                    if (resultStr.length() > 2000) {
                        resultStr = resultStr.substring(0, 2000) + "...";
                    }
                    log.setResponseResult(resultStr);
                } catch (Exception e) {
                    log.setResponseResult("序列化响应结果失败: " + e.getMessage());
                }
            }
            
            // 异步保存日志
            saveLogAsync(log, operationLog);
            
            return result;
            
        } catch (Throwable throwable) {
            // 记录异常信息
            long executionTime = System.currentTimeMillis() - startTime;
            log.setExecutionTime(executionTime);
            log.setStatus(com.blog.entity.OperationLog.OperationStatus.ERROR);
            log.setErrorMessage(throwable.getMessage());
            
            // 即使发生异常也要记录日志（如果配置允许）
            if (operationLog.ignoreException()) {
                saveLogAsync(log, operationLog);
            } else {
                saveLogAsync(log, operationLog);
            }
            
            throw throwable;
        }
    }
    
    /**
     * 设置基本信息
     */
    private void setBasicInfo(com.blog.entity.OperationLog log, OperationLog annotation, 
                             JoinPoint joinPoint, HttpServletRequest request) {
        
        // 设置操作信息
        log.setModule(annotation.module());
        log.setOperationType(annotation.type());
        log.setDescription(annotation.description());
        log.setOperationTime(LocalDateTime.now());
        
        if (request != null) {
            // 设置请求信息
            log.setMethod(request.getMethod());
            log.setRequestUrl(request.getRequestURL().toString());
            log.setIpAddress(getClientIpAddress(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            
            // 设置用户信息
            setUserInfo(log, request);
            
            // 记录请求参数
            if (annotation.recordParams()) {
                String params = getRequestParams(joinPoint, request);
                log.setRequestParams(params);
            }
        }
    }
    
    /**
     * 设置用户信息
     */
    private void setUserInfo(com.blog.entity.OperationLog log, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    log.setUserId(userId);
                    log.setUsername(username);
                }
            }
        } catch (Exception e) {
            logger.warn("获取用户信息失败: {}", e.getMessage());
        }
    }
    
    /**
     * 获取请求参数
     */
    private String getRequestParams(JoinPoint joinPoint, HttpServletRequest request) {
        try {
            Map<String, Object> params = new HashMap<>();
            
            // 获取方法参数
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 过滤敏感参数
                Object[] filteredArgs = Arrays.stream(args)
                    .map(this::filterSensitiveData)
                    .toArray();
                params.put("methodArgs", filteredArgs);
            }
            
            // 获取请求参数
            Map<String, String> requestParams = new HashMap<>();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                // 过滤敏感参数
                if (isSensitiveParam(paramName)) {
                    paramValue = "***";
                }
                requestParams.put(paramName, paramValue);
            }
            if (!requestParams.isEmpty()) {
                params.put("requestParams", requestParams);
            }
            
            String paramsStr = objectMapper.writeValueAsString(params);
            // 限制参数长度
            if (paramsStr.length() > 2000) {
                paramsStr = paramsStr.substring(0, 2000) + "...";
            }
            return paramsStr;
            
        } catch (Exception e) {
            return "获取请求参数失败: " + e.getMessage();
        }
    }
    
    /**
     * 过滤敏感数据
     */
    private Object filterSensitiveData(Object obj) {
        if (obj == null) {
            return null;
        }
        
        // 如果是字符串且可能是敏感信息，则脱敏
        if (obj instanceof String) {
            String str = (String) obj;
            if (str.length() > 100) {
                return str.substring(0, 100) + "...";
            }
        }
        
        // 如果是User对象，移除敏感信息
        if (obj instanceof User) {
            User user = (User) obj;
            User filteredUser = new User();
            filteredUser.setId(user.getId());
            filteredUser.setUsername(user.getUsername());
            filteredUser.setEmail(user.getEmail());
            // 不包含密码等敏感信息
            return filteredUser;
        }
        
        return obj;
    }
    
    /**
     * 判断是否为敏感参数
     */
    private boolean isSensitiveParam(String paramName) {
        String lowerParamName = paramName.toLowerCase();
        return lowerParamName.contains("password") || 
               lowerParamName.contains("token") ||
               lowerParamName.contains("secret") ||
               lowerParamName.contains("key");
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 获取HttpServletRequest
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 异步保存日志
     */
    private void saveLogAsync(com.blog.entity.OperationLog log, OperationLog annotation) {
        try {
            // 记录到文件
            operationLogger.info("操作日志: 用户[{}] 在模块[{}] 执行[{}]操作, 描述: {}, 状态: {}, 耗时: {}ms",
                log.getUsername(), log.getModule(), log.getOperationType().getDescription(),
                log.getDescription(), log.getStatus().getDescription(), log.getExecutionTime());
            
            // 保存到数据库
            operationLogService.saveOperationLog(log);
            
        } catch (Exception e) {
            logger.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }
}