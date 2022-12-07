package dev.orion.users.data.usecases.user;

import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.user.UpdateUser;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UpdateUserImpl implements UpdateUser {
    @Override
    public User update(UpdateUser updateUserDto) {
        return null;
    }
}
