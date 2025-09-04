-- 数据库索引优化脚本
-- 为关键查询字段添加索引以提升性能

-- ========================================
-- 用户表索引
-- ========================================

-- 用户名和邮箱已有唯一索引，添加其他常用查询字段索引
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_email_verified ON users(email_verified);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_last_login_time ON users(last_login_time);
CREATE INDEX idx_users_deleted ON users(deleted);

-- ========================================
-- 文章表索引
-- ========================================

-- 文章slug已有唯一索引，添加其他关键查询字段索引
CREATE INDEX idx_article_status ON article(status);
CREATE INDEX idx_article_author_id ON article(author_id);
CREATE INDEX idx_article_category_id ON article(category_id);
CREATE INDEX idx_article_is_top ON article(is_top);
CREATE INDEX idx_article_is_featured ON article(is_featured);
CREATE INDEX idx_article_published_at ON article(published_at);
CREATE INDEX idx_article_created_at ON article(created_at);
CREATE INDEX idx_article_view_count ON article(view_count);
CREATE INDEX idx_article_like_count ON article(like_count);

-- 复合索引用于常见查询组合
CREATE INDEX idx_article_status_published_at ON article(status, published_at DESC);
CREATE INDEX idx_article_status_is_top ON article(status, is_top, published_at DESC);
CREATE INDEX idx_article_status_is_featured ON article(status, is_featured, published_at DESC);
CREATE INDEX idx_article_author_status ON article(author_id, status, created_at DESC);
CREATE INDEX idx_article_category_status ON article(category_id, status, published_at DESC);

-- 全文搜索索引（MySQL）
-- CREATE FULLTEXT INDEX idx_article_fulltext ON article(title, summary, content);

-- ========================================
-- 分类表索引
-- ========================================

-- 分类name和slug已有唯一索引，添加其他查询字段索引
CREATE INDEX idx_category_status ON category(status);
CREATE INDEX idx_category_sort_order ON category(sort_order);
CREATE INDEX idx_category_created_at ON category(created_at);
CREATE INDEX idx_category_status_sort_order ON category(status, sort_order, name);

-- ========================================
-- 标签表索引
-- ========================================

-- 标签name和slug已有唯一索引，添加其他查询字段索引
CREATE INDEX idx_tag_article_count ON tag(article_count DESC);
CREATE INDEX idx_tag_created_at ON tag(created_at);

-- ========================================
-- 评论表索引
-- ========================================

CREATE INDEX idx_comment_article_id ON comment(article_id);
CREATE INDEX idx_comment_user_id ON comment(user_id);
CREATE INDEX idx_comment_parent_id ON comment(parent_id);
CREATE INDEX idx_comment_reply_to_id ON comment(reply_to_id);
CREATE INDEX idx_comment_status ON comment(status);
CREATE INDEX idx_comment_created_at ON comment(created_at);
CREATE INDEX idx_comment_deleted ON comment(deleted);

-- 复合索引用于常见查询组合
CREATE INDEX idx_comment_article_status ON comment(article_id, status, created_at DESC);
CREATE INDEX idx_comment_user_created ON comment(user_id, created_at DESC);
CREATE INDEX idx_comment_parent_created ON comment(parent_id, created_at ASC);

-- ========================================
-- 文章标签关联表索引
-- ========================================

CREATE INDEX idx_article_tag_article_id ON article_tag(article_id);
CREATE INDEX idx_article_tag_tag_id ON article_tag(tag_id);
CREATE INDEX idx_article_tag_created_at ON article_tag(created_at);

-- 复合索引用于唯一性约束和查询优化
CREATE UNIQUE INDEX idx_article_tag_unique ON article_tag(article_id, tag_id);

-- ========================================
-- 文章点赞表索引
-- ========================================

CREATE INDEX idx_article_like_article_id ON article_like(article_id);
CREATE INDEX idx_article_like_user_id ON article_like(user_id);
CREATE INDEX idx_article_like_created_at ON article_like(created_at);

-- 复合索引用于唯一性约束和查询优化
CREATE UNIQUE INDEX idx_article_like_unique ON article_like(article_id, user_id);

-- ========================================
-- 评论点赞表索引（如果存在）
-- ========================================

-- 假设存在评论点赞表
-- CREATE INDEX idx_comment_like_comment_id ON comment_like(comment_id);
-- CREATE INDEX idx_comment_like_user_id ON comment_like(user_id);
-- CREATE UNIQUE INDEX idx_comment_like_unique ON comment_like(comment_id, user_id);

-- ========================================
-- 通知表索引
-- ========================================

CREATE INDEX idx_notification_user_id ON personal_blog_notification(user_id);
CREATE INDEX idx_notification_type ON personal_blog_notification(type);
CREATE INDEX idx_notification_is_read ON personal_blog_notification(is_read);
CREATE INDEX idx_notification_created_at ON personal_blog_notification(created_at);
CREATE INDEX idx_notification_related_id ON personal_blog_notification(related_id);
CREATE INDEX idx_notification_related_type ON personal_blog_notification(related_type);

-- 复合索引用于常见查询组合
CREATE INDEX idx_notification_user_read ON personal_blog_notification(user_id, is_read, created_at DESC);
CREATE INDEX idx_notification_user_type ON personal_blog_notification(user_id, type, created_at DESC);

-- ========================================
-- 公告表索引
-- ========================================

CREATE INDEX idx_announcement_type ON personal_blog_announcement(type);
CREATE INDEX idx_announcement_priority ON personal_blog_announcement(priority);
CREATE INDEX idx_announcement_enabled ON personal_blog_announcement(enabled);
CREATE INDEX idx_announcement_pinned ON personal_blog_announcement(pinned);
CREATE INDEX idx_announcement_publisher_id ON personal_blog_announcement(publisher_id);
CREATE INDEX idx_announcement_start_time ON personal_blog_announcement(start_time);
CREATE INDEX idx_announcement_end_time ON personal_blog_announcement(end_time);
CREATE INDEX idx_announcement_created_at ON personal_blog_announcement(created_at);
CREATE INDEX idx_announcement_deleted ON personal_blog_announcement(deleted);

-- 复合索引用于常见查询组合
CREATE INDEX idx_announcement_enabled_pinned ON personal_blog_announcement(enabled, pinned, created_at DESC);
CREATE INDEX idx_announcement_type_enabled ON personal_blog_announcement(type, enabled, created_at DESC);
CREATE INDEX idx_announcement_time_range ON personal_blog_announcement(enabled, start_time, end_time);

-- ========================================
-- 登录日志表索引
-- ========================================

CREATE INDEX idx_login_logs_user_id ON login_logs(user_id);
CREATE INDEX idx_login_logs_ip_address ON login_logs(ip_address);
CREATE INDEX idx_login_logs_login_time ON login_logs(login_time);
CREATE INDEX idx_login_logs_session_id ON login_logs(session_id);
CREATE INDEX idx_login_logs_is_current_session ON login_logs(is_current_session);
CREATE INDEX idx_login_logs_login_status ON login_logs(login_status);
CREATE INDEX idx_login_logs_created_at ON login_logs(created_at);

-- 复合索引用于常见查询组合
CREATE INDEX idx_login_logs_user_time ON login_logs(user_id, login_time DESC);
CREATE INDEX idx_login_logs_user_current ON login_logs(user_id, is_current_session);
CREATE INDEX idx_login_logs_status_time ON login_logs(login_status, login_time DESC);

-- ========================================
-- 安全设置表索引
-- ========================================

-- user_id已有唯一索引，添加其他查询字段索引
CREATE INDEX idx_security_settings_two_factor_enabled ON security_settings(two_factor_enabled);
CREATE INDEX idx_security_settings_two_factor_method ON security_settings(two_factor_method);
CREATE INDEX idx_security_settings_password_change_required ON security_settings(password_change_required);
CREATE INDEX idx_security_settings_account_locked_until ON security_settings(account_locked_until);
CREATE INDEX idx_security_settings_failed_login_attempts ON security_settings(failed_login_attempts);
CREATE INDEX idx_security_settings_created_at ON security_settings(created_at);

-- ========================================
-- 用户公告阅读记录表索引（如果存在）
-- ========================================

-- 假设存在用户公告阅读记录表
-- CREATE INDEX idx_user_announcement_read_user_id ON user_announcement_read(user_id);
-- CREATE INDEX idx_user_announcement_read_announcement_id ON user_announcement_read(announcement_id);
-- CREATE INDEX idx_user_announcement_read_read_at ON user_announcement_read(read_at);
-- CREATE UNIQUE INDEX idx_user_announcement_read_unique ON user_announcement_read(user_id, announcement_id);

-- ========================================
-- 文件信息表索引（如果存在）
-- ========================================

-- 假设存在文件信息表
-- CREATE INDEX idx_file_info_user_id ON file_info(user_id);
-- CREATE INDEX idx_file_info_file_type ON file_info(file_type);
-- CREATE INDEX idx_file_info_created_at ON file_info(created_at);
-- CREATE INDEX idx_file_info_file_size ON file_info(file_size);

-- ========================================
-- 性能优化建议
-- ========================================

-- 1. 定期分析表统计信息
-- ANALYZE TABLE table_name;

-- 2. 监控慢查询日志
-- SET GLOBAL slow_query_log = 'ON';
-- SET GLOBAL long_query_time = 2;

-- 3. 定期优化表
-- OPTIMIZE TABLE table_name;

-- 4. 考虑分区表（对于大数据量表）
-- 例如：按时间分区登录日志表

-- 5. 使用EXPLAIN分析查询执行计划
-- EXPLAIN SELECT * FROM article WHERE status = 'PUBLISHED';

COMMIT;