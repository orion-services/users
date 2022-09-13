package dev.orion.users.data.mappers;

import org.apache.commons.codec.digest.DigestUtils;

import dev.orion.users.domain.dto.CreateUserDto;
import dev.orion.users.domain.models.User;

public class UserMapper {
    public static User toEntity(CreateUserDto createUser) {
        User user = new User(
                createUser.name,
                createUser.email,
                createUser.password);
        return user;
    }

}
