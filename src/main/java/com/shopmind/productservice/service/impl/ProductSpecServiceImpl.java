package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.productservice.entity.ProductSpec;
import com.shopmind.productservice.service.ProductSpecService;
import com.shopmind.productservice.mapper.ProductSpecMapper;
import org.springframework.stereotype.Service;

/**
* @author hcy18
* @description 针对表【sm_product_specs(SKU规格定义表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec>
    implements ProductSpecService{

}




