package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.TextAnalysisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文本分析控制器
 */
@RestController
@RequestMapping("/text-analysis")
public class TextAnalysisController {
    
    @Autowired
    private TextAnalysisService textAnalysisService;
    
    /**
     * 生成词云数据
     */
    @PostMapping("/wordcloud")
    public Result<List<Object[]>> generateWordCloud(@Valid @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> texts = (List<String>) request.get("texts");
            Integer limit = (Integer) request.getOrDefault("limit", 50);
            List<Object[]> wordCloudData = textAnalysisService.generateWordCloud(texts, limit);
            return Result.success(wordCloudData);
        } catch (Exception e) {
            return Result.error("生成词云失败: " + e.getMessage());
        }
    }
    
    /**
     * 情感分析
     */
    @PostMapping("/sentiment")
    public Result<Map<String, Object>> analyzeSentiment(@Valid @RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            double sentiment = textAnalysisService.analyzeSentiment(text);
            Map<String, Object> result = new HashMap<>();
            result.put("sentiment", sentiment);
            result.put("label", getSentimentLabel(sentiment));
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("情感分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 关键词提取
     */
    @PostMapping("/keywords")
    public Result<List<String>> extractKeywords(@Valid @RequestBody Map<String, Object> request) {
        try {
            String text = (String) request.get("text");
            Integer limit = (Integer) request.getOrDefault("limit", 10);
            List<String> keywords = textAnalysisService.extractKeywords(text, limit);
            return Result.success(keywords);
        } catch (Exception e) {
            return Result.error("关键词提取失败: " + e.getMessage());
        }
    }
    
    /**
     * 文本相似度计算
     */
    @PostMapping("/similarity")
    public Result<Map<String, Object>> calculateSimilarity(@Valid @RequestBody Map<String, String> request) {
        try {
            String text1 = request.get("text1");
            String text2 = request.get("text2");
            double similarity = textAnalysisService.calculateSimilarity(text1, text2);
            Map<String, Object> result = new HashMap<>();
            result.put("similarity", similarity);
            result.put("percentage", Math.round(similarity * 100 * 100.0) / 100.0);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("相似度计算失败: " + e.getMessage());
        }
    }
    
    /**
     * 文本摘要生成
     */
    @PostMapping("/summary")
    public Result<Map<String, Object>> generateSummary(@Valid @RequestBody Map<String, Object> request) {
        try {
            String text = (String) request.get("text");
            Integer maxLength = (Integer) request.getOrDefault("maxLength", 200);
            String summary = textAnalysisService.generateSummary(text, maxLength);
            Map<String, Object> result = new HashMap<>();
            result.put("summary", summary);
            result.put("originalLength", text.length());
            result.put("summaryLength", summary.length());
            result.put("compressionRatio", Math.round((double) summary.length() / text.length() * 100 * 100.0) / 100.0);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("文本摘要生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 文本统计信息
     */
    @PostMapping("/statistics")
    public Result<Map<String, Object>> getTextStatistics(@Valid @RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            Map<String, Object> statistics = textAnalysisService.getTextStatistics(text);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取文本统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 语言检测
     */
    @PostMapping("/language")
    public Result<Map<String, Object>> detectLanguage(@Valid @RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            String language = textAnalysisService.detectLanguage(text);
            
            Map<String, Object> result = new HashMap<>();
            result.put("language", language);
            result.put("languageName", getLanguageName(language));
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("语言检测失败: " + e.getMessage());
        }
    }
    
    /**
     * 文本分类
     */
    @PostMapping("/classify")
    public Result<Map<String, Object>> classifyText(@Valid @RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            String category = textAnalysisService.classifyText(text);
            
            Map<String, Object> result = new HashMap<>();
            result.put("category", category);
            result.put("categoryName", getCategoryName(category));
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("文本分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取情感标签
     */
    private String getSentimentLabel(double sentiment) {
        if (sentiment > 0.1) {
            return "积极";
        } else if (sentiment < -0.1) {
            return "消极";
        } else {
            return "中性";
        }
    }
    
    /**
     * 获取语言名称
     */
    private String getLanguageName(String language) {
        switch (language) {
            case "zh":
                return "中文";
            case "en":
                return "英文";
            case "mixed":
                return "混合语言";
            default:
                return "未知语言";
        }
    }
    
    /**
     * 获取分类名称
     */
    private String getCategoryName(String category) {
        switch (category) {
            case "technology":
                return "技术";
            case "lifestyle":
                return "生活";
            case "education":
                return "教育";
            case "entertainment":
                return "娱乐";
            case "general":
                return "通用";
            default:
                return "未知";
        }
    }
}