package com.propertysystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PropertySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertySystemApplication.class, args);
    }

}
