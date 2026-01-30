# ShopMind Product Service

商品服务微服务，负责商品管理、搜索推荐、热门新品计算等核心业务。

## 核心功能

### 1. 智能搜索
- **AI 关键词增强**：通过 AI 服务对用户搜索词进行语义分析，提取核心词和扩展词
- **全文检索**：基于 PostgreSQL ts_vector/ts_rank 实现全文搜索
- **语义排序**：调用推荐服务进行向量相似度排序，提升搜索准确性
- **降级策略**：当关键词增强失败时自动回退到纯语义搜索

### 2. 定时任务

#### 热门商品计算
- 执行频率：每 4 小时（08:00 - 20:00）
- 计算维度：新鲜度（40%）、销量（30%）、浏览量（30%），权重可配置
- 防冷启动：浏览量使用 Redis 记录短期数据，避免老商品长期积累导致的误判
- 结果缓存：热门商品 ID 列表存储于 Redis，过期时间可配置

#### 新品生成
- 执行频率：每 6 小时
- 筛选条件：最近 N 天内创建、已审核通过、未删除的商品
- 随机展示：从候选池中随机抽取 N 个作为新品，提升多样性

### 3. 用户行为埋点
- **浏览量统计**：用户查看商品详情时自动增加浏览计数
- **双重记录**：
  - `Product.view_count`：累计浏览量，持久化存储
  - `Redis:product:view:{id}`：周期内浏览量，用于热门商品计算热度评估

### 4. 商品 AI 流程
商品发布/更新时自动执行：
1. **内容审核**：检测标题和图片的合规性
2. **标签生成**：根据商品信息 AI 自动生成商品标签
3. **摘要生成**：生成商品 AI 描述摘要
4. **向量化**：将商品信息向量化，用于语义搜索

## 技术栈

| 技术                   | 说明 |
|----------------------|------|
| Spring Boot 3.x      | 基础框架 |
| Nacos                | 服务注册与配置中心 |
| PostgreSQL + PostGIS | 数据库，支持空间数据 |
| MyBatis Plus         | ORM 框架 |
| Redisson             | 分布式缓存客户端 |
| RustFS               | 分布式对象存储（图片） |

## 快速启动

```bash
# 编译打包
mvn clean package

# 运行
java -jar target/shopmind-product-service-0.0.1-SNAPSHOT.jar
```

## 依赖服务

- Nacos Server（配置中心与服务发现）
- PostgreSQL（主数据库）
- Redis（缓存）
- AI Service（内容审核、标签生成等）
- Recommendation Service（搜索排序）

## 接口示例

```bash
# 获取热门商品
GET /products/hot?limit=10

# 获取新品
GET /products/new?limit=10

# 商品搜索
GET /products/search?keyword=手机&pageNumber=1&pageSize=20

# 商品详情（自动埋点浏览量）
GET /products/detail/{productId}
```