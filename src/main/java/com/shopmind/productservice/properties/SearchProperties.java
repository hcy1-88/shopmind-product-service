package com.shopmind.productservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: huangcy
 * Date: 2026-01-04
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "shopmind.search")
public class SearchProperties {
    /**
     * 全文搜索召回数
     */
    private Integer fullTextSearchRecall = 30;

    /**
     * 语义搜索召回数
     */
    private Integer semanticsSearchRecall = 20;
}
