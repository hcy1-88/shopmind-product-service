package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.productservice.dto.response.CategoryResponseDto;
import com.shopmind.productservice.entity.ProductCategory;
import com.shopmind.productservice.service.ProductsCategoryService;
import com.shopmind.productservice.mapper.ProductsCategorieMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_products_categories(商品分类表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class ProductsCategoryServiceImpl extends ServiceImpl<ProductsCategorieMapper, ProductCategory>  implements ProductsCategoryService {
    @Override
    public List<CategoryResponseDto> getProductCategories(Integer level) {
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProductCategory::getLevel,level).orderByAsc(ProductCategory::getSortOrder);
        List<ProductCategory> productCategories = this.baseMapper.selectList(queryWrapper);
        return productCategories.stream().map(po -> {
            CategoryResponseDto dto = new CategoryResponseDto();
            BeanUtils.copyProperties(po, dto);
            return dto;
        }).toList();
    }
}




