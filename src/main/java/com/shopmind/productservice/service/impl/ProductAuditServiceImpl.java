package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.productservice.entity.ProductAudit;
import com.shopmind.productservice.service.ProductAuditService;
import com.shopmind.productservice.mapper.ProductAuditMapper;
import org.springframework.stereotype.Service;

/**
* @author hcy18
* @description 针对表【sm_product_audits(商品审核记录表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class ProductAuditServiceImpl extends ServiceImpl<ProductAuditMapper, ProductAudit>
    implements ProductAuditService{

}




