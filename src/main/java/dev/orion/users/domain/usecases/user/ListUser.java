package dev.orion.users.domain.usecases.user;

import dev.orion.users.domain.dto.user.UserQueryDto;
import dev.orion.users.domain.models.User;

import java.util.List;

public interface ListUser {
    List<User> list(UserQueryDto params);
}
