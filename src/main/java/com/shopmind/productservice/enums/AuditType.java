package com.shopmind.productservice.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Description:
 * Author: huangcy
 * Date: 2025-12-27
 */
public enum AuditType {
    AI("ai", "AI 审核"),
    MANUAL("manual", "人工审核");

    @EnumValue
    private final String value;

    @Getter
    private final String description;

    AuditType(String value, String description) {
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
    public static AuditType fromValue(String value) {
        for (AuditType status : AuditType.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("非法 AuditType value: " + value);
    }
}
