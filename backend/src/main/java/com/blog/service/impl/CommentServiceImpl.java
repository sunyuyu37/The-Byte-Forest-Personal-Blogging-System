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
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

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
        } else {
            // 留言板留言，通知所有管理员
            try {
                String displayName = (user.getNickname() != null && !user.getNickname().isEmpty()) ? user.getNickname() : user.getUsername();
                String preview = comment.getContent() == null ? "" : comment.getContent().trim();
                if (preview.length() > 120) {
                    preview = preview.substring(0, 120) + "...";
                }
                String title = "新的留言待处理";
                String content = String.format("用户\"%s\"在留言板发布了新留言：%s", displayName, preview);
                // 查询所有管理员并逐个创建通知
                org.springframework.data.domain.Page<com.blog.entity.User> admins = userRepository.findByRole(com.blog.entity.User.Role.ADMIN, org.springframework.data.domain.Pageable.unpaged());
                for (com.blog.entity.User admin : admins.getContent()) {
                    notificationService.createNotification(title, content, "comment", admin.getId(), comment.getId(), "message");
                }
            } catch (Exception e) {
                System.err.println("创建留言通知失败: " + e.getMessage());
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
    public void updateComment(Long id, CommentDTO commentDTO) {
        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (!commentOpt.isPresent()) {
            throw new RuntimeException("评论不存在");
        }
        
        Comment comment = commentOpt.get();
        
        // 更新评论内容
        if (commentDTO.getContent() != null && !commentDTO.getContent().trim().isEmpty()) {
            comment.setContent(commentDTO.getContent().trim());
        }
        
        // 更新评论状态
        if (commentDTO.getStatus() != null) {
            comment.setStatus(commentDTO.getStatus());
        }
        
        // 保存更新
        commentRepository.save(comment);
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
                                                  Boolean messageOnly, Pageable pageable) {
        Comment.Status statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = Comment.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 如果状态值无效，忽略状态过滤
            }
        }
        
        Page<Comment> comments = commentRepository.searchCommentsForAdmin(content, userNickname, articleTitle, statusEnum, messageOnly, pageable);
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
    
    // ========== 留言统计方法实现 ==========
    
    @Override
    public long countAllMessages() {
        return commentRepository.countAllMessages();
    }
    
    @Override
    public long countApprovedMessages() {
        return commentRepository.countApprovedMessages();
    }
    
    @Override
    public long countPendingMessages() {
        return commentRepository.countPendingMessages();
    }
    
    @Override
    public long countRejectedMessages() {
        return commentRepository.countRejectedMessages();
    }
    
    @Override
    public List<CommentDTO> getRecentMessages(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Comment> messages = commentRepository.findRecentMessagesLimit(pageable);
        
        return messages.stream()
                .map(message -> {
                    CommentDTO dto = new CommentDTO(message);
                    // 获取该留言的所有回复
                    List<Comment> replies = commentRepository.findByParent(message);
                    List<CommentDTO> replyDTOs = replies.stream()
                        .map(CommentDTO::new)
                        .collect(Collectors.toList());
                    dto.setReplies(replyDTOs);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<CommentDTO> getMessages(int page, int size, String content, String userNickname, String status, boolean messageOnly) {
        return getMessages(page, size, content, userNickname, status, messageOnly, null, null, null, "createdAt_desc", null, null);
    }
    
    @Override
    public Page<CommentDTO> getMessages(int page, int size, String content, String userNickname, String status, boolean messageOnly, 
                                      String ip, String userEmail, String contentLength, String sortBy, String startTime, String endTime) {
        org.springframework.data.jpa.domain.Specification<Comment> spec = (root, query, criteriaBuilder) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            
            // 只获取留言（article_id为null）
            if (messageOnly) {
                predicates.add(criteriaBuilder.isNull(root.get("article")));
            }
            
            // 按内容搜索
            if (content != null && !content.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + content + "%"));
            }
            
            // 按用户昵称搜索
            if (userNickname != null && !userNickname.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("user").get("nickname"), "%" + userNickname + "%"));
            }
            
            // 按状态筛选
            if (status != null && !status.trim().isEmpty()) {
                try {
                    Comment.Status commentStatus = Comment.Status.valueOf(status.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), commentStatus));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的状态值
                }
            }
            
            // 按IP地址筛选
            if (ip != null && !ip.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("userIp"), "%" + ip + "%"));
            }
            
            // 按用户邮箱筛选
            if (userEmail != null && !userEmail.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("user").get("email"), "%" + userEmail + "%"));
            }
            
            // 按内容长度筛选
            if (contentLength != null && !contentLength.trim().isEmpty()) {
                switch (contentLength) {
                    case "short":
                        predicates.add(criteriaBuilder.lessThan(criteriaBuilder.length(root.get("content")), 50));
                        break;
                    case "medium":
                        predicates.add(criteriaBuilder.and(
                            criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.length(root.get("content")), 50),
                            criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.length(root.get("content")), 200)
                        ));
                        break;
                    case "long":
                        predicates.add(criteriaBuilder.greaterThan(criteriaBuilder.length(root.get("content")), 200));
                        break;
                }
            }
            
            // 按时间范围筛选
            if (startTime != null && !startTime.trim().isEmpty()) {
                try {
                    java.time.LocalDateTime start = java.time.LocalDateTime.parse(startTime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), start));
                } catch (Exception e) {
                    // 忽略无效的时间格式
                }
            }
            
            if (endTime != null && !endTime.trim().isEmpty()) {
                try {
                    java.time.LocalDateTime end = java.time.LocalDateTime.parse(endTime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), end));
                } catch (Exception e) {
                    // 忽略无效的时间格式
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        
        // 处理排序
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy) {
                case "createdAt_asc":
                    sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "createdAt");
                    break;
                case "createdAt_desc":
                    sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
                    break;
                case "content_length":
                    // 按内容长度排序需要在查询后处理，这里先按创建时间排序
                    sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
                    break;
                default:
                    sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
                    break;
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> comments = commentRepository.findAll(spec, pageable);
        
        return comments.map(CommentDTO::new);
    }
    
    @Override
    public byte[] exportMessages(String status, String userNickname, String content) {
        try {
            // 获取留言数据
            Comment.Status statusEnum = null;
            if (status != null && !status.trim().isEmpty()) {
                try {
                    statusEnum = Comment.Status.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // 如果状态值无效，忽略状态过滤
                }
            }
            
            Page<Comment> messages = commentRepository.searchCommentsForAdmin(
                content, userNickname, null, statusEnum, true, Pageable.unpaged()
            );
            
            // 创建工作簿
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("留言数据");
            
            // 创建标题行
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "用户昵称", "用户邮箱", "留言内容", "状态", "留言时间", "回复数"};
            
            // 设置标题样式
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 创建数据行
            org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setWrapText(true);
            
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            List<Comment> messageList = messages.getContent();
            for (int i = 0; i < messageList.size(); i++) {
                Comment message = messageList.get(i);
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                
                // ID
                org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
                cell0.setCellValue(message.getId());
                cell0.setCellStyle(dataStyle);
                
                // 用户昵称
                org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
                cell1.setCellValue(message.getUser() != null ? message.getUser().getNickname() : "匿名用户");
                cell1.setCellStyle(dataStyle);
                
                // 用户邮箱
                org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2);
                cell2.setCellValue(message.getUser() != null ? message.getUser().getEmail() : "");
                cell2.setCellStyle(dataStyle);
                
                // 留言内容
                org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3);
                cell3.setCellValue(message.getContent());
                cell3.setCellStyle(dataStyle);
                
                // 状态
                org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4);
                String statusText = "";
                if (message.getStatus() == Comment.Status.APPROVED) {
                    statusText = "已通过";
                } else if (message.getStatus() == Comment.Status.REJECTED) {
                    statusText = "已拒绝";
                } else if (message.getStatus() == Comment.Status.PENDING) {
                    statusText = "待审核";
                }
                cell4.setCellValue(statusText);
                cell4.setCellStyle(dataStyle);
                
                // 留言时间
                org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5);
                cell5.setCellValue(message.getCreatedAt().format(formatter));
                cell5.setCellStyle(dataStyle);
                
                // 回复数
                org.apache.poi.ss.usermodel.Cell cell6 = row.createCell(6);
                cell6.setCellValue(message.getReplyCount() != null ? message.getReplyCount() : 0);
                cell6.setCellStyle(dataStyle);
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 设置最大列宽，避免内容过长
                if (sheet.getColumnWidth(i) > 15000) {
                    sheet.setColumnWidth(i, 15000);
                }
            }
            
            // 将工作簿写入字节数组
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("导出留言数据失败", e);
        }
    }
    
    @Override
    public CommentDTO getMessageDetail(Long messageId) {
        Optional<Comment> commentOpt = commentRepository.findById(messageId);
        if (commentOpt.isEmpty()) {
            return null;
        }
        
        Comment comment = commentOpt.get();
        // 确保这是一条留言（article为null）
        if (comment.getArticle() != null) {
            throw new IllegalArgumentException("指定的ID不是留言");
        }
        
        CommentDTO dto = new CommentDTO(comment);
        
        // 获取该留言的所有回复
        List<Comment> replies = commentRepository.findByParent(comment);
        List<CommentDTO> replyDTOs = replies.stream()
            .map(CommentDTO::new)
            .collect(java.util.stream.Collectors.toList());
        dto.setReplies(replyDTOs);
        
        return dto;
    }
    
    @Override
    public java.util.Map<String, Object> getMessageTrend(int days) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        // 获取指定天数内的留言趋势数据
        java.time.LocalDateTime startDate = java.time.LocalDateTime.now().minusDays(days);
        java.time.LocalDateTime endDate = java.time.LocalDateTime.now();
        
        // 按日期统计留言数量
        List<Object[]> dailyStats = commentRepository.findMessageCountByDateRange(startDate, endDate);
        
        // 处理日期和数量数据
        java.util.List<String> dates = new java.util.ArrayList<>();
        java.util.List<Long> counts = new java.util.ArrayList<>();
        
        for (Object[] stat : dailyStats) {
            if (stat.length >= 2) {
                // 处理日期类型转换
                java.time.LocalDate date;
                if (stat[0] instanceof java.sql.Date) {
                    date = ((java.sql.Date) stat[0]).toLocalDate();
                } else if (stat[0] instanceof java.time.LocalDate) {
                    date = (java.time.LocalDate) stat[0];
                } else {
                    // 如果是其他类型，尝试转换为字符串再解析
                    date = java.time.LocalDate.parse(stat[0].toString());
                }
                dates.add(date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                counts.add(((Number) stat[1]).longValue());
            }
        }
        
        // 按状态统计留言数量
        Long totalApproved = commentRepository.countMessagesByStatusAndDateRange(Comment.Status.APPROVED, startDate, endDate);
        Long totalPending = commentRepository.countMessagesByStatusAndDateRange(Comment.Status.PENDING, startDate, endDate);
        Long totalRejected = commentRepository.countMessagesByStatusAndDateRange(Comment.Status.REJECTED, startDate, endDate);
        
        // 构建前端期望的数据结构
        result.put("dates", dates);
        result.put("counts", counts);
        result.put("totalApproved", totalApproved != null ? totalApproved : 0L);
        result.put("totalPending", totalPending != null ? totalPending : 0L);
        result.put("totalRejected", totalRejected != null ? totalRejected : 0L);
        
        // 添加按状态分组的计数数组（如果前端需要）
        java.util.Map<String, java.util.List<Long>> statusCounts = new java.util.HashMap<>();
        statusCounts.put("approvedCounts", new java.util.ArrayList<>());
        statusCounts.put("pendingCounts", new java.util.ArrayList<>());
        statusCounts.put("rejectedCounts", new java.util.ArrayList<>());
        result.put("statusCounts", statusCounts);
        
        return result;
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