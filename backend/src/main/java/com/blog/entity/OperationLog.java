package com.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Entity
@Table(name = "operation_logs")
public class OperationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 操作用户ID
     */
    @Column(name = "user_id")
    private Long userId;
    
    /**
     * 操作用户名
     */
    @Column(name = "username", length = 50)
    private String username;
    
    /**
     * 操作模块
     */
    @Column(name = "module", length = 50, nullable = false)
    private String module;
    
    /**
     * 操作类型
     */
    @Column(name = "operation_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    
    /**
     * 操作描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * 请求方法
     */
    @Column(name = "method", length = 10)
    private String method;
    
    /**
     * 请求URL
     */
    @Column(name = "request_url", length = 500)
    private String requestUrl;
    
    /**
     * 请求参数
     */
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;
    
    /**
     * 响应结果
     */
    @Column(name = "response_result", columnDefinition = "TEXT")
    private String responseResult;
    
    /**
     * 操作状态
     */
    @Column(name = "status", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationStatus status;
    
    /**
     * 错误信息
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    /**
     * 用户代理
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    /**
     * 执行时间（毫秒）
     */
    @Column(name = "execution_time")
    private Long executionTime;
    
    /**
     * 操作时间
     */
    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 操作类型枚举
     */
    public enum OperationType {
        CREATE("创建"),
        UPDATE("更新"),
        DELETE("删除"),
        QUERY("查询"),
        LOGIN("登录"),
        LOGOUT("登出"),
        UPLOAD("上传"),
        DOWNLOAD("下载"),
        EXPORT("导出"),
        IMPORT("导入"),
        OTHER("其他");
        
        private final String description;
        
        OperationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 操作状态枚举
     */
    public enum OperationStatus {
        SUCCESS("成功"),
        FAILURE("失败"),
        ERROR("错误");
        
        private final String description;
        
        OperationStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 构造函数
    public OperationLog() {
        this.operationTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getModule() {
        return module;
    }
    
    public void setModule(String module) {
        this.module = module;
    }
    
    public OperationType getOperationType() {
        return operationType;
    }
    
    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getRequestUrl() {
        return requestUrl;
    }
    
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
    
    public String getRequestParams() {
        return requestParams;
    }
    
    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }
    
    public String getResponseResult() {
        return responseResult;
    }
    
    public void setResponseResult(String responseResult) {
        this.responseResult = responseResult;
    }
    
    public OperationStatus getStatus() {
        return status;
    }
    
    public void setStatus(OperationStatus status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public Long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }
    
    public LocalDateTime getOperationTime() {
        return operationTime;
    }
    
    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}