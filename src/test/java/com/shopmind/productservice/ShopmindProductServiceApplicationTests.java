package com.shopmind.productservice;

import com.shopmind.framework.context.ResultContext;
import com.shopmind.productservice.client.AIServiceClient;
import com.shopmind.productservice.dto.request.ProductAuditRequestDto;
import com.shopmind.productservice.dto.response.ProductAuditResponseDto;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
class ShopmindProductServiceApplicationTests {

    @Resource
    private AIServiceClient aiServiceClient;

    @Test
    public void testGoToPythonWeb(){
        ProductAuditRequestDto dto = new ProductAuditRequestDto();
        dto.setProductId(264417720236244992L);
        dto.setTitle("Apple苹果笔记本电脑MacBookPro13寸15办公设计Air超薄");
        dto.setDescription("Apple MacBook Pro 13/15/16寸笔记本电脑，极致轻薄设计，重量仅1.5kg至2kg，便携性出色。");
        dto.setCoverImage("http://127.0.0.1:9000/product-service/products/264417721565839360.jpeg");
        dto.setDetailImages(List.of("http://127.0.0.1:9000/product-service/products/264417722262093824.png"));

        ResultContext<ProductAuditResponseDto> resp = aiServiceClient.auditProduct(dto);
        System.out.println(resp);

    }


    @Test
    public void test02() throws IOException, InterruptedException {
        // 创建 HttpClient
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

        // 构造请求体
        String jsonRequest = "{\"data\": \"Hello, FastAPI!\"}";

        // 创建 POST 请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://192.168.0.103:8085/ai/product/audit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        // 发送请求并接收响应
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 输出响应
        System.out.println("Response status code: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        // Response status code: 400
        //Response body: Invalid HTTP request received.

    }
}
