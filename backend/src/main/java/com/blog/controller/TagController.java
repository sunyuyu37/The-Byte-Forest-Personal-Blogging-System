package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.TagDTO;
import com.blog.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取标签列表（分页）
     */
    @GetMapping
    public Result<Page<TagDTO>> getTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<TagDTO> tags;
            if (keyword != null && !keyword.trim().isEmpty()) {
                tags = tagService.searchTags(keyword.trim(), pageable);
            } else {
                tags = tagService.getTags(pageable);
            }
            
            return Result.success(tags);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有标签（不分页）
     */
    @GetMapping("/all")
    public Result<List<TagDTO>> getAllTags() {
        try {
            List<TagDTO> tags = tagService.getAllTags();
            return Result.success(tags);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取标签
     */
    @GetMapping("/{id}")
    public Result<TagDTO> getTagById(@PathVariable Long id) {
        try {
            TagDTO tag = tagService.getTagById(id);
            return Result.success(tag);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建标签
     */
    @PostMapping
    public Result<TagDTO> createTag(@Valid @RequestBody TagDTO tagDTO) {
        try {
            TagDTO createdTag = tagService.createTag(tagDTO);
            return Result.success("标签创建成功", createdTag);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public Result<TagDTO> updateTag(
            @PathVariable Long id, 
            @Valid @RequestBody TagDTO tagDTO) {
        try {
            TagDTO updatedTag = tagService.updateTag(id, tagDTO);
            return Result.success("标签更新成功", updatedTag);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        try {
            tagService.deleteTag(id);
            return Result.success("标签删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除标签
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteTags(@Valid @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.badRequest("请选择要删除的标签");
            }
            
            tagService.deleteTags(ids);
            return Result.success("标签批量删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/popular")
    public Result<List<TagDTO>> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<TagDTO> tags = tagService.getPopularTags(limit);
            return Result.success(tags);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取标签统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getTagStats() {
        try {
            long totalTags = tagService.countTags();
            Map<String, Object> stats = Map.of("totalTags", totalTags);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重新计算所有标签的文章数量
     */
    @PostMapping("/recalculate-counts")
    public Result<Void> recalculateTagArticleCounts() {
        try {
            tagService.recalculateAllTagArticleCounts();
            return Result.success("标签文章数量重新计算完成", null);
        } catch (Exception e) {
            return Result.error("重新计算失败: " + e.getMessage());
        }
    }
}