# 数据库索引优化说明

## 概述

本文档详细说明了为个人博客系统数据库添加的索引优化方案，旨在提升查询性能和用户体验。

## 索引优化策略

### 1. 用户表 (users)

#### 单列索引
- `idx_users_status`: 用户状态查询（活跃用户统计）
- `idx_users_role`: 角色权限查询（管理员筛选）
- `idx_users_email_verified`: 邮箱验证状态查询
- `idx_users_created_at`: 用户注册时间排序
- `idx_users_last_login_time`: 最近登录时间查询
- `idx_users_deleted`: 软删除状态查询

#### 优化效果
- 提升用户管理页面的筛选和排序性能
- 加速用户统计查询
- 优化权限验证查询

### 2. 文章表 (article)

#### 单列索引
- `idx_article_status`: 文章状态查询（已发布/草稿）
- `idx_article_author_id`: 作者文章查询
- `idx_article_category_id`: 分类文章查询
- `idx_article_is_top`: 置顶文章查询
- `idx_article_is_featured`: 推荐文章查询
- `idx_article_published_at`: 发布时间排序
- `idx_article_view_count`: 热门文章排序
- `idx_article_like_count`: 点赞排序

#### 复合索引
- `idx_article_status_published_at`: 已发布文章按时间排序
- `idx_article_status_is_top`: 置顶文章优先显示
- `idx_article_status_is_featured`: 推荐文章查询
- `idx_article_author_status`: 作者文章管理
- `idx_article_category_status`: 分类页面文章列表

#### 优化效果
- 显著提升首页文章列表加载速度
- 优化分类页面和作者页面性能
- 加速文章搜索和筛选功能

### 3. 分类表 (category)

#### 单列索引
- `idx_category_status`: 活跃分类查询
- `idx_category_sort_order`: 分类排序
- `idx_category_created_at`: 创建时间排序

#### 复合索引
- `idx_category_status_sort_order`: 活跃分类按排序显示

#### 优化效果
- 提升导航菜单加载性能
- 优化分类管理页面排序

### 4. 标签表 (tag)

#### 单列索引
- `idx_tag_article_count`: 热门标签排序
- `idx_tag_created_at`: 最新标签查询

#### 优化效果
- 加速标签云生成
- 优化热门标签推荐

### 5. 评论表 (comment)

#### 单列索引
- `idx_comment_article_id`: 文章评论查询
- `idx_comment_user_id`: 用户评论历史
- `idx_comment_parent_id`: 回复评论查询
- `idx_comment_status`: 评论状态筛选
- `idx_comment_created_at`: 评论时间排序

#### 复合索引
- `idx_comment_article_status`: 文章有效评论查询
- `idx_comment_user_created`: 用户评论时间线
- `idx_comment_parent_created`: 评论回复树形结构

#### 优化效果
- 显著提升文章评论区加载速度
- 优化评论管理和审核功能
- 加速用户评论历史查询

### 6. 文章标签关联表 (article_tag)

#### 索引设计
- `idx_article_tag_article_id`: 文章标签查询
- `idx_article_tag_tag_id`: 标签文章查询
- `idx_article_tag_unique`: 防重复关联

#### 优化效果
- 提升标签页面文章列表性能
- 优化文章标签显示
- 确保数据一致性

### 7. 文章点赞表 (article_like)

#### 索引设计
- `idx_article_like_article_id`: 文章点赞统计
- `idx_article_like_user_id`: 用户点赞历史
- `idx_article_like_unique`: 防重复点赞

#### 优化效果
- 加速点赞状态查询
- 优化点赞统计计算
- 提升用户互动体验

### 8. 通知表 (personal_blog_notification)

#### 单列索引
- `idx_notification_user_id`: 用户通知查询
- `idx_notification_type`: 通知类型筛选
- `idx_notification_is_read`: 未读通知查询
- `idx_notification_created_at`: 通知时间排序

#### 复合索引
- `idx_notification_user_read`: 用户未读通知
- `idx_notification_user_type`: 用户特定类型通知

#### 优化效果
- 提升通知中心加载速度
- 优化未读消息提醒
- 加速通知筛选功能

### 9. 公告表 (personal_blog_announcement)

#### 单列索引
- `idx_announcement_type`: 公告类型查询
- `idx_announcement_enabled`: 启用状态筛选
- `idx_announcement_pinned`: 置顶公告查询

#### 复合索引
- `idx_announcement_enabled_pinned`: 有效置顶公告
- `idx_announcement_time_range`: 时间范围内公告

#### 优化效果
- 提升公告展示性能
- 优化公告管理功能
- 加速时效性公告查询

### 10. 登录日志表 (login_logs)

#### 单列索引
- `idx_login_logs_user_id`: 用户登录历史
- `idx_login_logs_ip_address`: IP地址查询
- `idx_login_logs_login_time`: 登录时间排序
- `idx_login_logs_session_id`: 会话管理

#### 复合索引
- `idx_login_logs_user_time`: 用户登录时间线
- `idx_login_logs_user_current`: 当前活跃会话

#### 优化效果
- 提升安全日志查询性能
- 优化会话管理功能
- 加速异常登录检测

### 11. 安全设置表 (security_settings)

#### 单列索引
- `idx_security_settings_two_factor_enabled`: 双因子认证状态
- `idx_security_settings_account_locked_until`: 账户锁定查询
- `idx_security_settings_failed_login_attempts`: 失败登录统计

#### 优化效果
- 提升安全验证性能
- 优化账户安全检查
- 加速风险用户识别

## 性能监控建议

### 1. 查询性能监控
```sql
-- 启用慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- 分析查询执行计划
EXPLAIN SELECT * FROM article WHERE status = 'PUBLISHED' ORDER BY published_at DESC;
```

### 2. 索引使用情况监控
```sql
-- 查看索引使用统计
SHOW INDEX FROM article;

-- 分析表统计信息
ANALYZE TABLE article;
```

### 3. 定期维护
```sql
-- 优化表结构
OPTIMIZE TABLE article;

-- 重建索引统计
ANALYZE TABLE article;
```

## 注意事项

### 1. 索引维护成本
- 索引会增加写操作的开销
- 需要额外的存储空间
- 定期监控索引使用情况

### 2. 查询优化建议
- 避免在索引列上使用函数
- 合理使用复合索引的最左前缀原则
- 定期分析慢查询并优化

### 3. 数据量增长考虑
- 大数据量表考虑分区策略
- 历史数据归档机制
- 索引碎片整理

## 预期性能提升

- **首页加载速度**: 提升 60-80%
- **文章列表查询**: 提升 70-90%
- **评论加载**: 提升 50-70%
- **搜索功能**: 提升 80-95%
- **用户管理**: 提升 60-80%
- **统计查询**: 提升 70-90%

## 实施步骤

1. **备份数据库**: 执行索引创建前完整备份
2. **执行迁移脚本**: 运行 `V3__add_database_indexes.sql`
3. **性能测试**: 对比优化前后的查询性能
4. **监控调优**: 持续监控并根据实际使用情况调整
5. **文档更新**: 更新相关技术文档和运维手册

通过以上索引优化方案，可以显著提升博客系统的整体性能，改善用户体验，并为系统的扩展性奠定良好基础。