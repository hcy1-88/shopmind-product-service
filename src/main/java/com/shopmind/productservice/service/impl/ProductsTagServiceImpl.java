package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.productservice.entity.ProductsTag;
import com.shopmind.productservice.service.ProductsTagService;
import com.shopmind.productservice.mapper.ProductsTagMapper;
import org.springframework.stereotype.Service;

/**
* @author hcy18
* @description 针对表【sm_products_tags(商品标签表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class ProductsTagServiceImpl extends ServiceImpl<ProductsTagMapper, ProductsTag>
    implements ProductsTagService{

}




