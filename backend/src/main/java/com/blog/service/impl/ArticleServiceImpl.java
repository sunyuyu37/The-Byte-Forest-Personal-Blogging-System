package com.blog.service.impl;

import com.blog.dto.ArticleDTO;
import com.blog.dto.TagDTO;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.ArticleTagRepository;
import com.blog.repository.CategoryRepository;
import com.blog.repository.TagRepository;
import com.blog.repository.UserRepository;
import com.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.blog.entity.ArticleLike;
import com.blog.repository.ArticleLikeRepository;
import com.blog.service.NotificationService;
import com.blog.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private ArticleTagRepository articleTagRepository;
    
    @Autowired
    private ArticleLikeRepository articleLikeRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Override
    public ArticleDTO createArticle(ArticleDTO articleDTO, Long authorId) {
        // 验证必填字段
        if (articleDTO.getTitle() == null || articleDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }
        if (articleDTO.getContent() == null || articleDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("文章内容不能为空");
        }
        
        // 获取作者
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("作者不存在"));
        
        // 创建文章实体
        Article article = new Article();
        article.setTitle(articleDTO.getTitle());
        article.setSlug(generateSlug(articleDTO.getTitle()));
        article.setSummary(articleDTO.getSummary());
        article.setContent(articleDTO.getContent());
        article.setCoverImage(articleDTO.getCoverImage());
        article.setAuthor(author);
        
        // 设置分类
        if (articleDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(articleDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        }
        
        // 设置状态
        if (articleDTO.getStatus() != null) {
            try {
                article.setStatus(Article.Status.valueOf(articleDTO.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                article.setStatus(Article.Status.DRAFT);
            }
        } else {
            article.setStatus(Article.Status.DRAFT);
        }
        
        // 设置其他属性
        article.setIsTop(articleDTO.getIsTop() != null ? articleDTO.getIsTop() : false);
        article.setIsFeatured(articleDTO.getIsRecommend() != null ? articleDTO.getIsRecommend() : false);
        article.setAllowComment(articleDTO.getAllowComment() != null ? articleDTO.getAllowComment() : true);
        article.setSeoKeywords(articleDTO.getKeywords());
        article.setSeoDescription(articleDTO.getDescription());
        
        // 设置分类特定数据
        article.setCategorySpecificData(articleDTO.getCategorySpecificData());
        
        // 如果是发布状态，设置发布时间
        if (article.getStatus() == Article.Status.PUBLISHED) {
            article.setPublishedAt(LocalDateTime.now());
        }
        
        // 计算字数和阅读时间
        calculateWordCountAndReadingTime(article);
        
        // 保存文章
        Article savedArticle = articleRepository.save(article);
        
        // 处理标签
        if (articleDTO.getTags() != null && !articleDTO.getTags().isEmpty()) {
            for (TagDTO tagDTO : articleDTO.getTags()) {
                if (tagDTO.getName() != null && !tagDTO.getName().trim().isEmpty()) {
                    // 查找或创建标签
                    Tag tag = tagRepository.findByName(tagDTO.getName().trim())
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(tagDTO.getName().trim());
                                newTag.setSlug(this.generateTagSlug(tagDTO.getName().trim()));
                                newTag.setDescription(tagDTO.getDescription());
                                newTag.setColor(tagDTO.getColor());
                
                                newTag.setArticleCount(0);
                                return tagRepository.save(newTag);
                            });
                    
                    // 创建文章标签关联
                    if (!articleTagRepository.existsByArticleAndTag(savedArticle, tag)) {
                        ArticleTag articleTag = new ArticleTag(savedArticle, tag);
                        articleTagRepository.save(articleTag);
                        
                        // 更新标签的文章数量
                        tag.setArticleCount(tag.getArticleCount() + 1);
                        tagRepository.save(tag);
                    }
                }
            }
        }
        
        // 重新加载文章以获取完整的标签信息
        Article articleWithTags = articleRepository.findById(savedArticle.getId()).orElse(savedArticle);
        return new ArticleDTO(articleWithTags);
    }
    
    @Override
    public ArticleDTO updateArticle(Long id, ArticleDTO articleDTO, Long authorId) {
        // 查找现有文章
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        // 验证作者权限（可选，根据业务需求）
        // if (!article.getAuthor().getId().equals(authorId)) {
        //     throw new RuntimeException("无权限修改此文章");
        // }
        
        // 验证必填字段
        if (articleDTO.getTitle() == null || articleDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }
        if (articleDTO.getContent() == null || articleDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("文章内容不能为空");
        }
        
        // 更新文章属性
        article.setTitle(articleDTO.getTitle());
        article.setSummary(articleDTO.getSummary());
        article.setContent(articleDTO.getContent());
        article.setCoverImage(articleDTO.getCoverImage());
        
        // 更新分类
        if (articleDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(articleDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        } else {
            article.setCategory(null);
        }
        
        // 更新状态
        Article.Status oldStatus = article.getStatus();
        Article.Status newStatus = oldStatus;
        if (articleDTO.getStatus() != null) {
            try {
                newStatus = Article.Status.valueOf(articleDTO.getStatus().toUpperCase());
                article.setStatus(newStatus);
                
                // 如果从非发布状态变为发布状态，设置发布时间
                if (oldStatus != Article.Status.PUBLISHED && newStatus == Article.Status.PUBLISHED) {
                    article.setPublishedAt(LocalDateTime.now());
                }
            } catch (IllegalArgumentException e) {
                // 保持原状态
                newStatus = oldStatus;
            }
        }
        
        // 更新其他属性
        article.setIsTop(articleDTO.getIsTop() != null ? articleDTO.getIsTop() : false);
        article.setIsFeatured(articleDTO.getIsRecommend() != null ? articleDTO.getIsRecommend() : false);
        article.setAllowComment(articleDTO.getAllowComment() != null ? articleDTO.getAllowComment() : true);
        article.setSeoKeywords(articleDTO.getKeywords());
        article.setSeoDescription(articleDTO.getDescription());
        
        // 更新分类特定数据
        article.setCategorySpecificData(articleDTO.getCategorySpecificData());
        
        // 重新计算字数和阅读时间
        calculateWordCountAndReadingTime(article);
        
        // 保存更新
        Article savedArticle = articleRepository.save(article);
        
        // 处理状态变更时的标签文章数更新
        if (oldStatus != newStatus) {
            List<ArticleTag> articleTags = articleTagRepository.findByArticle(savedArticle);
            
            if (oldStatus != Article.Status.PUBLISHED && newStatus == Article.Status.PUBLISHED) {
                // 从非发布状态变为发布状态，增加标签文章数
                for (ArticleTag articleTag : articleTags) {
                    Tag tag = articleTag.getTag();
                    tag.setArticleCount(tag.getArticleCount() + 1);
                    tagRepository.save(tag);
                }
            } else if (oldStatus == Article.Status.PUBLISHED && newStatus != Article.Status.PUBLISHED) {
                // 从发布状态变为非发布状态，减少标签文章数
                for (ArticleTag articleTag : articleTags) {
                    Tag tag = articleTag.getTag();
                    if (tag.getArticleCount() > 0) {
                        tag.setArticleCount(tag.getArticleCount() - 1);
                        tagRepository.save(tag);
                    }
                }
            }
        }
        
        // 处理标签更新
        if (articleDTO.getTags() != null) {
            // 获取文章当前的所有标签
            List<ArticleTag> currentArticleTags = articleTagRepository.findByArticle(savedArticle);
            Set<String> currentTagNames = currentArticleTags.stream()
                    .map(at -> at.getTag().getName())
                    .collect(Collectors.toSet());
            
            Set<String> newTagNames = articleDTO.getTags().stream()
                    .map(TagDTO::getName)
                    .filter(name -> name != null && !name.trim().isEmpty())
                    .map(String::trim)
                    .collect(Collectors.toSet());
            
            // 删除不再需要的标签关联
            for (ArticleTag articleTag : currentArticleTags) {
                String tagName = articleTag.getTag().getName();
                if (!newTagNames.contains(tagName)) {
                    // 删除文章标签关联
                    articleTagRepository.delete(articleTag);
                    
                    // 只有已发布的文章才减少标签的文章数量
                    if (savedArticle.getStatus() == Article.Status.PUBLISHED) {
                        Tag tag = articleTag.getTag();
                        tag.setArticleCount(Math.max(0, tag.getArticleCount() - 1));
                        tagRepository.save(tag);
                    }
                }
            }
            
            // 添加新的标签关联
            for (TagDTO tagDTO : articleDTO.getTags()) {
                if (tagDTO.getName() != null && !tagDTO.getName().trim().isEmpty()) {
                    String tagName = tagDTO.getName().trim();
                    if (!currentTagNames.contains(tagName)) {
                        // 查找或创建标签
                        Tag tag = tagRepository.findByName(tagName)
                                .orElseGet(() -> {
                                    Tag newTag = new Tag();
                                    newTag.setName(tagName);
                                    newTag.setSlug(this.generateTagSlug(tagName));
                                    newTag.setDescription(tagDTO.getDescription());
                                    newTag.setColor(tagDTO.getColor());

                                    newTag.setArticleCount(0);
                                    return tagRepository.save(newTag);
                                });
                        
                        // 创建文章标签关联
                        if (!articleTagRepository.existsByArticleAndTag(savedArticle, tag)) {
                            ArticleTag articleTag = new ArticleTag(savedArticle, tag);
                            articleTagRepository.save(articleTag);
                            
                            // 只有已发布的文章才增加标签的文章数量
                            if (savedArticle.getStatus() == Article.Status.PUBLISHED) {
                                tag.setArticleCount(tag.getArticleCount() + 1);
                                tagRepository.save(tag);
                            }
                        }
                    }
                }
            }
        }
        
        // 重新加载文章以获取完整的标签信息
        Article articleWithTags = articleRepository.findById(savedArticle.getId()).orElse(savedArticle);
        return new ArticleDTO(articleWithTags);
    }
    
    @Override
    public Optional<ArticleDTO> findById(Long id) {
        return articleRepository.findById(id)
                .map(ArticleDTO::new);
    }
    
    @Override
    public Optional<ArticleDTO> findBySlug(String slug) {
        return articleRepository.findBySlug(slug)
                .map(ArticleDTO::new);
    }
    
    @Override
    public Page<ArticleDTO> findPublishedArticles(Pageable pageable) {
        return articleRepository.findPublishedArticles(pageable)
                .map(ArticleDTO::new);
    }
    
    @Override
    public Page<ArticleDTO> findAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable)
                .map(ArticleDTO::new);
    }
    
    @Override
    public Page<ArticleDTO> findArticlesByAuthor(User author, Pageable pageable) {
        return articleRepository.findByAuthor(author, pageable)
                .map(ArticleDTO::new);
    }
    
    @Override
    public Page<ArticleDTO> findArticlesByCategory(Category category, Pageable pageable) {
        return articleRepository.findByCategory(category, pageable)
                .map(ArticleDTO::new);
    }
    
    @Override
    public Page<ArticleDTO> getArticlesByCategory(Long categoryId, Pageable pageable) {
        return categoryRepository.findById(categoryId)
                .map(category -> articleRepository.findByCategory(category, pageable)
                        .map(ArticleDTO::new))
                .orElse(Page.empty());
    }
    
    @Override
    public Page<ArticleDTO> findArticlesByTag(Tag tag, Pageable pageable) {
        List<Article> articles = articleTagRepository.findArticlesByTag(tag);
        // 过滤已发布的文章并创建 Page
        List<ArticleDTO> articleDTOs = articles.stream()
                .filter(article -> article.getStatus() == Article.Status.PUBLISHED)
                .map(this::convertToDTO)
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
        
        long total = articles.stream()
                .filter(article -> article.getStatus() == Article.Status.PUBLISHED)
                .count();
        
        return new org.springframework.data.domain.PageImpl<>(articleDTOs, pageable, total);
    }
    
    @Override
    public Page<ArticleDTO> getArticlesByTag(Long tagId, Pageable pageable) {
        return tagRepository.findById(tagId)
                .map(tag -> findArticlesByTag(tag, pageable))
                .orElse(Page.empty());
    }
    
    @Override
    public Page<ArticleDTO> searchArticles(String keyword, Pageable pageable) {
        return articleRepository.searchArticles(keyword, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public Page<ArticleDTO> searchArticles(String keyword, String searchType, String category, String timeRange, Pageable pageable) {
        LocalDateTime startDate = null;
        
        // 处理时间范围
        if (timeRange != null && !timeRange.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            switch (timeRange) {
                case "week":
                    startDate = now.minusWeeks(1);
                    break;
                case "month":
                    startDate = now.minusMonths(1);
                    break;
                case "quarter":
                    startDate = now.minusMonths(3);
                    break;
                case "year":
                    startDate = now.minusYears(1);
                    break;
            }
        }
        
        Page<Article> articles;
        
        // 根据搜索条件组合查询
        if (category != null && !category.isEmpty() && startDate != null) {
            // 分类 + 时间范围
            articles = articleRepository.searchArticlesByCategoryAndTimeRange(keyword, category, startDate, pageable);
        } else if (category != null && !category.isEmpty()) {
            // 仅分类
            articles = articleRepository.searchArticlesByCategory(keyword, category, pageable);
        } else if (startDate != null) {
            // 仅时间范围
            articles = articleRepository.searchArticlesByTimeRange(keyword, startDate, pageable);
        } else {
            // 根据搜索类型
            switch (searchType) {
                case "title":
                    articles = articleRepository.searchArticlesByTitle(keyword, pageable);
                    break;
                case "content":
                    articles = articleRepository.searchArticlesByContent(keyword, pageable);
                    break;
                default: // all
                    articles = articleRepository.searchArticles(keyword, pageable);
                    break;
            }
        }
        
        return articles.map(this::convertToDTO);
    }
    
    @Override
    public List<ArticleDTO> getTopArticles() {
        return articleRepository.findTopArticles()
                .stream()
                .map(ArticleDTO::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<ArticleDTO> getFeaturedArticles(Pageable pageable) {
        return articleRepository.findFeaturedArticles(pageable)
                .map(ArticleDTO::new);
    }
    
    @Override
    public Page<ArticleDTO> getMostViewedArticles(Pageable pageable) {
        return articleRepository.findMostViewedArticles(pageable)
                .map(ArticleDTO::new);
    }
    
    @Override
    public Page<ArticleDTO> getMostLikedArticles(Pageable pageable) {
        return articleRepository.findMostLikedArticles(pageable)
                .map(ArticleDTO::new);
    }
    
    @Override
    public Page<ArticleDTO> getRecentArticles(Pageable pageable) {
        return articleRepository.findPublishedArticles(pageable)
                .map(ArticleDTO::new);
    }
    
    @Override
    public ArticleDTO publishArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        // 如果文章之前不是已发布状态，需要增加相关标签的文章数量
        boolean wasNotPublished = article.getStatus() != Article.Status.PUBLISHED;
        
        article.setStatus(Article.Status.PUBLISHED);
        if (article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        Article savedArticle = articleRepository.save(article);
        
        // 如果文章之前不是已发布状态，增加标签的文章数量
        if (wasNotPublished) {
            List<ArticleTag> articleTags = articleTagRepository.findByArticle(savedArticle);
            for (ArticleTag articleTag : articleTags) {
                Tag tag = articleTag.getTag();
                tag.setArticleCount(tag.getArticleCount() + 1);
                tagRepository.save(tag);
            }
        }
        
        return new ArticleDTO(savedArticle);
    }
    
    @Override
    public ArticleDTO unpublishArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        // 如果文章之前是已发布状态，需要减少相关标签的文章数量
        boolean wasPublished = article.getStatus() == Article.Status.PUBLISHED;
        
        article.setStatus(Article.Status.DRAFT);
        Article savedArticle = articleRepository.save(article);
        
        // 如果文章之前是已发布状态，减少标签的文章数量
        if (wasPublished) {
            List<ArticleTag> articleTags = articleTagRepository.findByArticle(savedArticle);
            for (ArticleTag articleTag : articleTags) {
                Tag tag = articleTag.getTag();
                if (tag.getArticleCount() > 0) {
                    tag.setArticleCount(tag.getArticleCount() - 1);
                    tagRepository.save(tag);
                }
            }
        }
        
        return new ArticleDTO(savedArticle);
    }
    
    @Override
    public void setTopArticle(Long id, boolean isTop) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setIsTop(isTop);
            articleRepository.save(article);
        });
    }
    
    @Override
    public void setFeaturedArticle(Long id, boolean isFeatured) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setIsFeatured(isFeatured);
            articleRepository.save(article);
        });
    }
    
    @Override
    public void likeArticle(Long articleId, Long userId) {
        if (articleId == null || userId == null) {
            throw new IllegalArgumentException("文章ID或用户ID不能为空");
        }
        
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (articleOpt.isEmpty()) {
            throw new IllegalArgumentException("文章不存在");
        }
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        Article article = articleOpt.get();
        User user = userOpt.get();
        
        // 检查是否已经点赞
        if (articleLikeRepository.existsByArticleAndUser(article, user)) {
            return; // 已经点赞，直接返回
        }
        
        // 保存点赞记录
        ArticleLike articleLike = new ArticleLike(article, user);
        articleLikeRepository.save(articleLike);
        
        // 增加文章点赞数
        article.setLikeCount(article.getLikeCount() + 1);
        articleRepository.save(article);
        
        // 创建点赞通知，通知文章作者（不通知自己）
        try {
            if (article.getAuthor() != null && !article.getAuthor().getId().equals(user.getId())) {
                String likerName = user.getNickname() != null ? user.getNickname() : user.getUsername();
                String title = "文章获得新点赞";
                String content = String.format("用户\"%s\"点赞了你的文章\"%s\"", likerName, article.getTitle());
                // 关联ID使用点赞记录ID，类型标记为 article_like
                notificationService.createNotification(title, content, "like", article.getAuthor().getId(), articleLike.getId(), "article_like");
            }
        } catch (Exception e) {
            // 通知失败不影响主流程
        }
    }
    
    @Override
    public void unlikeArticle(Long articleId, Long userId) {
        if (articleId == null || userId == null) {
            throw new IllegalArgumentException("文章ID或用户ID不能为空");
        }
        
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (articleOpt.isEmpty()) {
            throw new IllegalArgumentException("文章不存在");
        }
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        Article article = articleOpt.get();
        User user = userOpt.get();
        
        // 检查是否已经点赞
        if (!articleLikeRepository.existsByArticleAndUser(article, user)) {
            // 尚未点赞，无需进一步操作
            return;
        }
        
        // 先查出点赞记录以获取其ID
        Long likeId = articleLikeRepository.findByArticleAndUser(article, user)
                .map(ArticleLike::getId)
                .orElse(null);
        
        // 删除点赞记录
        articleLikeRepository.deleteByArticleAndUser(article, user);
        
        // 减少文章点赞数
        article.setLikeCount(Math.max(0, article.getLikeCount() - 1));
        articleRepository.save(article);
        
        // 删除关联的点赞通知（如果有）
        try {
            if (likeId != null) {
                notificationRepository.deleteByRelatedTypeAndRelatedId("article_like", likeId);
            }
        } catch (Exception e) {
            // 删除通知失败不影响主流程
        }
    }
    
    @Override
    public boolean isArticleLikedByUser(Long articleId, Long userId) {
        // 如果文章ID或用户ID为null，直接返回false
        if (articleId == null || userId == null) {
            return false;
        }
        
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        // 如果文章或用户不存在，返回false而不是抛出异常
        if (articleOpt.isEmpty() || userOpt.isEmpty()) {
            return false;
        }
        
        return articleLikeRepository.existsByArticleAndUser(articleOpt.get(), userOpt.get());
    }
    
    @Override
    public void addTagsToArticle(Long articleId, List<Long> tagIds) {
        // TODO: 实现添加标签逻辑
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public void removeTagsFromArticle(Long articleId, List<Long> tagIds) {
        // TODO: 实现移除标签逻辑
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public long countArticlesByAuthor(User author) {
        return articleRepository.countByAuthor(author);
    }
    
    @Override
    public String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "untitled";
        }
        
        // 生成基础slug
        String baseSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]+", "-") // 支持中文
                .replaceAll("^-+|-+$", "") // 移除开头和结尾的连字符
                .replaceAll("-+", "-"); // 合并多个连字符
        
        if (baseSlug.isEmpty()) {
            baseSlug = "untitled";
        }
        
        // 检查slug是否已存在，如果存在则添加数字后缀
        String slug = baseSlug;
        int counter = 1;
        while (articleRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }

    private String generateTagSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "tag";
        }
        
        // 生成基础slug
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]+", "-") // 支持中文
                .replaceAll("^-+|-+$", "") // 移除开头和结尾的连字符
                .replaceAll("-+", "-"); // 合并多个连字符
        
        if (baseSlug.isEmpty()) {
            baseSlug = "tag";
        }
        
        // 检查标签slug是否已存在，如果存在则添加数字后缀
        String slug = baseSlug;
        int counter = 1;
        while (tagRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }

    @Override
    public void calculateWordCountAndReadingTime(Article article) {
        if (article.getContent() != null && !article.getContent().trim().isEmpty()) {
            String content = article.getContent();
            
            // 移除HTML标签和Markdown标记
            String plainText = content
                    .replaceAll("<[^>]+>", "") // 移除HTML标签
                    .replaceAll("\\*\\*([^*]+)\\*\\*", "$1") // 移除粗体标记
                    .replaceAll("\\*([^*]+)\\*", "$1") // 移除斜体标记
                    .replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1") // 移除链接，保留文本
                    .replaceAll("#+\\s*", "") // 移除标题标记
                    .replaceAll("```[\\s\\S]*?```", "") // 移除代码块
                    .replaceAll("`([^`]+)`", "$1") // 移除行内代码标记
                    .trim();
            
            // 计算字数（中英文混合）
            int wordCount = 0;
            if (!plainText.isEmpty()) {
                // 中文字符数
                int chineseCount = plainText.replaceAll("[^\\u4e00-\\u9fa5]", "").length();
                // 英文单词数
                String englishText = plainText.replaceAll("[\\u4e00-\\u9fa5]", " ");
                int englishWordCount = englishText.trim().isEmpty() ? 0 : englishText.trim().split("\\s+").length;
                
                wordCount = chineseCount + englishWordCount;
            }
            
            article.setWordCount(wordCount);
            
            // 计算阅读时间（中文每分钟300字，英文每分钟200词）
            int readingTime = Math.max(1, (int) Math.ceil(wordCount / 250.0));
            article.setReadingTime(readingTime);
        } else {
            article.setWordCount(0);
            article.setReadingTime(1);
        }
    }
    
    @Override
    public void deleteArticle(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            // 获取文章的所有标签
            List<ArticleTag> articleTags = articleTagRepository.findByArticle(article);
            
            // 只有已发布的文章被删除时才减少标签的文章数量
            if (article.getStatus() == Article.Status.PUBLISHED) {
                for (ArticleTag articleTag : articleTags) {
                    Tag tag = articleTag.getTag();
                    if (tag.getArticleCount() > 0) {
                        tag.setArticleCount(tag.getArticleCount() - 1);
                        tagRepository.save(tag);
                    }
                }
            }
            
            // 硬删除
            articleRepository.delete(article);
        });
    }
    
    @Override
    public void incrementViewCount(Long id) {
        articleRepository.incrementViewCount(id);
    }
    
    public void incrementLikeCount(Long id) {
        articleRepository.incrementLikeCount(id);
    }
    
    public void decrementLikeCount(Long id) {
        articleRepository.decrementLikeCount(id);
    }
    
    public void incrementCommentCount(Long id) {
        articleRepository.incrementCommentCount(id);
    }
    
    public void decrementCommentCount(Long id) {
        articleRepository.decrementCommentCount(id);
    }
    
    @Override
    public long countAllArticles() {
        return articleRepository.countAllArticles();
    }
    
    @Override
    public long countPublishedArticles() {
        return articleRepository.countPublishedArticles();
    }
    
    @Override
    public long getTotalViewCount() {
        return articleRepository.getTotalViewCount();
    }
    
    @Override
    public List<ArticleDTO> getRecentArticles(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return articleRepository.findRecentArticlesLimit(pageable)
                .stream()
                .map(ArticleDTO::new)
                .collect(Collectors.toList());
    }
    
    private ArticleDTO convertToDTO(Article article) {
        return new ArticleDTO(article);
    }
    
    // ========== 管理员专用方法实现 ==========
    
    @Override
    public Page<ArticleDTO> getAllArticles(Pageable pageable) {
        return articleRepository.findAllArticles(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public Page<ArticleDTO> searchArticlesForAdmin(String keyword, String status, String category, Pageable pageable) {
        return articleRepository.searchArticlesForAdmin(keyword, status, category, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public Page<ArticleDTO> getArticlesByStatus(String status, Pageable pageable) {
        return articleRepository.findByStatus(status, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public Page<ArticleDTO> getArticlesByCategoryName(String categoryName, Pageable pageable) {
        return articleRepository.findByCategoryName(categoryName, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public void batchDeleteArticles(List<Long> ids) {
        // 先更新每篇文章相关标签的文章数量
        for (Long id : ids) {
            articleRepository.findById(id).ifPresent(article -> {
                // 获取文章的所有标签
                List<ArticleTag> articleTags = articleTagRepository.findByArticle(article);
                
                // 只有已发布的文章被删除时才减少标签的文章数量
                if (article.getStatus() == Article.Status.PUBLISHED) {
                    for (ArticleTag articleTag : articleTags) {
                        Tag tag = articleTag.getTag();
                        if (tag.getArticleCount() > 0) {
                            tag.setArticleCount(tag.getArticleCount() - 1);
                            tagRepository.save(tag);
                        }
                    }
                }
            });
        }
        
        // 批量硬删除文章
        articleRepository.deleteAllById(ids);
    }
    
    @Override
    @Transactional
    public void batchPublishArticles(List<Long> ids) {
        // 先处理标签文章数量更新
        for (Long id : ids) {
            articleRepository.findById(id).ifPresent(article -> {
                // 如果文章之前不是已发布状态，需要增加相关标签的文章数量
                if (article.getStatus() != Article.Status.PUBLISHED) {
                    List<ArticleTag> articleTags = articleTagRepository.findByArticle(article);
                    for (ArticleTag articleTag : articleTags) {
                        Tag tag = articleTag.getTag();
                        tag.setArticleCount(tag.getArticleCount() + 1);
                        tagRepository.save(tag);
                    }
                }
                
                // 更新发布时间
                if (article.getPublishedAt() == null) {
                    article.setPublishedAt(LocalDateTime.now());
                    articleRepository.save(article);
                }
            });
        }
        
        // 批量更新状态
        articleRepository.batchUpdateStatus(ids, "PUBLISHED");
    }
    
    @Override
    @Transactional
    public void batchUnpublishArticles(List<Long> ids) {
        // 先处理标签文章数量更新
        for (Long id : ids) {
            articleRepository.findById(id).ifPresent(article -> {
                // 如果文章之前是已发布状态，需要减少相关标签的文章数量
                if (article.getStatus() == Article.Status.PUBLISHED) {
                    List<ArticleTag> articleTags = articleTagRepository.findByArticle(article);
                    for (ArticleTag articleTag : articleTags) {
                        Tag tag = articleTag.getTag();
                        if (tag.getArticleCount() > 0) {
                            tag.setArticleCount(tag.getArticleCount() - 1);
                            tagRepository.save(tag);
                        }
                    }
                }
            });
        }
        
        // 批量更新状态
        articleRepository.batchUpdateStatus(ids, "DRAFT");
    }
    
    @Override
    @Transactional
    public ArticleDTO topArticle(Long id) {
        articleRepository.updateTopStatus(id, true);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        return new ArticleDTO(article);
    }
    
    @Override
    @Transactional
    public ArticleDTO untopArticle(Long id) {
        articleRepository.updateTopStatus(id, false);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        return new ArticleDTO(article);
    }
    
    @Override
    @Transactional
    public ArticleDTO featureArticle(Long id) {
        articleRepository.updateFeaturedStatus(id, true);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        return new ArticleDTO(article);
    }
    
    @Override
    @Transactional
    public ArticleDTO unfeatureArticle(Long id) {
        articleRepository.updateFeaturedStatus(id, false);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        return new ArticleDTO(article);
    }
    
    // ========== 图表数据方法实现 ==========
    
    @Override
    public List<Object[]> getArticleTrendData(String period) {
        LocalDateTime startDate;
        switch (period.toLowerCase()) {
            case "week":
                startDate = LocalDateTime.now().minusWeeks(1);
                break;
            case "month":
                startDate = LocalDateTime.now().minusMonths(1);
                break;
            case "year":
                startDate = LocalDateTime.now().minusYears(1);
                break;
            default:
                startDate = LocalDateTime.now().minusMonths(1);
        }
        return articleRepository.getArticleCountByDate(startDate);
    }
    
    @Override
    public List<Object[]> getArticleCategoryStats() {
        return articleRepository.getArticleCountByCategory();
    }
    
    @Override
    public List<Object[]> getArticleViewTrendData(String period) {
        LocalDateTime startDate;
        switch (period.toLowerCase()) {
            case "week":
                startDate = LocalDateTime.now().minusWeeks(1);
                break;
            case "month":
                startDate = LocalDateTime.now().minusMonths(1);
                break;
            case "year":
                startDate = LocalDateTime.now().minusYears(1);
                break;
            default:
                startDate = LocalDateTime.now().minusMonths(1);
        }
        return articleRepository.getViewCountByDate(startDate);
    }
    
    @Override
    public List<Object[]> getPopularArticlesData(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return articleRepository.getPopularArticlesData(pageable);
    }
}