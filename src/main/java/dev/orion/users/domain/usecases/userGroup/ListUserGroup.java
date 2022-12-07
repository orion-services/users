package dev.orion.users.domain.usecases.userGroup;

import dev.orion.users.domain.dto.userGroup.UserGroupQueryDto;
import dev.orion.users.domain.models.UserGroup;

import java.util.List;

public interface ListUserGroup {
    List<UserGroup> find (UserGroupQueryDto userGroupQueryDto);
    UserGroup findById (String id);
}
