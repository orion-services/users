package dev.orion.users.data.usecases;

import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.UpdateUser;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UpdateUserImpl implements UpdateUser {
    @Override
    public User update(UpdateUser updateUserDto) {
        return null;
    }
}
