package com.shopmind.productservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.framework.id.IdGenerator;
import com.shopmind.productservice.dto.business.SkuSpecValue;
import com.shopmind.productservice.dto.request.SkuSpecDto;
import com.shopmind.productservice.entity.ProductSpec;
import com.shopmind.productservice.service.ImageService;
import com.shopmind.productservice.service.ProductSpecService;
import com.shopmind.productservice.mapper.ProductSpecMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_product_specs(SKU规格定义表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Slf4j
@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements ProductSpecService{

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private ImageService imageService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ProductSpec> addBatchProductSpecs(Long productId, List<SkuSpecDto> specs) {
        List<ProductSpec> productSpecs = new ArrayList<>();
        for (SkuSpecDto spec:specs){
            ProductSpec productSpec = new ProductSpec();
            productSpec.setId(idGenerator.nextId());
            productSpec.setProductId(productId);
            productSpec.setSpecName(spec.getName());
            // 图片转 url
            if (CollectionUtil.isNotEmpty(spec.getValues())){
                for (SkuSpecValue specValue : spec.getValues()) {
                    String url = imageService.getImageUrlFromBase64(specValue.getImage());
                    specValue.setImage(url);
                }
            }
            productSpec.setSpecValues(spec.getValues());
            productSpecs.add(productSpec);
        }
        this.baseMapper.insert(productSpecs);
        return productSpecs;
    }
}




