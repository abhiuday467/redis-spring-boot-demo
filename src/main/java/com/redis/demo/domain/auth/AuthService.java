package com.redis.demo.domain.auth;

import com.redis.demo.domain.user.PasswordEncoderPort;
import com.redis.demo.domain.user.User;
import com.redis.demo.domain.user.UserRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public AuthService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String email, String rawPassword) {
        var userOpt = userRepository.findByEmailId(email);
        var user = userOpt.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return user;
    }
}

