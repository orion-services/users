package dev.orion.users.data.mappers;

import dev.orion.users.domain.dto.userGroup.CreateUserGroupDto;
import dev.orion.users.domain.models.UserGroup;

public class UserGroupMapper {
    public static UserGroup toEntity(CreateUserGroupDto createUserGroupDto){
        return new UserGroup(
                createUserGroupDto.name,
                createUserGroupDto.users);
    }
}
