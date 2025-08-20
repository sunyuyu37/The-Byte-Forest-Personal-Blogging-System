package com.blog.service;

import java.util.List;
import java.util.Map;

/**
 * 文本分析服务接口
 * 提供各种文本分析功能
 */
public interface TextAnalysisService {
    
    /**
     * 生成词云数据
     * @param texts 文本列表
     * @param limit 返回词汇数量限制
     * @return 词云数据 [词汇, 频次]
     */
    List<Object[]> generateWordCloud(List<String> texts, int limit);
    
    /**
     * 分析文本情感倾向
     * @param text 待分析文本
     * @return 情感分数 (-1到1，负数表示消极，正数表示积极)
     */
    double analyzeSentiment(String text);
    
    /**
     * 提取关键词
     * @param text 待分析文本
     * @param limit 关键词数量限制
     * @return 关键词列表
     */
    List<String> extractKeywords(String text, int limit);
    
    /**
     * 计算文本相似度
     * @param text1 文本1
     * @param text2 文本2
     * @return 相似度分数 (0到1)
     */
    double calculateSimilarity(String text1, String text2);
    
    /**
     * 文本摘要生成
     * @param text 原始文本
     * @param maxLength 摘要最大长度
     * @return 文本摘要
     */
    String generateSummary(String text, int maxLength);
    
    /**
     * 获取文本统计信息
     * @param text 待分析文本
     * @return 统计信息（字符数、词汇数、句子数等）
     */
    Map<String, Object> getTextStatistics(String text);
    
    /**
     * 检测文本语言
     * @param text 待检测文本
     * @return 语言代码 (zh, en, etc.)
     */
    String detectLanguage(String text);
    
    /**
     * 文本分类
     * @param text 待分类文本
     * @return 分类结果
     */
    String classifyText(String text);
}