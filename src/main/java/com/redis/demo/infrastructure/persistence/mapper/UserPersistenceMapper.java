package com.redis.demo.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    // Use fully-qualified names to avoid simple-name clashes
    com.redis.demo.infrastructure.persistence.entity.UserEntity toEntity(com.redis.demo.domain.user.User domain);
    com.redis.demo.domain.user.User toDomain(com.redis.demo.infrastructure.persistence.entity.UserEntity entity);
}
