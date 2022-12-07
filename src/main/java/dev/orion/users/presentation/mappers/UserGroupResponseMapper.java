package dev.orion.users.presentation.mappers;

import dev.orion.users.domain.models.UserGroup;
import dev.orion.users.presentation.dto.ResponseUserDto;
import dev.orion.users.presentation.dto.ResponseUserGroupDto;

import java.util.List;

public class UserGroupResponseMapper {
    public static ResponseUserGroupDto toResponse(UserGroup userGroup, List<ResponseUserDto> users){
        ResponseUserGroupDto response = new ResponseUserGroupDto();
        response.hash = userGroup.getUserGroupHash();
        response.name = userGroup.getName();
        response.users = users;
        return response;
    }
}
