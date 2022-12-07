package dev.orion.users.domain.usecases.user;

import dev.orion.users.domain.dto.user.CreateUserDto;
import dev.orion.users.domain.models.User;

public interface CreateUser {
    User create(CreateUserDto user);
}
