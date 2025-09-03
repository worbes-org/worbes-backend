package com.worbes.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.worbes"})
@EnableScheduling
public class WorbesBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorbesBatchApplication.class, args);
    }
}
