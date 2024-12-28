package com.zerobase.zerostore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//@SpringBootApplication
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ZerostoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZerostoreApplication.class, args);
    }

}
