package com.redis.demo.infrastructure.persistence.repository;

import com.redis.demo.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    boolean existsByEmailId(String emailId);
    Optional<UserEntity> findByEmailId(String emailId);
}

