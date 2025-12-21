package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.productservice.entity.ProductCategory;
import com.shopmind.productservice.service.ProductsCategorieService;
import com.shopmind.productservice.mapper.ProductsCategorieMapper;
import org.springframework.stereotype.Service;

/**
* @author hcy18
* @description 针对表【sm_products_categories(商品分类表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class ProductsCategorieServiceImpl extends ServiceImpl<ProductsCategorieMapper, ProductCategory>
    implements ProductsCategorieService{

}




