package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.ArticleService;
import com.blog.service.CommentService;
import com.blog.service.UserService;
import com.blog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController {
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats(@RequestHeader("Authorization") String token) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            Map<String, Object> stats = new HashMap<>();
            
            // 获取各种统计数据
            stats.put("articleCount", articleService.countPublishedArticles());
            stats.put("userCount", userService.countActiveUsers());
            stats.put("commentCount", commentService.countApprovedComments());
            
            // 计算总阅读量（这里需要在ArticleService中添加方法）
            stats.put("viewCount", articleService.getTotalViewCount());
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最新文章列表
     */
    @GetMapping("/recent-articles")
    public Result<Object> getRecentArticles(@RequestHeader("Authorization") String token,
                                          @RequestParam(defaultValue = "5") int limit) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            return Result.success(articleService.getRecentArticles(limit));
        } catch (Exception e) {
            return Result.error("获取最新文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最新评论列表
     */
    @GetMapping("/recent-comments")
    public Result<Object> getRecentComments(@RequestHeader("Authorization") String token,
                                          @RequestParam(defaultValue = "5") int limit) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            return Result.success(commentService.getRecentComments(limit));
        } catch (Exception e) {
            return Result.error("获取最新评论失败: " + e.getMessage());
        }
    }
    
    // ========== 图表数据API接口 ==========
    
    /**
     * 获取文章发布趋势数据
     */
    @GetMapping("/chart/article-trend")
    public Result<Object> getArticleTrendData(@RequestHeader("Authorization") String token,
                                             @RequestParam(defaultValue = "month") String period) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            return Result.success(articleService.getArticleTrendData(period));
        } catch (Exception e) {
            return Result.error("获取文章趋势数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文章分类统计数据
     */
    @GetMapping("/chart/article-category")
    public Result<Object> getArticleCategoryStats(@RequestHeader("Authorization") String token) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            return Result.success(articleService.getArticleCategoryStats());
        } catch (Exception e) {
            return Result.error("获取文章分类统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文章访问量趋势数据
     */
    @GetMapping("/chart/article-view-trend")
    public Result<Object> getArticleViewTrendData(@RequestHeader("Authorization") String token,
                                                 @RequestParam(defaultValue = "month") String period) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            return Result.success(articleService.getArticleViewTrendData(period));
        } catch (Exception e) {
            return Result.error("获取文章访问量趋势数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门文章数据
     */
    @GetMapping("/chart/popular-articles")
    public Result<Object> getPopularArticlesData(@RequestHeader("Authorization") String token,
                                                @RequestParam(defaultValue = "10") int limit) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            return Result.success(articleService.getPopularArticlesData(limit));
        } catch (Exception e) {
            return Result.error("获取热门文章数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取评论趋势数据
     */
    @GetMapping("/chart/comment-trend")
    public Result<Object> getCommentTrendData(@RequestHeader("Authorization") String token,
                                            @RequestParam(defaultValue = "month") String period) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            return Result.success(commentService.getCommentTrendData(period));
        } catch (Exception e) {
            return Result.error("获取评论趋势数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户注册趋势数据
     */
    @GetMapping("/chart/user-registration-trend")
    public Result<Object> getUserRegistrationTrendData(@RequestHeader("Authorization") String token,
                                                      @RequestParam(defaultValue = "month") String period) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            return Result.success(userService.getUserRegistrationTrendData(period));
        } catch (Exception e) {
            return Result.error("获取用户注册趋势数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取评论词云数据
     */
    @GetMapping("/chart/comment-wordcloud")
    public Result<Object> getCommentWordCloudData(@RequestHeader("Authorization") String token,
                                                 @RequestParam(defaultValue = "100") int limit) {
        try {
            // 验证token
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null) {
                return Result.error("无效的token");
            }
            
            return Result.success(commentService.getCommentWordCloudData());
        } catch (Exception e) {
            return Result.error("获取评论词云数据失败: " + e.getMessage());
        }
    }
}