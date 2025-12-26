package com.shopmind.productservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 商品状态枚举
 * 数据库存储为小写字符串（如 "draft"）
 */
@Getter
public enum ProductStatus {

    DRAFT("draft", "草稿"),
    PENDING_REVIEW("pending_review", "待审核"),
    APPROVED("approved", "已通过"),
    REJECTED("rejected", "已拒绝");

    /**
     * 存入数据库的值（对应 status 字段）
     */
    @EnumValue
    private final String value;

    /**
     * 中文描述（可选，用于展示）
     */
    private final String description;

    ProductStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 用于 JSON 序列化时输出 value（如返回给前端）
     */
    @JsonValue
    public String getValue() {
        return this.value;
    }

    /**
     * 根据 value 获取枚举（可选工具方法）
     */
    public static ProductStatus fromValue(String value) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("非法 ProductStatus value: " + value);
    }
}