package dev.orion.users.data.interfaces;

import dev.orion.users.domain.dto.userGroup.UserGroupQueryDto;
import dev.orion.users.domain.models.UserGroup;

import java.util.List;

public interface UserGroupRepository {
    UserGroup create(UserGroup userGroup);

    List<UserGroup> find(UserGroupQueryDto userGroupQueryDto);
    UserGroup findById(String id);

    Boolean delete(String id);

    UserGroup update(UserGroup userGroup);
}
