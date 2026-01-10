FROM eclipse-temurin:17-jre
LABEL authors="hcy18"

# 设置工作目录
WORKDIR /app

COPY target/shopmind-product-service-0.0.1-SNAPSHOT.jar shopmind-product-service.jar

EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "/app/shopmind-product-service.jar"]