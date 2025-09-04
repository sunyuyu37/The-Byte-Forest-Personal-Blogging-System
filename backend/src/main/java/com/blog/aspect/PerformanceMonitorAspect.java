package com.blog.aspect;

import com.blog.service.PerformanceMonitorService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 性能监控切面
 */
@Aspect
@Component
public class PerformanceMonitorAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorAspect.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE_LOGGER");
    
    @Autowired
    private PerformanceMonitorService performanceMonitorService;
    
    /**
     * 定义切点 - 所有Controller方法
     */
    @Pointcut("execution(* com.blog.controller..*(..))")
    public void controllerMethods() {}
    
    /**
     * 定义切点 - 所有Service方法
     */
    @Pointcut("execution(* com.blog.service..*(..))")
    public void serviceMethods() {}
    
    /**
     * 监控Controller方法性能
     */
    @Around("controllerMethods()")
    public Object monitorControllerPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = getMethodName(joinPoint);
        String endpoint = getEndpoint();
        
        try {
            // 记录请求
            performanceMonitorService.recordRequest();
            
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录响应时间
            performanceMonitorService.recordResponseTime(endpoint, executionTime);
            
            // 记录性能日志
            if (executionTime > 1000) { // 超过1秒的请求
                performanceLogger.warn("Controller慢方法: {} 耗时 {}ms", methodName, executionTime);
            } else {
                performanceLogger.debug("Controller方法: {} 耗时 {}ms", methodName, executionTime);
            }
            
            return result;
            
        } catch (Throwable throwable) {
            // 记录错误
            performanceMonitorService.recordError();
            
            long executionTime = System.currentTimeMillis() - startTime;
            performanceLogger.error("Controller方法异常: {} 耗时 {}ms, 异常: {}", 
                methodName, executionTime, throwable.getMessage());
            
            throw throwable;
        }
    }
    
    /**
     * 监控Service方法性能
     */
    @Around("serviceMethods()")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = getMethodName(joinPoint);
        
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录性能日志
            if (executionTime > 2000) { // 超过2秒的Service方法
                performanceLogger.warn("Service慢方法: {} 耗时 {}ms", methodName, executionTime);
            } else if (executionTime > 500) { // 超过500ms的方法
                performanceLogger.info("Service方法: {} 耗时 {}ms", methodName, executionTime);
            } else {
                performanceLogger.debug("Service方法: {} 耗时 {}ms", methodName, executionTime);
            }
            
            return result;
            
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            performanceLogger.error("Service方法异常: {} 耗时 {}ms, 异常: {}", 
                methodName, executionTime, throwable.getMessage());
            
            throw throwable;
        }
    }
    
    /**
     * 获取方法名称
     */
    private String getMethodName(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return className + "." + methodName;
    }
    
    /**
     * 获取请求端点
     */
    private String getEndpoint() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getMethod() + " " + request.getRequestURI();
            }
        } catch (Exception e) {
            logger.debug("获取请求端点失败: {}", e.getMessage());
        }
        return "Unknown";
    }
}