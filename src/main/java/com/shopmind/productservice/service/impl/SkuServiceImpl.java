package com.shopmind.productservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.framework.id.IdGenerator;
import com.shopmind.productservice.dto.request.SkuItemDto;
import com.shopmind.productservice.dto.response.ProductSkuResponseDto;
import com.shopmind.productservice.entity.Sku;
import com.shopmind.productservice.service.ImageService;
import com.shopmind.productservice.service.SkuService;
import com.shopmind.productservice.mapper.SkuMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_skus(SKU表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService{

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private ImageService imageService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ProductSkuResponseDto> addBatchSku(Long productId, List<SkuItemDto> skuItemDtos) {
        List<ProductSkuResponseDto> res = new ArrayList<>();
        List<Sku> skus = new ArrayList<>();
        for (SkuItemDto skuItemDto : skuItemDtos) {
            Sku sku = new Sku();
            sku.setId(idGenerator.nextId());
            sku.setProductId(productId);
            sku.setSkuSpecs(skuItemDto.getSpecs());
            sku.setPrice(skuItemDto.getPrice());
            sku.setStock(skuItemDto.getStock());
            sku.setImage(imageService.getImageUrlFromBase64(skuItemDto.getImage()));
            skus.add(sku);
        }
        this.saveBatch(skus);
        return skus.stream().map(sku -> {
            ProductSkuResponseDto resp = new ProductSkuResponseDto();
            BeanUtils.copyProperties(sku, resp);
            return resp;
        }).toList();
    }
}




