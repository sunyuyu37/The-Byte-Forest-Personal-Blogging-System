package com.blog.exception;

/**
 * 文章相关业务异常
 */
public class ArticleException extends BusinessException {
    
    public ArticleException(String message) {
        super(message);
    }
    
    public ArticleException(Integer code, String message) {
        super(code, message);
    }
    
    // 文章不存在
    public static class ArticleNotFoundException extends ArticleException {
        public ArticleNotFoundException() {
            super(404, "文章不存在");
        }
        
        public ArticleNotFoundException(String message) {
            super(404, message);
        }
    }
    
    // 文章已发布，无法修改
    public static class ArticleAlreadyPublishedException extends ArticleException {
        public ArticleAlreadyPublishedException() {
            super(409, "文章已发布，无法修改");
        }
        
        public ArticleAlreadyPublishedException(String message) {
            super(409, message);
        }
    }
    
    // 文章标题重复
    public static class DuplicateArticleTitleException extends ArticleException {
        public DuplicateArticleTitleException() {
            super(409, "文章标题已存在");
        }
        
        public DuplicateArticleTitleException(String message) {
            super(409, message);
        }
    }
    
    // 文章内容为空
    public static class EmptyArticleContentException extends ArticleException {
        public EmptyArticleContentException() {
            super(400, "文章内容不能为空");
        }
        
        public EmptyArticleContentException(String message) {
            super(400, message);
        }
    }
}