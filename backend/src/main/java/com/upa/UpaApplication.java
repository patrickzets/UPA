package com.upa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpaApplication.class, args);
    }
}
