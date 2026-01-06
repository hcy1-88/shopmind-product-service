package com.shopmind.productservice.job;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.shopmind.productservice.constant.RedisKeyConstant;
import com.shopmind.productservice.entity.Product;
import com.shopmind.productservice.enums.ProductStatus;
import com.shopmind.productservice.properties.RecommendProperties;
import com.shopmind.productservice.service.ProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 热门商品生成器
 * 定时任务：根据新鲜度、销量、浏览量计算热门商品
 * 
 * @author hcy18
 * @date 2025-12-31
 */
@Slf4j
@Component
public class HotProductGenerationJob {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ProductService productService;

    @Resource
    private RecommendProperties recommendProperties;


    /**
     * 定时任务：每 4 小时执行行一次，早上 8 点开始
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 8-20/4 * * ?")
    public void generateHotProducts() {
        log.info("========== 开始计算热门商品 ==========");
        long startTime = System.currentTimeMillis();

        try {
            // 1. 获取所有已审核通过的商品
            List<Product> approvedProducts = getApprovedProducts(recommendProperties.getRecallFromDB());
            if (approvedProducts.isEmpty()) {
                log.warn("没有已审核通过的商品，跳过热门商品计算");
                return;
            }

            // 2. 计算每个商品的热度分数
            Map<Long, Double> productScores = calculateHotScores(approvedProducts);

            // 3. 按热度分数排序，取前 N 个
            List<Long> hotProductIds = productScores.entrySet().stream()
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                    .limit(recommendProperties.getHotLimit())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // 4. 保存到 Redis
            saveHotProductsToRedis(hotProductIds);

            long duration = System.currentTimeMillis() - startTime;
            log.info("========== 热门商品计算完成，共 {} 个商品，耗时 {}ms ==========", 
                    hotProductIds.size(), duration);

        } catch (Exception e) {
            log.error("计算热门商品失败", e);
        }
    }

    /**
     * 获取所有已审核通过的商品
     */
    private List<Product> getApprovedProducts(int limit) {
        return productService.lambdaQuery()
                .eq(Product::getStatus, ProductStatus.APPROVED)
                .isNull(Product::getDeletedAt)
                .last("ORDER BY RANDOM() LIMIT " + limit)
                .list();
    }

    /**
     * 计算商品热度分数
     */
    private Map<Long, Double> calculateHotScores(List<Product> products) {
        Map<Long, Double> scores = new HashMap<>();

        // 获取归一化参数
        NormalizationParams params = calculateNormalizationParams(products);

        for (Product product : products) {
            Long productId = product.getId();

            // 1. 新鲜度分数（时间越近分数越高）
            double freshnessScore = calculateFreshnessScore(product, params);

            // 2. 销量分数
            double salesScore = calculateSalesScore(product, params);

            // 3. 浏览量分数
            double viewsScore = calculateViewsScore(productId, params);

            // 4. 综合热度 = 加权求和
            double totalScore = freshnessScore * recommendProperties.getFreshWeight()
                              + salesScore * recommendProperties.getSalesWeight()
                              + viewsScore * recommendProperties.getViewsWeight();

            scores.put(productId, totalScore);

            log.debug("商品 {} 热度分数 - 新鲜度:{}, 销量:{}, 浏览量:{}, 综合:{}",
                    productId, freshnessScore, salesScore, viewsScore, totalScore);
        }

        return scores;
    }

    /**
     * 计算归一化参数（用于将不同量纲的指标归一化到 0-1）
     */
    private NormalizationParams calculateNormalizationParams(List<Product> products) {
        // 计算时间范围
        long now = System.currentTimeMillis();
        long oldestTime = products.stream()
                .map(p -> p.getCreatedAt().getTime())
                .min(Long::compare)
                .orElse(now);
        long timeRange = now - oldestTime;

        // 计算销量最大值
        long maxSales = products.stream()
                .mapToLong(Product::getSalesCount)
                .max()
                .orElse(1L);

        // 计算浏览量最大值
        long maxViews = products.stream()
                .mapToLong(p -> {
                    String key = RedisKeyConstant.PRODUCT_VIEW_PREFIX + p.getId();
                    return redissonClient.getAtomicLong(key).get();
                })
                .max()
                .orElse(1L);

        long maxLikeCounts = products.stream()
                .mapToLong(Product::getLikeCount)
                .max()
                .orElse(1L);

        return new NormalizationParams(timeRange, maxSales, maxViews, maxLikeCounts);
    }

    /**
     * 计算新鲜度分数（0-1，越新越高）
     */
    private double calculateFreshnessScore(Product product, NormalizationParams params) {
        long now = System.currentTimeMillis();
        long productTime = product.getCreatedAt().getTime();
        long age = now - productTime;

        if (params.timeRange <= 0) {
            return 1.0; // 所有商品同一时间创建
        }

        // 归一化：越新的商品分数越高
        return 1.0 - ((double) age / params.timeRange);
    }

    /**
     * 计算销量分数（0-1，销量越高分数越高）
     */
    private double calculateSalesScore(Product product, NormalizationParams params) {
        long sales = product.getSalesCount();
        if (params.maxSales <= 0) {
            return 0.0;
        }
        // 归一化
        return (double) sales / params.maxSales;
    }

    /**
     * 计算浏览量分数（0-1，浏览量越高分数越高）
     */
    private double calculateViewsScore(Long productId, NormalizationParams params) {
        String key = RedisKeyConstant.PRODUCT_VIEW_PREFIX + productId;
        RAtomicLong view = redissonClient.getAtomicLong(key);
        // 浏览量加 1
        long views = view.get();

        if (params.maxViews <= 0) {
            return 0.0;
        }

        // 归一化
        return (double) views / params.maxViews;
    }

    /**
     * 计算点赞分数（0-1，点赞越高分数越高）
     */
    private double calculateLikeScore(Product product, NormalizationParams params) {
        long likeCount = product.getLikeCount();
        if (params.likeCount <= 0) {
            return 0.0;
        }
        // 归一化
        return (double) likeCount / params.likeCount;
    }

    /**
     * 保存热门商品到 Redis
     */
    private void saveHotProductsToRedis(List<Long> hotProductIds) {
        String key = RedisKeyConstant.HOT_PRODUCTS_GUEST;
        RList<Long> list = redissonClient.getList(key);

        // 删除旧数据
        list.delete();

        // 存储新数据（使用 List 结构）
        if (!hotProductIds.isEmpty()) {
            list.addAll(hotProductIds);

            // 设置过期时间
            list.expire(Duration.ofDays(recommendProperties.getHotExpire()));

            log.info("成功保存 {} 个热门商品到 Redis", hotProductIds.size());
        }
    }

    /**
     * 归一化参数（内部类）
     */
    private static class NormalizationParams {
        final long timeRange;   // 时间范围（毫秒）
        final long maxSales;    // 最大销量
        final long maxViews;    // 最大浏览量
        final long likeCount;

        NormalizationParams(long timeRange, long maxSales, long maxViews, long likeCount) {
            this.timeRange = timeRange;
            this.maxSales = maxSales;
            this.maxViews = maxViews;
            this.likeCount = likeCount;
        }
    }
}

