package com.blog.service.impl;

import com.blog.dto.CommentDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.repository.CommentRepository;
import com.blog.repository.ArticleRepository;
import com.blog.repository.UserRepository;
import com.blog.service.CommentService;
import com.blog.service.NotificationService;
import com.blog.service.TextAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private TextAnalysisService textAnalysisService;
    
    // 注入评论点赞仓库
    @Autowired
    private com.blog.repository.CommentLikeRepository commentLikeRepository;
    
    @Override
    public CommentDTO createComment(CommentDTO commentDTO, Long userId) {
        // 获取用户
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        Article article = null;
        
        // 如果提供了文章ID，则获取文章（留言板功能可以不关联文章）
        if (commentDTO.getArticleId() != null) {
            Optional<Article> articleOpt = articleRepository.findById(commentDTO.getArticleId());
            if (!articleOpt.isPresent()) {
                throw new RuntimeException("文章不存在");
            }
            article = articleOpt.get();
        }
        
        // 创建评论
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setUser(user);
        comment.setArticle(article); // 可以为null（留言板）
        comment.setStatus(Comment.Status.APPROVED); // 默认审核通过，可根据需要修改
        
        // 如果是回复评论
        if (commentDTO.getParentId() != null) {
            Optional<Comment> parentOpt = commentRepository.findById(commentDTO.getParentId());
            if (parentOpt.isPresent()) {
                comment.setParent(parentOpt.get());
                // 增加父评论的回复数
                Comment parent = parentOpt.get();
                parent.setReplyCount(parent.getReplyCount() + 1);
                commentRepository.save(parent);
            }
        }
        
        // 保存评论
        comment = commentRepository.save(comment);
        
        // 创建通知（仅当关联文章时）
        if (article != null) {
            try {
                // 通知文章作者有新评论
                if (!article.getAuthor().getId().equals(userId)) {
                    notificationService.createCommentNotification(
                        article.getId(),
                        comment.getId(),
                        user.getNickname(),
                        comment.getContent(),
                        article.getTitle(),
                        article.getAuthor().getId()
                    );
                }
                
                // 如果是回复评论，通知被回复的用户
                if (comment.getParent() != null && !comment.getParent().getUser().getId().equals(userId)) {
                    String title = "您的评论收到新回复";
                    String preview = comment.getContent() == null ? "" : comment.getContent().trim();
                    if (preview.length() > 120) {
                        preview = preview.substring(0, 120) + "...";
                    }
                    String content = String.format("用户\"%s\"回复了您在文章\"%s\"下的评论：%s", user.getNickname(), article.getTitle(), preview);
                    notificationService.createUserNotification(title, content, comment.getParent().getUser().getId(), userId);
                }
            } catch (Exception e) {
                // 通知创建失败不影响评论创建
                System.err.println("创建通知失败: " + e.getMessage());
            }
        }
        
        return new CommentDTO(comment);
    }
    
    @Override
    public CommentDTO updateComment(Long id, CommentDTO commentDTO, Long userId) {
        // TODO: 实现更新评论逻辑
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public Optional<CommentDTO> findById(Long id) {
        return commentRepository.findById(id)
                .map(CommentDTO::new);
    }
    
    @Override
    public Page<CommentDTO> findCommentsByArticle(Article article, Pageable pageable) {
        return commentRepository.findByArticleAndParentIsNull(article, pageable)
                .map(CommentDTO::new);
    }
    
    @Override
    public Page<CommentDTO> findCommentsByUser(User user, Pageable pageable) {
        return commentRepository.findByUser(user, pageable)
                .map(CommentDTO::new);
    }
    
    @Override
    public Page<CommentDTO> findCommentsByStatus(Comment.Status status, Pageable pageable) {
        return commentRepository.findByStatus(status, pageable)
                .map(CommentDTO::new);
    }
    
    @Override
    public Page<CommentDTO> searchComments(String keyword, Pageable pageable) {
        return commentRepository.searchComments(keyword, pageable)
                .map(CommentDTO::new);
    }
    
    @Override
    public Page<CommentDTO> searchCommentsForAdmin(String content, String userNickname, 
                                                  String articleTitle, String status, 
                                                  Pageable pageable) {
        Comment.Status statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = Comment.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 如果状态值无效，忽略状态过滤
            }
        }
        
        Page<Comment> comments = commentRepository.searchCommentsForAdmin(content, userNickname, articleTitle, statusEnum, pageable);
        return comments.map(CommentDTO::new);
    }
    
    @Override
    public void approveComment(Long id) {
        commentRepository.findById(id).ifPresent(comment -> {
            comment.setStatus(Comment.Status.APPROVED);
            commentRepository.save(comment);
        });
    }
    
    @Override
    public void rejectComment(Long id) {
        commentRepository.findById(id).ifPresent(comment -> {
            comment.setStatus(Comment.Status.REJECTED);
            commentRepository.save(comment);
        });
    }
    
    @Override
    public void deleteComment(Long id) {
        commentRepository.findById(id).ifPresent(comment -> {
            // 硬删除评论
            commentRepository.delete(comment);
        });
    }
    
    @Override
    public void likeComment(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            throw new IllegalArgumentException("评论ID或用户ID不能为空");
        }
        
        // 如果已点赞，保持幂等
        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return;
        }
        
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (commentOpt.isEmpty()) {
            throw new IllegalArgumentException("评论不存在");
        }
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        Comment comment = commentOpt.get();
        User user = userOpt.get();
        
        // 保存点赞记录
        com.blog.entity.CommentLike like = new com.blog.entity.CommentLike(comment, user);
        commentLikeRepository.save(like);
        
        // 更新点赞数（简单做法，避免并发复杂性；如需高并发可改为@Modifying更新）
        comment.setLikeCount((comment.getLikeCount() == null ? 0 : comment.getLikeCount()) + 1);
        commentRepository.save(comment);
    }
    
    @Override
    public void unlikeComment(Long commentId, Long userId) {
        Optional<com.blog.entity.CommentLike> likeOpt = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (!likeOpt.isPresent()) {
            // 未点赞，幂等返回
            return;
        }
        // 删除点赞记录
        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
        
        // 减少点赞数，避免负数
        commentRepository.findById(commentId).ifPresent(c -> {
            int current = c.getLikeCount() == null ? 0 : c.getLikeCount();
            c.setLikeCount(Math.max(0, current - 1));
            commentRepository.save(c);
        });
    }
    
    @Override
    public boolean isCommentLikedByUser(Long commentId, Long userId) {
        // 如果评论ID或用户ID为null，直接返回false
        if (commentId == null || userId == null) {
            return false;
        }
        
        try {
            return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        } catch (Exception e) {
            // 如果发生异常（如评论不存在），返回false而不是抛出异常
            return false;
        }
    }
    
    @Override
    public long countAllComments() {
        return commentRepository.count();
    }

    @Override
    public long countApprovedComments() {
        return commentRepository.countApprovedComments();
    }

    @Override
    public long countPendingComments() {
        return commentRepository.countPendingComments();
    }
    
    @Override
    public List<CommentDTO> getRecentComments(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return commentRepository.findRecentCommentsLimit(pageable)
                .stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }
    
    // ========== 图表数据方法实现 ==========
    
    @Override
    public List<Object[]> getCommentTrendData(String period) {
        java.time.LocalDateTime startDate;
        switch (period.toLowerCase()) {
            case "week":
                startDate = java.time.LocalDateTime.now().minusWeeks(1);
                break;
            case "month":
                startDate = java.time.LocalDateTime.now().minusMonths(1);
                break;
            case "year":
                startDate = java.time.LocalDateTime.now().minusYears(1);
                break;
            default:
                startDate = java.time.LocalDateTime.now().minusMonths(1);
        }
        return commentRepository.getCommentCountByDate(startDate);
    }
    
    @Override
    public List<Object[]> getCommentWordCloudData() {
        // 获取评论内容
        List<String> comments = commentRepository.getCommentWordCloudData();
        
        // 使用高级文本处理算法生成词云数据
        return textAnalysisService.generateWordCloud(comments, 50);
    }
}