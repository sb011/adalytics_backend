package com.adalytics.adalytics_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableAsync
public class AdalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdalyticsApplication.class, args);
    }

}