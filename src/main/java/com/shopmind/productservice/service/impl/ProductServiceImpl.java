package com.shopmind.productservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.framework.id.IdGenerator;
import com.shopmind.framework.service.StorageService;
import com.shopmind.productservice.client.BaiduGeocodingClient;
import com.shopmind.productservice.contants.StorageConstants;
import com.shopmind.productservice.dto.request.ProductRequestDto;
import com.shopmind.productservice.dto.response.ProductResponseDto;
import com.shopmind.productservice.dto.response.ProductSkuResponseDto;
import com.shopmind.productservice.entity.Product;
import com.shopmind.productservice.entity.ProductSpec;
import com.shopmind.productservice.enums.ProductStatus;
import com.shopmind.productservice.exception.ProductServiceException;
import com.shopmind.productservice.service.ImageService;
import com.shopmind.productservice.service.ProductService;
import com.shopmind.productservice.mapper.ProductMapper;
import com.shopmind.productservice.service.ProductSpecService;
import com.shopmind.productservice.service.SkuService;
import com.shopmind.productservice.utils.ImageUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
* @author hcy18
* @description 针对表【sm_products(商品表)】的数据库操作Service实现
* @createDate 2025-12-21 15:40:09
*/
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService{

    @Resource
    private IdGenerator idGenerator;

    @Resource
    private ImageService imageService;

    @Resource
    private BaiduGeocodingClient  baiduGeocodingClient;

    @Resource
    private ProductSpecService productSpecService;

    @Resource
    private SkuService skuService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = new Product();
        long id = idGenerator.nextId();
        product.setId(id);

        // 1，填充公共字段
        fillCommonProductFields(product, requestDto);

        // 2，插入商品
        this.save(product);
        log.info("成功插入了商品：{}", product);

        //3，插入规格和 SKU
        handleSpecsAndSkus(product.getId(), requestDto);

        // todo 请求 shopmind ai service 进行审核

        // todo 请求 shopmind ai service 生成商品的 tag（ProductsTag 类）

        // todo 如果 tag name 不存在于数据库，则创建；否则 复用

        // todo 让 shopmind ai service 进行商品向量化

        // todo 请求 shopmind ai service 获取 ai summary

        // todo 将 ai summary 插入数据库

        // todo 插入一条商品审核记录 Product Audit

        // todo 返回对象
//        ProductResponseDto productResponseDto = new ProductResponseDto();
//        productResponseDto.setId(product.getId());
//        productResponseDto.setCategory(product.getCategoryId());

        return null;
    }

    @Override
    public ProductResponseDto updateProduct(Long productId, ProductRequestDto requestDto) {
        // 1. 校验商品是否存在且可编辑
        Product existingProduct = this.getById(productId);
        if (existingProduct == null) {
            throw new ProductServiceException("PRODUCT0004");
        }
        if (!isEditableStatus(existingProduct.getStatus())) {
            throw new ProductServiceException("PRODUCT0005", existingProduct.getStatus().getDescription());
        }
        // 2. 重置为待审核状态（重新提交）
        existingProduct.setStatus(ProductStatus.PENDING_REVIEW);

        // 3. 填充更新后的字段（覆盖原值）
        fillCommonProductFields(existingProduct, requestDto);

        // 4. 更新商品
        this.updateById(existingProduct);
        log.info("成功更新商品：{}", existingProduct.getId());

        // todo 根据商品 id，删除旧的 spec 和 sku

        // 6. 重新插入规格和 SKU
        handleSpecsAndSkus(productId, requestDto);

        // todo 请求 shopmind ai service 进行审核（重新触发）

        // todo 请求 shopmind ai service 生成商品的 tag（ProductsTag 类）

        // todo 如果 tag name 不存在于数据库，则创建；否则 复用

        // todo 让 shopmind ai service 重新进行商品向量化

        // todo 请求 shopmind ai service 重新获取 ai summary

        // todo 更新或插入新的 ai summary

        // todo 插入一条【新的】商品审核记录（不要覆盖旧的！）

        // todo 构造并返回 ProductResponseDto

        return null;
    }

    @Override
    public List<ProductResponseDto> getProductsByMerchantId(Long merchantId) {
        return List.of();
    }

    @Override
    public void deleteProductById(Long productId) {

    }

    /**
     * 判断商品是否处于可编辑状态。 比如审核中的商品，不能编辑
     */
    private boolean isEditableStatus(ProductStatus status) {
        return status == ProductStatus.REJECTED ||
                status == ProductStatus.DRAFT ||
                status == ProductStatus.APPROVED; // 已上架商品也可下架后编辑
    }

    /**
     * 填充商品的公共字段（创建 & 更新共用）
     */
    private void fillCommonProductFields(Product product, ProductRequestDto dto) {
        product.setName(dto.getName());
        product.setCategoryId(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setPriceRange(dto.getPriceRange());
        product.setDescription(dto.getDescription());

        // 地理位置
        product.setProvinceCode(dto.getProvinceCode());
        product.setProvinceName(dto.getProvinceName());
        product.setCityCode(dto.getCityCode());
        product.setCityName(dto.getCityName());
        product.setDistrictCode(dto.getDistrictCode());
        product.setDistrictName(dto.getDistrictName());
        product.setDetailAddress(dto.getDetailAddress());
        product.setLocation(baiduGeocodingClient.createAddressLocation(
                product.getProvinceName(),
                product.getCityName(),
                product.getDistrictName(),
                product.getDetailAddress()
        ));

        // 图片处理
        processImages(product, dto);
    }

    /**
     * 处理商品图片（封面 + 详情图）
     */
    private void processImages(Product product, ProductRequestDto dto) {
        String coverImgUrl = imageService.getImageUrlFromBase64(dto.getCoverImage());
        product.setCoverImage(coverImgUrl);

        List<String> detailImages = dto.getDetailImages();
        if (CollectionUtil.isNotEmpty(detailImages)) {
            List<String> detailImgUrls = detailImages.stream()
                    .map(base64 -> imageService.getImageUrlFromBase64(base64))
                    .toList();
            product.setDetailImages(detailImgUrls);
        } else {
            product.setDetailImages(Collections.emptyList());
        }
    }

    /**
     * 处理规格和 SKU 的创建（创建 & 更新共用）
     */
    private void handleSpecsAndSkus(Long productId, ProductRequestDto dto) {
        List<ProductSpec> productSpecs = productSpecService.addBatchProductSpecs(productId, dto.getSkuSpecs());
        log.info("成功创建 {} 个商品规格参数，商品 id：{}", productSpecs.size(), productId);

        List<ProductSkuResponseDto> skuResponses = skuService.addBatchSku(productId, dto.getSkuItems());
        log.info("成功创建 {} 条 SKU，商品 id：{}", skuResponses.size(), productId);
    }
}




