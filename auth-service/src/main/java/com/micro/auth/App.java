package com.micro.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.generic.service","com.micro.auth"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}