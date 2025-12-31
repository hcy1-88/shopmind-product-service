package com.shopmind.productservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum BehaviorTargetType {

    PRODUCT("product", "商品"),
    REVIEW("review", "评论"),
    ORDER("order", "订单");

    @EnumValue
    private final String value;

    private final String description;

    BehaviorTargetType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    public static BehaviorTargetType fromValue(String value) {
        for (BehaviorTargetType type : BehaviorTargetType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null; // 或抛异常
    }
}