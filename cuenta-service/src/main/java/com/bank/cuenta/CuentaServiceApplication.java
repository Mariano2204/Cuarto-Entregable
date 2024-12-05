package com.bank.cuenta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CuentaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CuentaServiceApplication.class, args);
    }
}