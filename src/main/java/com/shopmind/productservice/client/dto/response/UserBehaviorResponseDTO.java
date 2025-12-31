package com.shopmind.productservice.client.dto.response;

import com.shopmind.productservice.enums.BehaviorTargetType;
import com.shopmind.productservice.enums.BehaviorType;
import lombok.Data;

import java.util.Date;

@Data
public class UserBehaviorResponseDTO {
    private Long userId;
    private BehaviorType behaviorType;
    private BehaviorTargetType targetType;
    private String targetId;
    private Date createdAt;
}