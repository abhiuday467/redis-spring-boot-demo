package com.redis.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            HttpSession session,
            @RequestParam String userId,
            @RequestParam String username,
            @RequestParam String email
    ) {
        // Store minimal user info in the HTTP session (backed by Redis)
        session.setAttribute("userId", userId);
        session.setAttribute("username", username);
        session.setAttribute("email", email);

        Map<String, Object> body = Map.of(
                "sessionId", session.getId(),
                "userId", userId,
                "username", username,
                "email", email
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No active session"));
        }
        Map<String, Object> body = Map.of(
                "sessionId", session.getId(),
                "userId", session.getAttribute("userId"),
                "username", session.getAttribute("username"),
                "email", session.getAttribute("email")
        );
        return ResponseEntity.ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}

