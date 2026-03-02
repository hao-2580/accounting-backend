package com.accounting.controller;

import com.accounting.dto.Result;
import com.accounting.entity.Category;
import com.accounting.mapper.CategoryMapper;
import com.accounting.util.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "收支分类")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryMapper categoryMapper;

    @Operation(summary = "获取分类列表(系统+个人)")
    @GetMapping
    public Result<List<Category>> list(@RequestParam(required = false) String type) {
        Long userId = SecurityUtil.getCurrentUserId();
        var wrapper = new LambdaQueryWrapper<Category>()
                .and(w -> w.eq(Category::getUserId, 0).or().eq(Category::getUserId, userId))
                .eq(type != null, Category::getType, type)
                .orderByAsc(Category::getUserId)
                .orderByAsc(Category::getSortOrder);
        return Result.ok(categoryMapper.selectList(wrapper));
    }

    @Operation(summary = "新增自定义分类")
    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        category.setUserId(SecurityUtil.getCurrentUserId());
        categoryMapper.insert(category);
        return Result.ok(category);
    }

    @Operation(summary = "删除自定义分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Category cat = categoryMapper.selectById(id);
        if (cat != null && cat.getUserId().equals(userId)) categoryMapper.deleteById(id);
        return Result.ok();
    }
}
