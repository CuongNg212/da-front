package jwd.practice.userservice.mapper;


import jwd.practice.userservice.dto.request.UserCreateRequest;
import jwd.practice.userservice.dto.request.UserUpdateRequest;
import jwd.practice.userservice.dto.response.UserResponse;
import jwd.practice.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IUserMapper {
    User toUser(UserCreateRequest userCreateRequest);
    UserResponse toUserResponse(User user);
    @Mapping(source = "address", target = "user.address")
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
}
