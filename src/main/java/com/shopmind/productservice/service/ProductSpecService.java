package com.shopmind.productservice.service;

import com.shopmind.productservice.dto.request.SkuSpecDto;
import com.shopmind.productservice.entity.ProductSpec;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_product_specs(SKU规格定义表)】的数据库操作Service
* @createDate 2025-12-21 15:40:09
*/
public interface ProductSpecService extends IService<ProductSpec> {
    /**
     * 批量创建规格参数
     * @param productId 商品 id（此方法，spec 的商品 id 以此为准）
     * @param specs 多个规格参数
     * @return list ProductSpec
     */
    List<ProductSpec> addBatchProductSpecs(Long productId, List<SkuSpecDto> specs);
}
