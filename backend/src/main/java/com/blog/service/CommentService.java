package com.blog.service;

import com.blog.dto.CommentDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    
    /**
     * 创建评论
     */
    CommentDTO createComment(CommentDTO commentDTO, Long userId);
    
    /**
     * 更新评论
     */
    CommentDTO updateComment(Long id, CommentDTO commentDTO, Long userId);
    
    /**
     * 根据ID查找评论
     */
    Optional<CommentDTO> findById(Long id);
    
    /**
     * 根据文章查询评论
     */
    Page<CommentDTO> findCommentsByArticle(Article article, Pageable pageable);
    
    /**
     * 根据用户查询评论
     */
    Page<CommentDTO> findCommentsByUser(User user, Pageable pageable);
    
    /**
     * 根据状态查询评论
     */
    Page<CommentDTO> findCommentsByStatus(Comment.Status status, Pageable pageable);
    
    /**
     * 搜索评论
     */
    Page<CommentDTO> searchComments(String keyword, Pageable pageable);
    
    /**
     * 管理员搜索评论（支持多条件筛选）
     */
    Page<CommentDTO> searchCommentsForAdmin(String content, String userNickname, 
                                          String articleTitle, String status, 
                                          Pageable pageable);
    
    /**
     * 审核评论
     */
    void approveComment(Long id);
    
    /**
     * 拒绝评论
     */
    void rejectComment(Long id);
    
    /**
     * 删除评论
     */
    void deleteComment(Long id);
    
    /**
     * 点赞评论
     */
    void likeComment(Long commentId, Long userId);
    
    /**
     * 取消点赞评论
     */
    void unlikeComment(Long commentId, Long userId);
    
    /**
     * 检查用户是否已点赞评论
     */
    boolean isCommentLikedByUser(Long commentId, Long userId);
    
    /**
     * 获取评论统计信息
     */
    long countAllComments();
    
    long countApprovedComments();
    
    long countPendingComments();
    
    /**
     * 获取最新评论（限制数量）
     */
    List<CommentDTO> getRecentComments(int limit);
    
    // ========== 图表数据方法 ==========
    
    /**
     * 获取评论趋势数据（按日期）
     */
    List<Object[]> getCommentTrendData(String period);
    
    /**
     * 获取评论词云数据
     */
    List<Object[]> getCommentWordCloudData();
}