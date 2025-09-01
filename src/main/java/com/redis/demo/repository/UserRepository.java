package com.redis.demo.repository;

import com.redis.demo.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmailId(String emailId);
    Optional<User> findByEmailId(String emailId);
}

