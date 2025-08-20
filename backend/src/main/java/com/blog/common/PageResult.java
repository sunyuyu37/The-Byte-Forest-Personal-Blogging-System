package com.blog.common;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResult<T> {
    
    private List<T> content;
    private Integer page;
    private Integer size;
    private Long total;
    private Integer totalPages;
    private Boolean first;
    private Boolean last;
    private Boolean hasNext;
    private Boolean hasPrevious;
    
    public PageResult() {}
    
    public PageResult(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.total = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }
    
    public PageResult(List<T> content, Integer page, Integer size, Long total) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
        this.hasNext = page < totalPages - 1;
        this.hasPrevious = page > 0;
    }
    
    // Getters and Setters
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    
    public Boolean getFirst() { return first; }
    public void setFirst(Boolean first) { this.first = first; }
    
    public Boolean getLast() { return last; }
    public void setLast(Boolean last) { this.last = last; }
    
    public Boolean getHasNext() { return hasNext; }
    public void setHasNext(Boolean hasNext) { this.hasNext = hasNext; }
    
    public Boolean getHasPrevious() { return hasPrevious; }
    public void setHasPrevious(Boolean hasPrevious) { this.hasPrevious = hasPrevious; }
}