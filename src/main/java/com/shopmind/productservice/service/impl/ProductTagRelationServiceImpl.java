package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.framework.id.IdGenerator;
import com.shopmind.productservice.client.dto.response.GenerateTagsResponseDto;
import com.shopmind.productservice.dto.business.ProductTagDTO;
import com.shopmind.productservice.entity.ProductTagRelation;
import com.shopmind.productservice.entity.ProductsTag;
import com.shopmind.productservice.service.ProductTagRelationService;
import com.shopmind.productservice.mapper.ProductTagRelationMapper;
import com.shopmind.productservice.service.ProductsTagService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author hcy18
* @description 针对表【sm_product_tag_relations(商品标签关联表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class ProductTagRelationServiceImpl extends ServiceImpl<ProductTagRelationMapper, ProductTagRelation> implements ProductTagRelationService {

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private ProductsTagService productsTagService;

    @Resource
    private ProductTagRelationMapper productTagRelationMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createRelations(Long productId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        List<ProductTagRelation> relations = tagIds.stream()
                .map(tagId -> {
                    ProductTagRelation relation = new ProductTagRelation();
                    relation.setId(idGenerator.nextId());
                    relation.setProductId(productId);
                    relation.setTagId(tagId);
                    return relation;
                })
                .toList();

        this.saveBatch(relations);
    }

    @Override
    public void deleteByProductId(Long productId) {
        LambdaQueryWrapper<ProductTagRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductTagRelation::getProductId, productId);
        this.remove(queryWrapper);
    }

    @Override
    public List<ProductsTag> findTagsByProductId(Long productId) {
        LambdaQueryWrapper<ProductTagRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductTagRelation::getProductId, productId);
        List<ProductTagRelation> relations = this.list(queryWrapper);
        return relations.stream().map(relation -> productsTagService.getById(relation.getTagId())).toList();
    }

    @Override
    public Map<Long, List<GenerateTagsResponseDto.TagInfo>> findTagsByProductIds(List<Long> productIds) {
        List<ProductTagDTO> productTagDTOS = productTagRelationMapper.selectTagsByProductIds(productIds);
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1. 一次查询获取所有 (productId, tagName, tagColor)
        List<ProductTagDTO> dtos = productTagRelationMapper.selectTagsByProductIds(productIds);

        // 2. 按 productId 分组
        return dtos.stream()
                .collect(Collectors.groupingBy(
                        ProductTagDTO::getProductId,
                        Collectors.mapping(
                                dto -> new GenerateTagsResponseDto.TagInfo(dto.getTagName(), dto.getTagColor()),
                                Collectors.toList()
                        )
                ));
    }
}




