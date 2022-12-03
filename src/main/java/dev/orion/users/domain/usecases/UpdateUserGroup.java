package dev.orion.users.domain.usecases;

import dev.orion.users.domain.dto.UpdateUserGroupDto;
import dev.orion.users.domain.models.UserGroup;

public interface UpdateUserGroup {
    UserGroup update(UpdateUserGroupDto updateUserGroupDto);
}
