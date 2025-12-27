package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.framework.id.IdGenerator;
import com.shopmind.productservice.entity.ProductsTag;
import com.shopmind.productservice.enums.TagType;
import com.shopmind.productservice.service.ProductsTagService;
import com.shopmind.productservice.mapper.ProductsTagMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
* @author hcy18
* @description 针对表【sm_products_tags(商品标签表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class ProductsTagServiceImpl extends ServiceImpl<ProductsTagMapper, ProductsTag> implements ProductsTagService{

    @Resource
    private IdGenerator idGenerator;

    @Override
    public ProductsTag findOrCreateTag(String tagName, TagType type, String color) {
        // 先查询是否已存在
        LambdaQueryWrapper<ProductsTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductsTag::getName, tagName);
        ProductsTag existingTag = this.getOne(queryWrapper);

        if (existingTag != null) {
            return existingTag;
        }

        // 不存在则创建新标签
        ProductsTag newTag = new ProductsTag();
        newTag.setId(idGenerator.nextId());
        newTag.setName(tagName);
        newTag.setType(type);
        newTag.setColor(color);
        this.save(newTag);
        return newTag;
    }
}




