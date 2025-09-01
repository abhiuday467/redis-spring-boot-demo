package com.redis.demo.domain.user;

import java.util.Optional;

public interface UserRepositoryPort {
    boolean existsByEmailId(String emailId);
    Optional<User> findByEmailId(String emailId);
    User save(User user);
}

