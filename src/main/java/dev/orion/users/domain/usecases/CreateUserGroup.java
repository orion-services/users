package dev.orion.users.domain.usecases;

import dev.orion.users.domain.dto.CreateUserGroupDto;
import dev.orion.users.domain.models.UserGroup;

public interface CreateUserGroup {
    UserGroup create(CreateUserGroupDto createGroupDto);
}
