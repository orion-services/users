package dev.orion.users.data.usecases.userGroup;

import dev.orion.users.data.interfaces.UserGroupRepository;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.data.mappers.UserGroupMapper;
import dev.orion.users.domain.dto.userGroup.CreateUserGroupDto;
import dev.orion.users.domain.models.UserGroup;
import dev.orion.users.domain.usecases.userGroup.CreateUserGroup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.val;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CreateUserGroupImpl implements CreateUserGroup {
    @Inject
    UserGroupRepository userGroupRepository;

    @Inject
    UserRepository userRepository;
    @Override
    public UserGroup create(CreateUserGroupDto createGroupDto) {
        return userGroupRepository.create(UserGroupMapper.toEntity(createGroupDto));
    }
}
