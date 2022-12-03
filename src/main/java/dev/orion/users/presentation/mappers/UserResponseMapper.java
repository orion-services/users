package dev.orion.users.presentation.mappers;

import dev.orion.users.domain.models.User;
import dev.orion.users.presentation.dto.ResponseUserDto;

public class UserResponseMapper {

    public static ResponseUserDto toResponse(User user) {
        ResponseUserDto response = new ResponseUserDto();
        response.hash = user.getUserHash();
        response.name = user.getName();
        response.email = user.getEmail().getAddress();
        response.status = user.getStatus();
        return response;
    }

}
