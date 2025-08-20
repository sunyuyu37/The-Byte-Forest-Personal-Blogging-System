package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.TagDTO;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class PublicController {

    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签（公开接口）
     */
    @GetMapping
    public Result<List<TagDTO>> getAllTags() {
        List<TagDTO> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 获取热门标签（公开接口）
     */
    @GetMapping("/popular")
    public Result<List<TagDTO>> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {
        List<TagDTO> tags = tagService.getPopularTags(limit);
        return Result.success(tags);
    }
}