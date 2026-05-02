package com.integration.shopifybling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class ShopifyBlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopifyBlingApplication.class, args);
    }
}
