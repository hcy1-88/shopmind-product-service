package com.shopmind.productservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Description:
 * Author: huangcy
 * Date: 2025-12-27
 */
public enum TagType {

    AI_GENERATED("ai_generated", "AI 生成的 tag"),
    MANUAL("manual", "运营人员收到添加的 tag");

    @EnumValue
    private final String value;

    @Getter
    private final String description;

    TagType(String value, String description) {
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
     * 根据 value 获取枚举
     */
    public static TagType fromValue(String value) {
        for (TagType status : TagType.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("非法 TagType value: " + value);
    }
}
