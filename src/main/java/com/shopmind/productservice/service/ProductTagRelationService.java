package com.shopmind.productservice.service;

import com.shopmind.productservice.entity.ProductTagRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_product_tag_relations(商品标签关联表)】的数据库操作Service
* @createDate 2025-12-21 15:40:09
*/
public interface ProductTagRelationService extends IService<ProductTagRelation> {

    /**
     * 批量创建商品与标签的关联关系
     * @param productId 商品 ID
     * @param tagIds 标签 ID 列表
     */
    void createRelations(Long productId, List<Long> tagIds);

    /**
     * 删除商品的所有标签关联
     * @param productId 商品 ID
     */
    void deleteByProductId(Long productId);
}
