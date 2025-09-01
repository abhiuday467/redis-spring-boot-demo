package com.redis.demo.domain.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public UserService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserCommand cmd) {
        validate(cmd);
        if (userRepository.existsByEmailId(cmd.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        User u = new User();
        u.setEmailId(cmd.getEmail());
        u.setFirstName(cmd.getFirstName());
        u.setLastName(cmd.getLastName());
        u.setPasswordHash(passwordEncoder.hash(cmd.getPassword()));
        return userRepository.save(u);
    }

    private void validate(CreateUserCommand cmd) {
        if (cmd == null || isBlank(cmd.getEmail()) || isBlank(cmd.getFirstName()) || isBlank(cmd.getLastName()) || isBlank(cmd.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

