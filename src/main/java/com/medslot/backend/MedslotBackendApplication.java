package com.medslot.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.medslot")
public class MedslotBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedslotBackendApplication.class, args);
    }
}