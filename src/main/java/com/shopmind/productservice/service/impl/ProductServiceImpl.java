package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.productservice.entity.Product;
import com.shopmind.productservice.service.ProductService;
import com.shopmind.productservice.mapper.ProductMapper;
import org.springframework.stereotype.Service;

/**
* @author hcy18
* @description 针对表【sm_products(商品表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

}




