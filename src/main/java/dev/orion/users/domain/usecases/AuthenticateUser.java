package dev.orion.users.domain.usecases;

import dev.orion.users.domain.dto.AuthenticateUserDto;
import dev.orion.users.domain.models.User;

public interface AuthenticateUser {
    User authenticate(AuthenticateUserDto userDto);
}
