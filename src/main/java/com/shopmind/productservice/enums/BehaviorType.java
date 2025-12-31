package com.shopmind.productservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户行为类型枚举
 * 对应值: view / like / share / search / add_cart / purchase
 */
@Getter
public enum BehaviorType {

    VIEW("view", "浏览"),
    LIKE("like", "点赞"),
    SHARE("share", "分享"),
    SEARCH("search", "搜索"),
    ADD_CART("add_cart", "加入购物车"),
    PURCHASE("purchase", "购买");

    @EnumValue
    private final String value;

    private final String description;

    BehaviorType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    public static BehaviorType fromValue(String value) {
        for (BehaviorType type : BehaviorType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null; // 或抛异常
    }
}