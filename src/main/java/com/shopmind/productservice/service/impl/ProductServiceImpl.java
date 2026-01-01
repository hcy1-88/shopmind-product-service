package com.shopmind.productservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopmind.framework.context.PageResult;
import com.shopmind.framework.context.UserContext;
import com.shopmind.framework.id.IdGenerator;
import com.shopmind.productservice.client.AIServiceClient;
import com.shopmind.productservice.client.BaiduGeocodingClient;
import com.shopmind.productservice.client.dto.request.GenerateSummaryRequestDto;
import com.shopmind.productservice.client.dto.request.GenerateTagsRequestDto;
import com.shopmind.productservice.client.dto.request.ProductAuditRequestDto;
import com.shopmind.productservice.client.dto.request.VectorizeProductRequestDto;
import com.shopmind.productservice.client.dto.response.GenerateSummaryResponseDto;
import com.shopmind.productservice.client.dto.response.GenerateTagsResponseDto;
import com.shopmind.productservice.client.dto.response.ProductAuditResponseDto;
import com.shopmind.productservice.client.dto.response.VectorizeProductResponseDto;
import com.shopmind.productservice.constant.RedisKeyConstant;
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
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Resource
    private RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        // 1. 创建并初始化商品
        Product product = initializeNewProduct(requestDto);

        // 2. 创建 sku
        handleSpecsAndSkus(product.getId(), requestDto);

        // 3. 处理商品的完整 AI 流程（审核、标签、摘要、向量化等）
        return processProductWithAI(product, false);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponseDto updateProduct(Long productId, ProductRequestDto requestDto) {
        // 1. 校验并获取现有商品
        Product existingProduct = validateAndGetProduct(productId);
        
        // 2. 清理旧数据（规格、SKU、标签关联）
        cleanupOldProductData(productId);
        
        // 3. 更新商品基本信息
        updateProductBasicInfo(existingProduct, requestDto);

        // 3. 创建新的 sku
        handleSpecsAndSkus(existingProduct.getId(), requestDto);
        
        // 4. 处理商品的完整 AI 流程（审核、标签、摘要、向量化等）
        return processProductWithAI(existingProduct, true);
    }

    // ==================== 核心业务流程方法 ====================

    /**
     * 初始化新商品
     */
    private Product initializeNewProduct(ProductRequestDto requestDto) {
        Product product = new Product();
        product.setId(idGenerator.nextId());
        fillCommonProductFields(product, requestDto);
        this.save(product);
        log.info("成功创建商品，ID：{}", product.getId());
        return product;
    }

    /**
     * 校验并获取商品
     */
    private Product validateAndGetProduct(Long productId) {
        Product product = this.getById(productId);
        if (product == null) {
            throw new ProductServiceException("PRODUCT0004");
        }
        if (!isEditableStatus(product.getStatus())) {
            throw new ProductServiceException("PRODUCT0005", product.getStatus().getDescription());
        }
        return product;
    }

    /**
     * 清理商品的旧数据
     */
    private void cleanupOldProductData(Long productId) {
        productSpecService.deleteByProductId(productId);
        skuService.deleteByProductId(productId);
        productTagRelationService.deleteByProductId(productId);
        log.info("已清理商品 {} 的旧数据", productId);
    }

    /**
     * 更新商品基本信息
     */
    private void updateProductBasicInfo(Product product, ProductRequestDto requestDto) {
        product.setStatus(ProductStatus.PENDING_REVIEW);
        fillCommonProductFields(product, requestDto);
        this.updateById(product);
        log.info("成功更新商品基本信息，ID：{}", product.getId());
    }

    /**
     * 处理商品的完整 AI 流程
     * @param product 商品实体
     * @param isUpdate 是否为更新操作
     * @return 商品响应 DTO
     */
    private ProductResponseDto processProductWithAI(Product product, boolean isUpdate) {
        // 1. 处理规格和 SKU
        log.info("商品（id：{}， 是否为更新操作：{}）发布，正在执行 AI 流程 ----------------", product.getId(), isUpdate);

        // 2. 执行 AI 审核
        ProductAuditResponseDto auditResponse = performProductAudit(product);
        
        // 3. 根据审核结果更新商品状态
        updateProductStatus(product, auditResponse);

        List<ProductsTag> tags = new ArrayList<>();
        // 审核成功才能生成 tag、AI 摘要、向量化商品
        if (product.getStatus() == ProductStatus.APPROVED) {
            // 4. 生成并保存标签
            tags = generateAndSaveTags(product);
            List<Long> tagIds = extractTagIds(tags);

            // 5. 创建商品标签关联
            productTagRelationService.createRelations(product.getId(), tagIds);

            // 6. 执行 AI 增强操作（摘要、向量化）
            performAIEnhancement(product, tagIds);
        }

        // 7. 保存审核记录
        ProductAudit audit = saveAuditRecord(product, auditResponse);
        auditResponse.setAuditTime(audit.getCreatedAt());

        // 8. 构造并返回响应
        return buildProductResponse(product, auditResponse, tags);
    }

    /**
     * 更新商品状态（基于审核结果）
     */
    private void updateProductStatus(Product product, ProductAuditResponseDto auditResponse) {
        ProductStatus newStatus = StrUtil.equals(ProductStatus.APPROVED.getValue(), auditResponse.getAuditStatus())
                ? ProductStatus.APPROVED
                : ProductStatus.REJECTED;
        
        product.setStatus(newStatus);
        log.info("商品 {} 审核状态更新为：{}", product.getId(), newStatus);
    }

    /**
     * 提取标签 ID 列表
     */
    private List<Long> extractTagIds(List<ProductsTag> tags) {
        return tags.stream()
                .map(ProductsTag::getId)
                .collect(Collectors.toList());
    }

    /**
     * 执行 AI 增强操作（摘要生成、向量化）
     */
    private void performAIEnhancement(Product product, List<Long> tagIds) {
        // 生成 AI 摘要
        GenerateSummaryResponseDto summaryResponse = generateAiSummary(product);
        product.setAiSummary(summaryResponse.getSummary());
        this.updateById(product);

        // 向量化商品
        vectorizeProduct(product, tagIds);
    }

    @Override
    public PageResult<List<ProductResponseDto>> getProductsByMerchantId(Long merchantId, MerchantProductQueryParams params) {
        // 1. 查询商家的所有商品
        IPage<Product> products = queryMerchantProducts(merchantId, params);


        // 2. 批量查询审核记录
        Map<Long, ProductAuditResponseDto> auditMap = batchQueryAuditRecords(products.getRecords());

        // 3. 构建响应列表
        List<ProductResponseDto> productResponseDtos = buildProductResponseList(products.getRecords(), auditMap);
        return PageResult.<List<ProductResponseDto>>builder()
                .data(productResponseDtos)
                .total(products.getTotal())
                .pageNumber(products.getCurrent())
                .pageSize(products.getSize())
                .build();
    }

    /**
     * 查询商家的所有有效商品
     */
    private IPage<Product> queryMerchantProducts(Long merchantId, MerchantProductQueryParams params) {
        if (params.getPageNumber() == null || params.getPageSize() == null) {
            log.error("分页参数为 null");
            throw new IllegalArgumentException("分页参数异常！");
        }
        IPage<Product> page = new Page<>();
        page.setCurrent(params.getPageNumber());
        page.setSize(params.getPageSize());

        LambdaQueryChainWrapper<Product> wrapper = this.lambdaQuery()
                .eq(Product::getMerchantId, merchantId);

        if(StrUtil.isNotEmpty(params.getStatus())) {
            wrapper.eq(Product::getStatus, params.getStatus());
        }

        if (StrUtil.isNotEmpty(params.getKeyword())) {
            wrapper.like(Product::getName, params.getKeyword());
        }

        return wrapper
                .isNull(Product::getDeletedAt)
                .orderByDesc(Product::getCreatedAt)
                .page(page);
    }

    /**
     * 批量查询商品的审核记录
     */
    private Map<Long, ProductAuditResponseDto> batchQueryAuditRecords(List<Product> products) {
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();
        return productAuditService.getLatestAuditBatchByProductIds(productIds);
    }

    /**
     * 批量构建商品响应列表
     */
    private List<ProductResponseDto> buildProductResponseList(List<Product> products, 
                                                              Map<Long, ProductAuditResponseDto> auditMap) {
        return products.stream()
                .map(product -> {
                    ProductAuditResponseDto auditResponse = auditMap.get(product.getId());
                    List<ProductsTag> tags = productTagRelationService.findTagsByProductId(product.getId());
                    return buildProductResponse(product, auditResponse, tags);
                })
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteProductById(Long productId) {
        // 1. 验证商品存在性
        Product product = getProductOrThrow(productId);

        // 2. 软删除商品
        softDeleteProduct(product);

        // 3. 清理关联数据
        cleanupOldProductData(productId);
    }

    /**
     * 获取商品，如果不存在则抛出异常
     */
    private Product getProductOrThrow(Long productId) {
        Product product = this.getById(productId);
        if (product == null) {
            log.error("商品不存在，product id：{}", productId);
            throw new ProductServiceException("PRODUCT0004");
        }
        return product;
    }

    /**
     * 软删除商品
     */
    private void softDeleteProduct(Product product) {
        product.setDeletedAt(new Date());
        this.updateById(product);
        log.info("成功软删除商品，ID：{}", product.getId());
    }

    // ==================== 数据填充和处理方法 ====================

    /**
     * 判断商品是否处于可编辑状态
     * 审核中的商品不能编辑
     */
    private boolean isEditableStatus(ProductStatus status) {
        return status == ProductStatus.REJECTED ||
                status == ProductStatus.DRAFT ||
                status == ProductStatus.APPROVED;
    }

    /**
     * 填充商品的公共字段（创建 & 更新共用）
     */
    private void fillCommonProductFields(Product product, ProductRequestDto dto) {
        // 商家信息
        product.setMerchantId(UserContext.userId());
        
        // 基本信息
        fillBasicInfo(product, dto);
        
        // 地理位置信息
        fillLocationInfo(product, dto);
        
        // 图片处理
        processImages(product, dto);
    }

    /**
     * 填充商品基本信息
     */
    private void fillBasicInfo(Product product, ProductRequestDto dto) {
        product.setName(dto.getName());
        product.setCategoryId(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setPriceRange(dto.getPriceRange());
        product.setDescription(dto.getDescription());
    }

    /**
     * 填充地理位置信息
     */
    private void fillLocationInfo(Product product, ProductRequestDto dto) {
        product.setProvinceCode(dto.getProvinceCode());
        product.setProvinceName(dto.getProvinceName());
        product.setCityCode(dto.getCityCode());
        product.setCityName(dto.getCityName());
        product.setDistrictCode(dto.getDistrictCode());
        product.setDistrictName(dto.getDistrictName());
        product.setDetailAddress(dto.getDetailAddress());
        
        // 创建地理位置点
        product.setLocation(baiduGeocodingClient.createAddressLocation(
                product.getProvinceName(),
                product.getCityName(),
                product.getDistrictName(),
                product.getDetailAddress()
        ));
    }

    /**
     * 处理商品图片（封面 + 详情图）
     */
    private void processImages(Product product, ProductRequestDto dto) {
        // 处理封面图
        product.setCoverImage(imageService.getImageUrlFromBase64(dto.getCoverImage()));

        // 处理详情图
        List<String> detailImages = dto.getDetailImages();
        if (CollectionUtil.isNotEmpty(detailImages)) {
            List<String> detailImgUrls = detailImages.stream()
                    .map(imageService::getImageUrlFromBase64)
                    .toList();
            product.setDetailImages(detailImgUrls);
        } else {
            product.setDetailImages(Collections.emptyList());
        }
    }

    /**
     * 处理规格和 SKU 的创建
     */
    private void handleSpecsAndSkus(Long productId, ProductRequestDto dto) {
        // 创建规格
        List<ProductSpec> specs = productSpecService.addBatchProductSpecs(productId, dto.getSkuSpecs());
        log.info("商品 {} 创建了 {} 个规格参数", productId, specs.size());

        // 创建 SKU
        List<ProductSkuResponseDto> skus = skuService.addBatchSku(productId, dto.getSkuItems());
        log.info("商品 {} 创建了 {} 条 SKU", productId, skus.size());
    }

    // ==================== AI 服务调用方法 ====================

    /**
     * 执行商品审核
     */
    private ProductAuditResponseDto performProductAudit(Product product) {
        ProductAuditRequestDto request = buildAuditRequest(product);
        ProductAuditResponseDto response = aiServiceClient.auditProduct(request).getData();
        log.info("商品 {} 审核完成，结果：{}", product.getId(), response.getAuditStatus());
        return response;
    }

    /**
     * 构建审核请求
     */
    private ProductAuditRequestDto buildAuditRequest(Product product) {
        return ProductAuditRequestDto.builder()
                .productId(product.getId())
                .title(product.getName())
                .description(product.getDescription())
                .coverImage(product.getCoverImage())
                .detailImages(product.getDetailImages())
                .build();
    }

    /**
     * 生成并保存商品标签
     */
    private List<ProductsTag> generateAndSaveTags(Product product) {
        // 调用 AI 服务生成标签
        GenerateTagsRequestDto request = buildTagsRequest(product);
        GenerateTagsResponseDto response = aiServiceClient.generateProductTags(request).getData();
        log.info("商品 {} 生成了 {} 个标签", product.getId(), response.getTags().size());

        // 保存标签到数据库
        return saveTagsToDatabase(response.getTags());
    }

    /**
     * 构建标签生成请求
     */
    private GenerateTagsRequestDto buildTagsRequest(Product product) {
        return GenerateTagsRequestDto.builder()
                .productId(product.getId())
                .title(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .imageUrls(Collections.singletonList(product.getCoverImage()))
                .build();
    }

    /**
     * 保存标签到数据库（如果不存在则创建）
     */
    private List<ProductsTag> saveTagsToDatabase(List<GenerateTagsResponseDto.TagInfo> tagInfos) {
        return tagInfos.stream()
                .map(tagInfo -> productsTagService.findOrCreateTag(
                        tagInfo.getName(),
                        TagType.AI_GENERATED,
                        tagInfo.getColor()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 生成 AI 摘要
     */
    private GenerateSummaryResponseDto generateAiSummary(Product product) {
        GenerateSummaryRequestDto request = buildSummaryRequest(product);
        GenerateSummaryResponseDto response = aiServiceClient.generateProductSummary(request).getData();
        log.info("商品 {} AI 摘要生成成功", product.getId());
        return response;
    }

    /**
     * 构建摘要生成请求
     */
    private GenerateSummaryRequestDto buildSummaryRequest(Product product) {
        GenerateSummaryRequestDto resq = GenerateSummaryRequestDto.builder()
                .productId(product.getId())
                .title(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .build();
        resq.setPrice(getOnePrice(product));
        return resq;
    }

    /**
     * 至少取一个价格作为商品的参考价格
     * @param product 商品
     * @return 价格
     */
    private static BigDecimal getOnePrice(Product product) {
        BigDecimal price = product.getPrice();
        if (price == null) {
            price = product.getPriceRange().getMin();
            if (price == null) {
                price = product.getOriginalPrice();
            }
        }
        if (price == null) {
            log.error("商品 Id：{} 价格确实！", product.getId());
            throw new ProductServiceException("PRODUCT0006");
        }
        return price;
    }

    /**
     * 向量化商品
     */
    private void vectorizeProduct(Product product, List<Long> tagIds) {
        // 构建向量化请求
        List<String> tagNames = getTagNamesByIds(tagIds);
        VectorizeProductRequestDto request = buildVectorizeRequest(product, tagNames);
        
        // 调用向量化服务
        VectorizeProductResponseDto response = aiServiceClient.vectorizeProduct(request).getData();
        
        // 记录结果
        logVectorizeResult(product.getId(), response);
    }

    /**
     * 根据标签 ID 获取标签名称列表
     */
    private List<String> getTagNamesByIds(List<Long> tagIds) {
        return tagIds.stream()
                .map(productsTagService::getById)
                .filter(Objects::nonNull)
                .map(ProductsTag::getName)
                .collect(Collectors.toList());
    }

    /**
     * 构建向量化请求
     */
    private VectorizeProductRequestDto buildVectorizeRequest(Product product, List<String> tagNames) {
        VectorizeProductRequestDto vectorizeProductRequestDto = VectorizeProductRequestDto.builder()
                .productId(product.getId())
                .title(product.getName())
                .description(product.getDescription())
                .aiSummary(product.getAiSummary())
                .tags(tagNames)
                .build();
        vectorizeProductRequestDto.setPrice(getOnePrice(product));
        return vectorizeProductRequestDto;
    }

    /**
     * 记录向量化结果
     */
    private void logVectorizeResult(Long productId, VectorizeProductResponseDto response) {
        if (Boolean.TRUE.equals(response.getSuccess())) {
            log.info("商品 {} 向量化成功，向量 ID：{}", productId, response.getVectorId());
        } else {
            log.error("商品 {} 向量化失败：{}", productId, response.getErrorMessage());
        }
    }

    /**
     * 保存审核记录
     */
    private ProductAudit saveAuditRecord(Product product, ProductAuditResponseDto auditResponse) {
        ProductAudit audit = buildAuditEntity(product, auditResponse);
        productAuditService.save(audit);
        log.info("商品 {} 审核记录保存成功", product.getId());
        return audit;
    }

    /**
     * 构建审核记录实体
     */
    private ProductAudit buildAuditEntity(Product product, ProductAuditResponseDto auditResponse) {
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
        return audit;
    }

    // ==================== 响应构建方法 ====================

    /**
     * 构建商品响应 DTO
     */
    private ProductResponseDto buildProductResponse(Product product, ProductAuditResponseDto auditResponse, List<ProductsTag> tags) {
        ProductResponseDto response = new ProductResponseDto();
        
        // 基本信息
        setBasicProductInfo(response, product);
        
        // 地理位置
        response.setLocation(formatLocation(product));
        fillAddressField(product, response);

        // SKU 列表
        response.setSkus(skuService.getSkusByProductId(product.getId()));
        
        // 审核信息
        setAuditInfo(response, auditResponse);
        
        // 标签信息
        response.setTagInfo(convertToTagInfoList(tags));
        
        return response;
    }

    /**
     * 设置商品基本信息
     */
    private void setBasicProductInfo(ProductResponseDto response, Product product) {
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
    }

    /**
     * 格式化地理位置信息
     */
    private String formatLocation(Product product) {
        return String.format("%s %s %s", 
                product.getProvinceName(), 
                product.getCityName(), 
                product.getDistrictName());
    }

    /**
     * 设置审核信息
     */
    private void setAuditInfo(ProductResponseDto response, ProductAuditResponseDto auditResponse) {
        if (auditResponse != null) {
            response.setRejectReason(auditResponse.getRejectReason());
            response.setSuggestions(auditResponse.getSuggestions());
            response.setAuditTime(auditResponse.getAuditTime());
        }
    }

    /**
     * 将标签实体列表转换为标签信息列表
     */
    private List<GenerateTagsResponseDto.TagInfo> convertToTagInfoList(List<ProductsTag> tags) {
        return tags.stream()
                .map(this::convertToTagInfo)
                .toList();
    }

    /**
     * 将标签实体转换为标签信息
     */
    private GenerateTagsResponseDto.TagInfo convertToTagInfo(ProductsTag tag) {
        GenerateTagsResponseDto.TagInfo tagInfo = new GenerateTagsResponseDto.TagInfo();
        tagInfo.setName(tag.getName());
        tagInfo.setColor(tag.getColor());
        return tagInfo;
    }

    @Override
    public List<ProductResponseDto> getProductsBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return convertProductResponseByIds(ids);
    }

    // ==================== 热门商品相关方法 ====================

    @Override
    public List<ProductResponseDto> getHotProducts(Integer limit) {
        // 1. 从 Redis 获取热门商品 ID 列表
        List<Long> hotProductIds = getHotProductIdsFromRedis(limit);

        if (hotProductIds.isEmpty()) {
            log.warn("Redis 中没有热门商品数据，返回空列表");
            return Collections.emptyList();
        }

        // 2. 根据 ID 批量查询商品
        return convertProductResponseByIds(hotProductIds);

    }


    /**
     * 从 Redis 获取热门商品 ID 列表
     */
    private List<Long> getHotProductIdsFromRedis(Integer limit) {
        String key = RedisKeyConstant.HOT_PRODUCTS_GUEST;
        RList<Long> list = redissonClient.getList(key);

        // 检查列表是否存在
        if (!list.isExists() || list.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取列表范围 [0, limit-1]
        int end = Math.min(limit, list.size());
        return new ArrayList<>(list.subList(0, end));
    }

    /**
     * 根据商品 id，直接返回响应
     */
    private List<ProductResponseDto> convertProductResponseByIds(List<Long> productIds) {
        List<Product> products = this.lambdaQuery()
                .in(Product::getId, productIds)
                .eq(Product::getStatus, ProductStatus.APPROVED)
                .isNull(Product::getDeletedAt)
                .list();

        // 3. 查 tag
        List<Long> ids = products.stream().map(Product::getId).toList();
        Map<Long, List<GenerateTagsResponseDto.TagInfo>> tagsByProductIds = productTagRelationService.findTagsByProductIds(ids);

        // 4, 转换 dto
        return products.stream().map(p -> convertToDto(p, tagsByProductIds.get(p.getId()))).toList();
    }

    /**
     * 将 Product 转换为 ProductResponseDto
     */
    private ProductResponseDto convertToDto(Product product, List<GenerateTagsResponseDto.TagInfo> tags) {

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .image(product.getCoverImage())
                .originalPrice(product.getOriginalPrice())
                .priceRange(product.getPriceRange())
                .category(product.getCategoryId())
                .aiSummary(product.getAiSummary())
                .description(product.getDescription())
                .salesCount(product.getSalesCount())
                .location(formatLocation(product))
                .tagInfo(tags)
                .build();
    }

    @Override
    public ProductResponseDto getProductDetailById(Long productId) {
        // 1. 查询商品基本信息
        Product product = this.lambdaQuery()
                .eq(Product::getId, productId)
                .eq(Product::getStatus, ProductStatus.APPROVED)
                .isNull(Product::getDeletedAt)
                .one();

        if (product == null) {
            log.error("商品不存在或未上架，product id：{}", productId);
            throw new ProductServiceException("PRODUCT0004");
        }

        // 2. 查询标签信息
        List<ProductsTag> tags = productTagRelationService.findTagsByProductId(productId);
        List<GenerateTagsResponseDto.TagInfo> tagInfoList = convertToTagInfoList(tags);

        // 3. 使用 convertToDto 构建基础响应
        ProductResponseDto responseDto = convertToDto(product, tagInfoList);

        // 4. 查询并设置 SKU 信息
        List<ProductSkuResponseDto> skus = skuService.getSkusByProductId(productId);
        responseDto.setSkus(skus);

        // 5. 设置详情图片
        responseDto.setImages(product.getDetailImages());

        // 6. 设置商品状态
        responseDto.setStatus(product.getStatus());

        // 7, 商品位置
        fillAddressField(product, responseDto);

        log.info("成功查询商品详情，商品ID：{}", productId);
        return responseDto;
    }

    private void fillAddressField(Product product, ProductResponseDto responseDto) {
        responseDto.setProvinceCode(product.getProvinceCode());
        responseDto.setProvinceName(product.getProvinceName());
        responseDto.setCityCode(product.getCityCode());
        responseDto.setCityName(product.getCityName());
        responseDto.setDistrictCode(product.getDistrictCode());
        responseDto.setDistrictName(product.getDistrictName());
        responseDto.setDetailAddress(product.getDetailAddress());
    }

}




