package com.shopmind.productservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

/**
 * SKU表
 * @TableName sm_skus
 */
@TableName(value ="sm_skus")
public class Sku {
    /**
     * SKU ID
     */
    @TableId
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU规格组合 {"颜色": "红色", "内存": "128GB"}
     */
    private Object skuSpecs;

    /**
     * SKU价格
     */
    private BigDecimal price;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * SKU图片URL
     */
    private String image;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * SKU ID
     */
    public Long getId() {
        return id;
    }

    /**
     * SKU ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 商品ID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 商品ID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * SKU规格组合 {"颜色": "红色", "内存": "128GB"}
     */
    public Object getSkuSpecs() {
        return skuSpecs;
    }

    /**
     * SKU规格组合 {"颜色": "红色", "内存": "128GB"}
     */
    public void setSkuSpecs(Object skuSpecs) {
        this.skuSpecs = skuSpecs;
    }

    /**
     * SKU价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * SKU价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 库存数量
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * 库存数量
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * SKU图片URL
     */
    public String getImage() {
        return image;
    }

    /**
     * SKU图片URL
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 更新时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 更新时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Sku other = (Sku) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProductId() == null ? other.getProductId() == null : this.getProductId().equals(other.getProductId()))
            && (this.getSkuSpecs() == null ? other.getSkuSpecs() == null : this.getSkuSpecs().equals(other.getSkuSpecs()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getStock() == null ? other.getStock() == null : this.getStock().equals(other.getStock()))
            && (this.getImage() == null ? other.getImage() == null : this.getImage().equals(other.getImage()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProductId() == null) ? 0 : getProductId().hashCode());
        result = prime * result + ((getSkuSpecs() == null) ? 0 : getSkuSpecs().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getStock() == null) ? 0 : getStock().hashCode());
        result = prime * result + ((getImage() == null) ? 0 : getImage().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", productId=").append(productId);
        sb.append(", skuSpecs=").append(skuSpecs);
        sb.append(", price=").append(price);
        sb.append(", stock=").append(stock);
        sb.append(", image=").append(image);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}