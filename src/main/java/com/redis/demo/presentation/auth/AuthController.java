package com.redis.demo.presentation.auth;

import com.redis.demo.domain.auth.AuthService;
import com.redis.demo.domain.user.User;
import com.redis.demo.presentation.auth.dto.AuthResponse;
import com.redis.demo.presentation.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req, HttpSession session) {
        User user = authService.authenticate(req.email, req.password);
        // Minimal info in session
        session.setAttribute("userId", user.getId());
        session.setAttribute("email", user.getEmailId());
        session.setAttribute("firstName", user.getFirstName());
        session.setAttribute("lastName", user.getLastName());

        AuthResponse resp = new AuthResponse();
        resp.sessionId = session.getId();
        resp.userId = user.getId();
        resp.email = user.getEmailId();
        resp.firstName = user.getFirstName();
        resp.lastName = user.getLastName();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No active session"));
        }
        return ResponseEntity.ok(Map.of(
                "sessionId", session.getId(),
                "userId", userId,
                "email", session.getAttribute("email"),
                "firstName", session.getAttribute("firstName"),
                "lastName", session.getAttribute("lastName")
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}

