package com.shopmind.productservice.controller;

import com.shopmind.framework.annotation.RequireAuth;
import com.shopmind.framework.context.ResultContext;
import com.shopmind.framework.context.UserContext;
import com.shopmind.productservice.dto.request.ProductRequestDto;
import com.shopmind.productservice.dto.response.ProductResponseDto;
import com.shopmind.productservice.service.ProductService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 商家
 * Author: huangcy
 * Date: 2025-12-22
 */
@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @Resource
    private ProductService productService;

    @RequireAuth
    @GetMapping("/products")
    public ResultContext<List<ProductResponseDto>> getProducts() {
        Long merchantId = UserContext.userId();
        return null;
    }

    @RequireAuth
    @PostMapping("/products")
    public ResultContext<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto){
        ProductResponseDto product = productService.createProduct(productRequestDto);
        return ResultContext.success(product);
    }

    @RequireAuth
    @PutMapping("/products/{id}")
    public ResultContext<ProductResponseDto> updateProduct(@Valid @RequestBody ProductRequestDto productRequestDto, @PathVariable("id") Long id){
        ProductResponseDto product = productService.updateProduct(id, productRequestDto);
        return ResultContext.success(product);
    }

    @RequireAuth
    @DeleteMapping("/products/{id}")
    public ResultContext<Void> deleteProduct(@PathVariable("id") Long id){
        return ResultContext.success();
    }
}
