package com.salshaikhi.discountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class DiscountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscountServiceApplication.class, args);
    }

}
