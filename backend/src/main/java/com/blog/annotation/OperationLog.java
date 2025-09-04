package com.blog.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作模块
     */
    String module() default "";
    
    /**
     * 操作类型
     */
    com.blog.entity.OperationLog.OperationType type() default com.blog.entity.OperationLog.OperationType.OTHER;
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;
    
    /**
     * 是否记录响应结果
     */
    boolean recordResult() default false;
    
    /**
     * 是否记录执行时间
     */
    boolean recordExecutionTime() default true;
    
    /**
     * 是否忽略异常
     * 如果为true，即使方法抛出异常也会记录日志
     */
    boolean ignoreException() default false;
}