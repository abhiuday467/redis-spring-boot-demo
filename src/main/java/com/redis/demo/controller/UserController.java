package com.redis.demo.controller;

import com.redis.demo.model.User;
import com.redis.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static class CreateUserRequest {
        public String email;
        public String firstName;
        public String lastName;
        public String password;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest req) {
        if (req == null || req.email == null || req.password == null || req.firstName == null || req.lastName == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }
        if (userRepository.existsByEmailId(req.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already in use"));
        }
        User user = new User();
        user.setEmailId(req.email);
        user.setFirstName(req.firstName);
        user.setLastName(req.lastName);
        user.setPasswordHash(passwordEncoder.encode(req.password));

        User saved = userRepository.save(user);

        Map<String, Object> body = Map.of(
                "id", saved.getId(),
                "email", saved.getEmailId(),
                "firstName", saved.getFirstName(),
                "lastName", saved.getLastName()
        );
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).body(body);
    }
}

