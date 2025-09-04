package com.blog.service;

import com.blog.dto.CommentDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
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
     * 更新评论（管理员）
     */
    void updateComment(Long id, CommentDTO commentDTO);
    
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
                                          Boolean messageOnly, Pageable pageable);
    
    /**
     * 获取留言列表（支持高级筛选参数）
     */
    Page<CommentDTO> getMessages(int page, int size, String content, String userNickname, String status, boolean messageOnly);
    
    Page<CommentDTO> getMessages(int page, int size, String content, String userNickname, String status, boolean messageOnly, 
                                String ip, String userEmail, String contentLength, String sortBy, String startTime, String endTime);
    
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
    
    // ========== 留言统计方法 ==========
    
    /**
     * 获取留言统计信息
     */
    long countAllMessages();
    
    long countApprovedMessages();
    
    long countPendingMessages();
    
    long countRejectedMessages();
    
    /**
     * 获取最新留言（限制数量）
     */
    List<CommentDTO> getRecentMessages(int limit);
    
    /**
     * 导出留言数据
     */
    byte[] exportMessages(String status, String userNickname, String content);
    
    /**
     * 获取留言详情（包含完整信息）
     */
    CommentDTO getMessageDetail(Long messageId);
    
    /**
     * 获取留言趋势统计
     */
    Map<String, Object> getMessageTrend(int days);
    
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