package com.zerobase.zerostore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ZerostoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZerostoreApplication.class, args);
    }

}
