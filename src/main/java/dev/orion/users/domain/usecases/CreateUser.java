package dev.orion.users.domain.usecases;

import dev.orion.users.domain.dto.CreateUserDto;
import dev.orion.users.domain.models.User;

public interface CreateUser {
    User create(CreateUserDto user);
}
