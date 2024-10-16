package com.book.network;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BookNetworkApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkApiApplication.class, args);
    }

}
