package dev.orion.users.data.usecases.userGroup;

import dev.orion.users.data.interfaces.UserGroupRepository;
import dev.orion.users.domain.usecases.userGroup.DeleteUserGroup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DeleteUserGroupImpl implements DeleteUserGroup {

    @Inject
    UserGroupRepository userGroupRepository;


    @Override
    public boolean delete(String id) {
        return userGroupRepository.delete(id);
    }
}
