package dev.orion.users.data.usecases.userGroup;

import dev.orion.users.domain.dto.userGroup.UpdateUserGroupDto;
import dev.orion.users.domain.models.UserGroup;
import dev.orion.users.domain.usecases.userGroup.UpdateUserGroup;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UpdateUserGroupImpl implements UpdateUserGroup {

    @Override
    public UserGroup update(String hash, UpdateUserGroupDto updateUserGroupDto) {
        return null;
    }
}
