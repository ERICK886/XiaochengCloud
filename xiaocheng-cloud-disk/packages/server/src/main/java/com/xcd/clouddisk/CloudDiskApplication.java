package com.xcd.clouddisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CloudDiskApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudDiskApplication.class, args);
    }
}
