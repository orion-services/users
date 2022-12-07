package dev.orion.users.domain.usecases.user;

import dev.orion.users.domain.dto.user.AuthenticateUserDto;
import dev.orion.users.domain.models.User;

public interface AuthenticateUser {
    User authenticate(AuthenticateUserDto userDto);
}
