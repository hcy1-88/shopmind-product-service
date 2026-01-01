package com.shopmind.productservice.service;

import com.shopmind.framework.context.PageResult;
import com.shopmind.productservice.dto.request.MerchantProductQueryParams;
import com.shopmind.productservice.dto.request.ProductRequestDto;
import com.shopmind.productservice.dto.response.ProductResponseDto;
import com.shopmind.productservice.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_products(商品表)】的数据库操作Service
* @createDate 2025-12-21 15:40:09
*/
public interface ProductService extends IService<Product> {
    /**
     * 新增商品
     * @param productRequestDto 商品表单
     */
    ProductResponseDto createProduct(ProductRequestDto productRequestDto);

    /**
     * 更新商品
     * @param productId 商品 id
     * @param productRequestDto 商品表单
     */
    ProductResponseDto  updateProduct(Long productId, ProductRequestDto productRequestDto);

    /**
     * 商家获取商品
     * @param merchantId 商品 id
     * @return 商品列表
     */
    PageResult<List<ProductResponseDto>> getProductsByMerchantId(Long merchantId, MerchantProductQueryParams params);

    /**
     * 删除商品
     * @param productId 商品 id
     */
    void deleteProductById(Long productId);

    /**
     * 获取热门商品（首页）
     * @param limit 数量限制
     * @return 商品列表
     */
    List<ProductResponseDto> getHotProducts(Integer limit);

    /**
     * 批量获取商品
     */
    List<ProductResponseDto> getProductsBatch(List<Long> ids);

    /**
     * 根据商品ID获取商品详情(C端)
     * @param productId 商品ID
     * @return 商品详情
     */
    ProductResponseDto getProductDetailById(Long productId);

}
