package com.redis.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot with Redis Caching!";
    }

    @GetMapping("/health")
    public String health() {
        return "Application is running!";
    }
}
