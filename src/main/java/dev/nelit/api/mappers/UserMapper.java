package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.user.User;
import dev.nelit.api.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "createdAt", target = "createdAt")
    UserResponse toResponse(User user);
}