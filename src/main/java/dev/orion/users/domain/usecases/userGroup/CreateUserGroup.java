package dev.orion.users.domain.usecases.userGroup;

import dev.orion.users.domain.dto.userGroup.CreateUserGroupDto;
import dev.orion.users.domain.models.UserGroup;

public interface CreateUserGroup {
    UserGroup create(CreateUserGroupDto createGroupDto);
}
