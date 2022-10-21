package dev.orion.users.data.mappers;

import dev.orion.users.domain.dto.CreateUserDto;
import dev.orion.users.domain.models.User;

public class UserMapper {
    public static User toEntity(CreateUserDto createUser) {
        return new User(
                createUser.name,
                createUser.email,
                createUser.password);
    }

}
