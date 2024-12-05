package com.bank.movimiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MovimientoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovimientoServiceApplication.class, args);
    }
}