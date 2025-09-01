package com.redis.demo.presentation.user;

import com.redis.demo.domain.user.CreateUserCommand;
import com.redis.demo.domain.user.User;
import com.redis.demo.domain.user.UserService;
import com.redis.demo.presentation.user.dto.UserRequest;
import com.redis.demo.presentation.user.dto.UserResponse;
import com.redis.demo.presentation.user.mapper.UserPresentationMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserPresentationMapper mapper;

    public UserController(UserService userService, UserPresentationMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest req) {
        CreateUserCommand cmd = mapper.toCommand(req);
        User saved = userService.createUser(cmd);
        UserResponse resp = mapper.toResponse(saved);
        return ResponseEntity.created(URI.create("/api/users/" + resp.id)).body(resp);
    }
}

