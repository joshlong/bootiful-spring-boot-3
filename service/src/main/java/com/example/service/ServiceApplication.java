package com.example.service;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}

class IoExample {

    @Bean
    ApplicationRunner io() {
        return args -> {

            try (var is = new FileInputStream(".")) {
                var next = -1;
                while ((next = is.read()) != -1)
                    next = is.read();
                // do something with read
            }

        };

    }

}