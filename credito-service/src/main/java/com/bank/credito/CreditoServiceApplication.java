package com.bank.credito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CreditoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreditoServiceApplication.class, args);
    }
}