package com.shopmind.productservice.service;

import com.shopmind.productservice.dto.request.SkuItemDto;
import com.shopmind.productservice.dto.response.ProductSkuResponseDto;
import com.shopmind.productservice.entity.Sku;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_skus(SKU表)】的数据库操作Service
* @createDate 2025-12-21 15:40:09
*/
public interface SkuService extends IService<Sku> {

    /**
     * 批量添加 sku
     * @param productId 商品 id，以此为准
     * @param skuItemDtos 每一条 sku 组合
     * @return list ProductSkuResponseDto
     */
    List<ProductSkuResponseDto> addBatchSku(Long productId, List<SkuItemDto> skuItemDtos);
}
