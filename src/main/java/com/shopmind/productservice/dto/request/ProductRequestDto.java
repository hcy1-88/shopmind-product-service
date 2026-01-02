package com.shopmind.productservice.dto.request;

import com.shopmind.productservice.dto.business.PriceRange;
import com.shopmind.productservice.dto.business.SkuSpecValue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Description:
 * Author: huangcy
 * Date: 2025-12-26
 */
@Data
public class ProductRequestDto {
    // ========================= 商品主体信息 ======================

    /**
     * 商品标题
     */
    @NotBlank(message = "商品标题不能为空")
    private String name;

    /**
     * 商品分类 id
     */
    @NotNull(message = "商品分类不能为空")
    private Long category;

    /**
     * 商品预览图片base64带前缀，前端传递格式形如 data:image/png;base64,xxxxxx
     */
    @NotBlank(message = "商品预览图不能为空")
    private String coverImage;

    /**
     * 多个商品的详情图片 base64带前缀，前端传递格式形如 data:image/png;base64,xxxxxx
     */
    private List<String> detailImages;

    /**
     * 现价（单一价格）
     */
    private BigDecimal price;

    /**
     * 原价（单一价格）
     */
    private BigDecimal originalPrice;

    /**
     * 价格范围
     */
    private PriceRange priceRange;

    /**
     * 商品描述
     */
    @NotBlank(message = "商品描述不能为空")
    private String description;


    // ============================== sku ====================

    /**
     * 每个规格参数和其值：
     * [
     *   {name: "颜色", values: [{value: "星光色", image:"xxx"}],
     *   {name: "内存", values: [{value: "512GB", image:"xxx"}]
     * ]
     */
    private List<SkuSpecDto> skuSpecs;

    /**
     * 所有 sku 组合：
     * [{
     * 	image: "xxxx",
     * 	price: 19199,
     * 	specs: {
     * 		颜色: "星光色",
     * 		存储: "512GB"
     * 	  },
     * 	stock: 10
     * }]
     */
    private List<SkuItemDto> skuItems;

    // ============================ 地址 ====================

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 省份名
     */
    private String provinceName;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 城市名
     */
    private String cityName;

    /**
     * 区域编码
     */
    private String districtCode;

    /**
     * 区域名
     */
    private String districtName;

    /**
     * 详细地址
     */
    private String detailAddress;



}
