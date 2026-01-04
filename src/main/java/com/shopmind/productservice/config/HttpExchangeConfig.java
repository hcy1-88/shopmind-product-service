package com.shopmind.productservice.config;

import com.shopmind.framework.constant.ServiceNameConstant;
import com.shopmind.framework.util.ShopmindHttpClientUtils;
import com.shopmind.productservice.client.AIServiceClient;
import com.shopmind.productservice.client.RecommendationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Description: RPC 服务调用配置类
 * Author: huangcy
 * Date: 2025-12-26
 */
@Configuration
public class HttpExchangeConfig {

    /**
     * 远程 shopmind-ai-service 服务（框架已负载均衡）
     */
    @Bean
    public AIServiceClient  aiServiceClient(RestClient.Builder builder) {
        return ShopmindHttpClientUtils.createLoadBalancedClient(builder, ServiceNameConstant.AI_SERVICE, AIServiceClient.class);
    }

    @Bean
    public RecommendationClient recommendationClient(RestClient.Builder builder) {
        return ShopmindHttpClientUtils.createLoadBalancedClient(builder, ServiceNameConstant.RECOMMENDATION_SERVICE, RecommendationClient.class);
    }
}
