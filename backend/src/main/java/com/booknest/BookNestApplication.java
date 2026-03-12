package com.booknest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BookNestApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookNestApplication.class, args);
    }
}
