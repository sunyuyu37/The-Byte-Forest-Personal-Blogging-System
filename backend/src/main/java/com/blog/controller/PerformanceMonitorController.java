package com.blog.controller;

import com.blog.annotation.OperationLog;
import com.blog.common.Result;
import com.blog.service.PerformanceMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 性能监控控制器
 * 提供系统性能监控数据的查询接口
 */
@RestController
@RequestMapping("/admin/performance")
public class PerformanceMonitorController {

    @Autowired
    private PerformanceMonitorService performanceMonitorService;

    /**
     * 获取系统性能概览
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getPerformanceOverview() {
        try {
            Map<String, Object> overview = Map.of(
                "jvmMetrics", performanceMonitorService.getJvmMemoryMetrics(),
                "systemMetrics", performanceMonitorService.getSystemMetrics(),
                "threadMetrics", performanceMonitorService.getThreadMetrics(),
                "requestMetrics", performanceMonitorService.getRequestMetrics(),
                "errorRate", performanceMonitorService.getErrorRate(),
                "healthStatus", performanceMonitorService.getHealthStatus()
            );
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error("获取性能概览失败: " + e.getMessage());
        }
    }

    /**
     * 获取JVM内存指标
     */
    @GetMapping("/jvm")
    public Result<Map<String, Object>> getJvmMetrics() {
        try {
            Map<String, Object> metrics = performanceMonitorService.getJvmMemoryMetrics();
            return Result.success(metrics);
        } catch (Exception e) {
            return Result.error("获取JVM指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统指标
     */
    @GetMapping("/system")
    public Result<Map<String, Object>> getSystemMetrics() {
        try {
            Map<String, Object> metrics = performanceMonitorService.getSystemMetrics();
            return Result.success(metrics);
        } catch (Exception e) {
            return Result.error("获取系统指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取线程指标
     */
    @GetMapping("/threads")
    public Result<Map<String, Object>> getThreadMetrics() {
        try {
            Map<String, Object> metrics = performanceMonitorService.getThreadMetrics();
            return Result.success(metrics);
        } catch (Exception e) {
            return Result.error("获取线程指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取请求指标
     */
    @GetMapping("/requests")
    public Result<Map<String, Object>> getRequestMetrics() {
        try {
            Map<String, Object> metrics = performanceMonitorService.getRequestMetrics();
            return Result.success(metrics);
        } catch (Exception e) {
            return Result.error("获取请求指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取错误率
     */
    @GetMapping("/error-rate")
    public Result<Double> getErrorRate() {
        try {
            double errorRate = performanceMonitorService.getErrorRate();
            return Result.success(errorRate);
        } catch (Exception e) {
            return Result.error("获取错误率失败: " + e.getMessage());
        }
    }

    /**
     * 获取健康状态
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> getHealthStatus() {
        try {
            Map<String, Object> health = performanceMonitorService.getHealthStatus();
            return Result.success(health);
        } catch (Exception e) {
            return Result.error("获取健康状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取接口响应时间统计
     */
    @GetMapping("/response-times")
    public Result<Map<String, Object>> getResponseTimes() {
        try {
            Map<String, Object> responseTimes = performanceMonitorService.getResponseTimeStats();
            return Result.success(responseTimes);
        } catch (Exception e) {
            return Result.error("获取响应时间统计失败: " + e.getMessage());
        }
    }

    /**
     * 重置性能监控数据
     */
    @PostMapping("/reset")
    @OperationLog(module = "性能监控", type = com.blog.entity.OperationLog.OperationType.UPDATE, description = "重置性能监控数据")
    public Result<String> resetPerformanceData() {
        try {
            performanceMonitorService.resetMetrics();
            return Result.success("重置性能监控数据成功");
        } catch (Exception e) {
            return Result.error("重置性能监控数据失败: " + e.getMessage());
        }
    }

    /**
     * 清理性能监控数据
     */
    @DeleteMapping("/cleanup")
    @OperationLog(module = "性能监控", type = com.blog.entity.OperationLog.OperationType.DELETE, description = "清理性能监控数据")
    public Result<String> cleanupPerformanceData(@RequestParam(defaultValue = "7") int days) {
        try {
            performanceMonitorService.cleanupOldData(days);
            return Result.success("清理性能监控数据成功");
        } catch (Exception e) {
            return Result.error("清理性能监控数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取性能告警信息
     */
    @GetMapping("/alerts")
    public Result<Map<String, Object>> getPerformanceAlerts() {
        try {
            Map<String, Object> alerts = performanceMonitorService.getPerformanceAlerts();
            return Result.success(alerts);
        } catch (Exception e) {
            return Result.error("获取性能告警信息失败: " + e.getMessage());
        }
    }
}