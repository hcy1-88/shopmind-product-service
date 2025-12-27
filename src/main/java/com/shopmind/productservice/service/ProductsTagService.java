package com.shopmind.productservice.service;

import com.shopmind.productservice.entity.ProductsTag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shopmind.productservice.enums.TagType;

/**
* @author hcy18
* @description 针对表【sm_products_tags(商品标签表)】的数据库操作Service
* @createDate 2025-12-21 15:40:09
*/
public interface ProductsTagService extends IService<ProductsTag> {

    /**
     * 根据标签名称查找或创建标签
     * @param tagName 标签名称
     * @param type 标签类型
     * @param color 标签颜色
     * @return 标签实体
     */
    ProductsTag findOrCreateTag(String tagName, TagType type, String color);
}
