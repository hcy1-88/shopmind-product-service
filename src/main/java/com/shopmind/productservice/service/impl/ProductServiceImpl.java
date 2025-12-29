package com.shopmind.productservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.framework.context.UserContext;
import com.shopmind.framework.id.IdGenerator;
import com.shopmind.productservice.client.AIServiceClient;
import com.shopmind.productservice.client.BaiduGeocodingClient;
import com.shopmind.productservice.dto.request.*;
import com.shopmind.productservice.dto.response.*;
import com.shopmind.productservice.entity.*;
import com.shopmind.productservice.enums.AuditType;
import com.shopmind.productservice.enums.ProductStatus;
import com.shopmind.productservice.enums.TagType;
import com.shopmind.productservice.exception.ProductServiceException;
import com.shopmind.productservice.service.*;
import com.shopmind.productservice.mapper.ProductMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private AIServiceClient aiServiceClient;

    @Resource
    private ProductAuditService productAuditService;

    @Resource
    private ProductsTagService productsTagService;

    @Resource
    private ProductTagRelationService productTagRelationService;

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

        // 4. 请求 shopmind ai service 进行审核
        ProductAuditResponseDto auditResponse = performProductAudit(product);
        
        // 5. 更新商品状态
        if (StrUtil.equals(ProductStatus.APPROVED.getValue(),auditResponse.getAuditStatus())) {
            product.setStatus(ProductStatus.APPROVED);
        } else {
            product.setStatus(ProductStatus.REJECTED);
        }

        // 6. 请求 shopmind ai service 生成商品的 tag
        List<Long> tagIds = generateAndSaveTags(product);

        // 7. 创建商品标签关联
        productTagRelationService.createRelations(product.getId(), tagIds);

        // 后续请求 ai 相关的操作
        aiCommonOperation(product, tagIds);

        // 11. 插入一条商品审核记录
        ProductAudit audit = saveAuditRecord(product, auditResponse);
        auditResponse.setAuditTime(audit.getCreatedAt());
        // 12. 构造并返回响应对象
        return buildProductResponse(product, auditResponse);
    }

    private void aiCommonOperation(Product product, List<Long> tagIds) {
        // 8. 请求 shopmind ai service 获取 ai summary
        GenerateSummaryResponseDto summaryResponse = generateAiSummary(product);

        // 9. 将 ai summary 更新到商品
        product.setAiSummary(summaryResponse.getSummary());
        this.updateById(product);

        // 10. 让 shopmind ai service 进行商品向量化
        vectorizeProduct(product, tagIds);
    }

    @Transactional(rollbackFor = Exception.class)
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

        // 5. 删除旧的 spec 和 sku
        productSpecService.deleteByProductId(productId);
        skuService.deleteByProductId(productId);

        // 6. 重新插入规格和 SKU
        handleSpecsAndSkus(productId, requestDto);

        // 7. 删除旧的标签关联
        productTagRelationService.deleteByProductId(productId);

        // 8. 请求 shopmind ai service 进行审核（重新触发）
        ProductAuditResponseDto auditResponse = performProductAudit(existingProduct);
        
        // 9. 更新商品状态
        if (StrUtil.equals(ProductStatus.APPROVED.getValue(),auditResponse.getAuditStatus())) {
            existingProduct.setStatus(ProductStatus.APPROVED);
        } else {
            existingProduct.setStatus(ProductStatus.REJECTED);
        }

        // 10. 请求 shopmind ai service 重新生成商品的 tag
        List<Long> tagIds = generateAndSaveTags(existingProduct);

        // 11. 重新创建商品标签关联
        productTagRelationService.createRelations(productId, tagIds);

        // 12. ai 相关的操作
        aiCommonOperation(existingProduct, tagIds);

        // 15. 插入一条【新的】商品审核记录（不要覆盖旧的！）
        ProductAudit audit = saveAuditRecord(existingProduct, auditResponse);
        auditResponse.setAuditTime(audit.getCreatedAt());

        // 16. 构造并返回 ProductResponseDto
        return buildProductResponse(existingProduct, auditResponse);
    }

    @Override
    public List<ProductResponseDto> getProductsByMerchantId(Long merchantId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getMerchantId, merchantId)
                .isNull(Product::getDeletedAt)
                .orderByDesc(Product::getCreatedAt);
        
        List<Product> products = this.list(queryWrapper);

        // 查最近的一条审核记录
        List<Long> pids = products.stream().map(Product::getId).toList();
        Map<Long, ProductAuditResponseDto> audits = productAuditService.getLatestAuditBatchByProductIds(pids);

        // 构建响应列表
        return products.stream()
                .map(product -> {
                    // 从 Map 中获取对应的审核结果
                    ProductAuditResponseDto auditResponse = audits.get(product.getId());
                    return buildProductResponse(product, auditResponse);
                })
                .collect(Collectors.toList());

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteProductById(Long productId) {
        // 1. 检查商品是否存在
        Product product = this.getById(productId);
        if (product == null) {
            log.error("商品不存在，product id：{}", productId);
            throw new ProductServiceException("PRODUCT0004");
        }

        // 2. 软删除商品（设置删除时间）
        product.setDeletedAt(new Date());
        this.updateById(product);
        log.info("成功删除商品：{}", productId);

        // 3. 删除相关的规格和 SKU
        productSpecService.deleteByProductId(productId);
        skuService.deleteByProductId(productId);

        // 4. 删除商品标签关联
        productTagRelationService.deleteByProductId(productId);
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
        product.setMerchantId(UserContext.userId());
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

    /**
     * 执行商品审核
     */
    private ProductAuditResponseDto performProductAudit(Product product) {
        ProductAuditRequestDto auditRequest = ProductAuditRequestDto.builder()
                .productId(product.getId())
                .title(product.getName())
                .description(product.getDescription())
                .coverImage(product.getCoverImage())
                .detailImages(product.getDetailImages())
                .categoryId(product.getCategoryId())
                .build();

        ProductAuditResponseDto auditResponse = aiServiceClient.auditProduct(auditRequest).getData();
        log.info("商品 {} 审核结果：{}", product.getId(), auditResponse.getAuditStatus());
        return auditResponse;
    }

    /**
     * 生成并保存商品标签
     */
    private List<Long> generateAndSaveTags(Product product) {
        GenerateTagsRequestDto tagsRequest = GenerateTagsRequestDto.builder()
                .productId(product.getId())
                .title(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .imageUrls(Collections.singletonList(product.getCoverImage()))
                .build();

        GenerateTagsResponseDto tagsResponse = aiServiceClient.generateProductTags(tagsRequest).getData();
        log.info("商品 {} 生成了 {} 个标签", product.getId(), tagsResponse.getTags().size());

        // 保存标签（如果不存在则创建）
        List<Long> tagIds = new ArrayList<>();
        for (GenerateTagsResponseDto.TagInfo tagInfo : tagsResponse.getTags()) {
            ProductsTag tag = productsTagService.findOrCreateTag(
                    tagInfo.getName(),
                    TagType.AI_GENERATED,
                    tagInfo.getColor()
            );
            tagIds.add(tag.getId());
        }
        return tagIds;
    }

    /**
     * 生成 AI 摘要
     */
    private GenerateSummaryResponseDto generateAiSummary(Product product) {
        GenerateSummaryRequestDto summaryRequest = GenerateSummaryRequestDto.builder()
                .productId(product.getId())
                .title(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .build();

        GenerateSummaryResponseDto summaryResponse = aiServiceClient.generateProductSummary(summaryRequest).getData();
        log.info("商品 {} 生成了 AI 摘要", product.getId());
        return summaryResponse;
    }

    /**
     * 向量化商品
     */
    private void vectorizeProduct(Product product, List<Long> tagIds) {
        // 获取标签名称
        List<String> tagNames = tagIds.stream()
                .map(tagId -> productsTagService.getById(tagId))
                .filter(Objects::nonNull)
                .map(ProductsTag::getName)
                .collect(Collectors.toList());

        VectorizeProductRequestDto vectorizeRequest = VectorizeProductRequestDto.builder()
                .productId(product.getId())
                .title(product.getName())
                .description(product.getDescription())
                .aiSummary(product.getAiSummary())
                .tags(tagNames)
                .categoryId(product.getCategoryId())
                .build();

        VectorizeProductResponseDto vectorizeResponse = aiServiceClient.vectorizeProduct(vectorizeRequest).getData();
        if (vectorizeResponse.getSuccess()) {
            log.info("商品 {} 向量化成功，向量 ID：{}", product.getId(), vectorizeResponse.getVectorId());
        } else {
            log.error("商品 {} 向量化失败：{}", product.getId(), vectorizeResponse.getErrorMessage());
        }
    }

    /**
     * 保存审核记录
     */
    private ProductAudit saveAuditRecord(Product product, ProductAuditResponseDto auditResponse) {
        ProductAudit audit = new ProductAudit();
        audit.setId(idGenerator.nextId());
        audit.setProductId(product.getId());
        audit.setMerchantId(product.getMerchantId());
        audit.setAuditType(AuditType.AI);
        audit.setAuditStatus(auditResponse.getAuditStatus());
        audit.setTitleCheckResult(auditResponse.getTitleCheckResult());
        audit.setImageCheckResults(auditResponse.getImageCheckResults());
        audit.setRejectReason(auditResponse.getRejectReason());
        audit.setSuggestions(auditResponse.getSuggestions());
        
        productAuditService.save(audit);
        log.info("保存了商品 {} 的审核记录", product.getId());
        return audit;
    }

    /**
     * 构建商品响应 DTO
     */
    private ProductResponseDto buildProductResponse(Product product, ProductAuditResponseDto  auditResponse) {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setPriceRange(product.getPriceRange());
        response.setImage(product.getCoverImage());
        response.setImages(product.getDetailImages());
        response.setAiSummary(product.getAiSummary());
        response.setDescription(product.getDescription());
        response.setMerchantId(product.getMerchantId());
        response.setCategory(product.getCategoryId());
        response.setStatus(product.getStatus());
        
        // 构建地理位置字符串
        String location = String.format("%s %s %s", 
                product.getProvinceName(), 
                product.getCityName(), 
                product.getDistrictName());
        response.setLocation(location);

        // 获取 SKU 列表
        List<ProductSkuResponseDto> skus = skuService.getSkusByProductId(product.getId());
        response.setSkus(skus);

        // 审核结果
        if (auditResponse != null) {
            response.setRejectReason(auditResponse.getRejectReason());
            response.setSuggestions(auditResponse.getSuggestions());
            response.setAuditTime(auditResponse.getAuditTime());
        }
        return response;
    }
}




