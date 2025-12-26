package com.shopmind.productservice.controller;

import com.google.common.base.Preconditions;
import com.shopmind.framework.context.ResultContext;
import com.shopmind.productservice.dto.response.CategoryResponseDto;
import com.shopmind.productservice.service.ProductsCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 商品分类
 * Author: huangcy
 * Date: 2025-12-26
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private ProductsCategoryService categoryService;

    @GetMapping("/{level}")
    public ResultContext<List<CategoryResponseDto>> getCategories(@PathVariable("level") Integer level){
        Preconditions.checkArgument(level != null && level != 0, "分类级别不能为空！且不能为 0！");
        return ResultContext.success(categoryService.getProductCategories(level));
    }
}
