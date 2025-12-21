package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.productservice.entity.Sku;
import com.shopmind.productservice.service.SkuService;
import com.shopmind.productservice.mapper.SkuMapper;
import org.springframework.stereotype.Service;

/**
* @author hcy18
* @description 针对表【sm_skus(SKU表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku>
    implements SkuService{

}




