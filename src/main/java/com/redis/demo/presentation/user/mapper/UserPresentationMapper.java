package com.redis.demo.presentation.user.mapper;

import com.redis.demo.domain.user.CreateUserCommand;
import com.redis.demo.domain.user.User;
import com.redis.demo.presentation.user.dto.UserRequest;
import com.redis.demo.presentation.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPresentationMapper {
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "password", source = "password")
    CreateUserCommand toCommand(UserRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "emailId")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    UserResponse toResponse(User domain);
}

