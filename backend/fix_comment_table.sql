-- 修复comment表的article_id字段，允许null值以支持留言板功能
-- 这个脚本将修改comment表，使article_id字段可以为null

USE personal_blog;

-- 修改comment表的article_id字段，允许null值
ALTER TABLE comment MODIFY COLUMN article_id BIGINT NULL;

-- 验证修改结果
DESCRIBE comment;

-- 显示修改后的表结构
SHOW CREATE TABLE comment;