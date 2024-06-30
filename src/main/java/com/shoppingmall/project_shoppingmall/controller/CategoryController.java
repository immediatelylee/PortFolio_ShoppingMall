package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.constant.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @GetMapping
    public List<ItemCategory> getAllCategories() {
        return Arrays.asList(ItemCategory.values());
    }

    @GetMapping("/children")
    public List<String> getChildCategories(@RequestParam String parentTitle) {
        ItemCategory parentCategory = ItemCategory.titleOf(parentTitle);
        return parentCategory != null ? parentCategory.getChildCategories().stream()
                .map(ItemCategory::getTitle)
                .collect(Collectors.toList()) : List.of();
    }
    @GetMapping("/children2")
    public List<ItemCategory> getChildCategories2(@RequestParam String parentTitle) {
        ItemCategory parentCategory = ItemCategory.titleOf(parentTitle);
        return parentCategory != null ? parentCategory.getChildCategories() : List.of();
    }
}
