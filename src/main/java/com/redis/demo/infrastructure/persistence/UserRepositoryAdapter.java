package com.redis.demo.infrastructure.persistence;

import com.redis.demo.domain.user.User;
import com.redis.demo.domain.user.UserRepositoryPort;
import com.redis.demo.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.redis.demo.infrastructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserRepository repository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryAdapter(UserRepository repository, UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public boolean existsByEmailId(String emailId) {
        return repository.existsByEmailId(emailId);
    }

    @Override
    public Optional<User> findByEmailId(String emailId) {
        return repository.findByEmailId(emailId).map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
}
