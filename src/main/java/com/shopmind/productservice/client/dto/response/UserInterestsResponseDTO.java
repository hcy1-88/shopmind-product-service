package com.shopmind.productservice.client.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class UserInterestsResponseDTO {
    private Long userId;

    /**
     * key 是兴趣的英文 code，value 是中文兴趣名称
     */
    private Map<String, String> interests;
}