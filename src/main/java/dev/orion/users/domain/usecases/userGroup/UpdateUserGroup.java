package dev.orion.users.domain.usecases.userGroup;

import dev.orion.users.domain.dto.userGroup.UpdateUserGroupDto;
import dev.orion.users.domain.models.UserGroup;

public interface UpdateUserGroup {
    UserGroup update(String hash ,UpdateUserGroupDto updateUserGroupDto);
}
