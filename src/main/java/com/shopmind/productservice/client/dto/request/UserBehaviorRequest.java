package com.shopmind.productservice.client.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Description:
 * Author: huangcy
 * Date: 2026-01-01
 */
@Data
public class UserBehaviorRequest {

    /**
     * 用户 id
     */
    @NotBlank
    private Long userId;

    /**
     * 按行为过滤
     */
    private String behaviorType;
    /**
     * 按目标过滤
     */
    private String targetType;
    /**
     * 最近多少天
     */
    @NotBlank
    @Min(1)
    private Integer day;
}
