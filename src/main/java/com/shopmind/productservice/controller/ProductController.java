package com.shopmind.productservice.controller;

import com.shopmind.framework.context.PageResult;
import com.shopmind.framework.context.ResultContext;
import com.shopmind.productservice.client.dto.request.ProductSoldRequestDTO;
import com.shopmind.productservice.dto.request.ProductGettingRequestDTO;
import com.shopmind.productservice.dto.request.ProductRequestDto;
import com.shopmind.productservice.dto.response.ProductResponseDto;
import com.shopmind.productservice.service.ProductService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品 Controller - 面向 C 端用户
 * 
 * @author huangcy
 * @date 2025-12-26
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * 获取热门商品（首页）
     * 
     * @param limit 数量限制，默认 20
     * @return 简化的商品列表
     */
    @GetMapping("/hot")
    public ResultContext<List<ProductResponseDto>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        
        // 限制最大查询数量
        if (limit > 100) {
            limit = 100;
        }
        
        List<ProductResponseDto> hotProducts = productService.getHotProducts(limit);
        return ResultContext.success(hotProducts);
    }

    @PostMapping("/ids")
    public ResultContext<List<ProductResponseDto>> getProductsBatch(@RequestBody ProductGettingRequestDTO requestDTO){
        List<ProductResponseDto> productsBatch = productService.getProductsBatch(requestDTO.getIds());
        return ResultContext.success(productsBatch);
    }

    @GetMapping("/detail/{productId}")
    public ResultContext<ProductResponseDto> getProductById(@PathVariable("productId") Long productId){
        ProductResponseDto productDetail = productService.getProductDetailById(productId);
        return ResultContext.success(productDetail);
    }

    @PostMapping("/sale")
    public ResultContext<Void> productBeSold(@Valid @RequestBody List<ProductSoldRequestDTO> soldRequestDTOs){
        productService.handleProductWhenSold(soldRequestDTOs);
        return ResultContext.success();
    }

    @GetMapping("/search")
    public ResultContext<PageResult<List<ProductResponseDto>>> searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam("pageNumber") Integer pageNumber,
            @RequestParam("pageSize") Integer pageSize){
        PageResult<List<ProductResponseDto>> listPageResult = productService.searchProducts(keyword, pageNumber, pageSize);
        return ResultContext.success(listPageResult);
    }
}
