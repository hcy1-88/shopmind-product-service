package com.shopmind.productservice.job;

import com.shopmind.productservice.constant.RedisKeyConstant;
import com.shopmind.productservice.entity.Product;
import com.shopmind.productservice.enums.ProductStatus;
import com.shopmind.productservice.properties.RecommendProperties;
import com.shopmind.productservice.service.ProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 新品，定时更新
 * Author: huangcy
 * Date: 2026-01-04
 */
@Slf4j
@Component
public class NewProductJob {
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ProductService productService;

    @Resource
    private RecommendProperties  recommendProperties;


    /**
     * 定时任务：每天凌晨 2 点执行
     * cron: 秒 分 时 日 月 周
     */
//    @Scheduled(cron = "0 0 2 * * ?")  TODO 记得改回来
    @Scheduled(cron = "0 */20 * * * ?")  // 暂时每点20分钟执行一次
    public void generateNewProducts() {
        log.info("========== 开始生成新品商品列表 ==========");
        long startTime = System.currentTimeMillis();

        try {
            // 1. 计算7天前的时间点
            LocalDateTime window = LocalDateTime.now().minusDays(recommendProperties.getFreshWindow());

            // 2. 查询最近7天内创建、已审核、未删除的商品（按创建时间倒序）
            List<Product> newProducts = productService.lambdaQuery()
                    .eq(Product::getStatus, ProductStatus.APPROVED)
                    .ge(Product::getCreatedAt, window)
                    .isNull(Product::getDeletedAt)
                    .orderByDesc(Product::getCreatedAt)
                    .last("ORDER BY RANDOM() LIMIT " + recommendProperties.getRecallFromDB()) // 防止数据过多
                    .list();

            // 3. 提取商品ID
            List<Long> newProductIds = newProducts.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());

            // 4. 保存到 Redis
            saveNewProductsToRedis(newProductIds);

            long duration = System.currentTimeMillis() - startTime;
            log.info("========== 新品商品列表生成完成，共 {} 个商品，耗时 {}ms ==========",
                    newProductIds.size(), duration);

        } catch (Exception e) {
            log.error("生成新品商品列表失败", e);
        }
    }

    /**
     * 保存新品商品ID列表到 Redis
     */
    private void saveNewProductsToRedis(List<Long> newProductIds) {
        String key = RedisKeyConstant.PRODUCT_NEW; // 使用新 key
        RList<Long> list = redissonClient.getList(key);

        // 删除旧数据
        list.delete();

        // 存储新数据
        if (!newProductIds.isEmpty()) {
            list.addAll(newProductIds);

            // 设置过期时间：25 小时（确保覆盖到下次执行）
            list.expire(Duration.ofHours(recommendProperties.getNewProductExpire()));

            log.info("成功保存 {} 个新品商品到 Redis", newProductIds.size());
        } else {
            log.warn("没有符合条件的新品商品");
        }
    }
}
