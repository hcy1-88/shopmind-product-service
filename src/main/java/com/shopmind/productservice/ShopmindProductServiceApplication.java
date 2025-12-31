package com.shopmind.productservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.shopmind.productservice.mapper")
@EnableScheduling
public class ShopmindProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopmindProductServiceApplication.class, args);
    }

}
