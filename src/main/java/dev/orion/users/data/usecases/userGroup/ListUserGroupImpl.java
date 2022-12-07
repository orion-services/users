package dev.orion.users.data.usecases.userGroup;

import dev.orion.users.data.interfaces.UserGroupRepository;
import dev.orion.users.domain.dto.userGroup.UserGroupQueryDto;
import dev.orion.users.domain.models.UserGroup;
import dev.orion.users.domain.usecases.userGroup.ListUserGroup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ListUserGroupImpl implements ListUserGroup {
    @Inject
    UserGroupRepository userGroupRepository;


    @Override
    public List<UserGroup> find(UserGroupQueryDto userGroupQueryDto) {

        return userGroupRepository.find(userGroupQueryDto);
    }

    @Override
    public UserGroup findById(String id) {
        return userGroupRepository.findById(id);
    }
}
