package com.shopmind.productservice.dto.response;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: huangcy
 * Date: 2026-01-04
 */
@Data
public class EnhanceKeywordResponseDTO {
    private List<String> coreWords;
    private List<String> expandWords;
}