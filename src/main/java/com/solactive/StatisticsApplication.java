package com.solactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.solactive.controller",
        "com.solactive.service",
        "com.solactive.validator",
        "com.solactive.store",
        "com.solactive.util"
})
public class StatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisticsApplication.class, args);
    }

}
