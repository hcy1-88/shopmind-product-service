package com.shopmind.productservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.shopmind.productservice.dto.business.PriceRange;
import com.shopmind.productservice.enums.ProductStatus;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品表
 * @TableName sm_products
 */
@Data
@TableName(value ="sm_products")
public class Product {
    /**
     * 商品ID
     */
    @TableId
    private Long id;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商品标题
     */
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 详情图URL数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> detailImages;

    /**
     * 商品描述
     */
    private String description;

    /**
     * AI生成的商品摘要
     */
    private String aiSummary;

    /**
     * 单一价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 价格区间 {"min": 0, "max": 0}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private PriceRange priceRange;

    /**
     * 商品状态：draft/pending_review/approved/rejected
     */
    private ProductStatus status;

    /**
     * 发货地省份编码
     */
    private String provinceCode;

    /**
     * 发货地省份名称
     */
    private String provinceName;

    /**
     * 发货地城市编码
     */
    private String cityCode;

    /**
     * 发货地城市名称
     */
    private String cityName;

    /**
     * 发货地区县编码
     */
    private String districtCode;

    /**
     * 发货地区县名称
     */
    private String districtName;

    /**
     * 发货地详细地址
     */
    private String detailAddress;

    /**
     * 发货地地理位置（WGS84）, 需调用 {@link com.shopmind.productservice.client.BaiduGeocodingClient#createAddressLocation(String, String, String, String)} 获取
     */
    private Point location;

    /**
     * 浏览次数
     */
    private Long viewCount;

    /**
     * 点赞次数
     */
    private Long likeCount;

    /**
     * 销量
     */
    private Long salesCount;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 软删除时间
     */
    private Date deletedAt;



}