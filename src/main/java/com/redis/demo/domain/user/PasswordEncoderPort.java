package com.redis.demo.domain.user;

public interface PasswordEncoderPort {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String hash);
}

