package dev.orion.users.domain.usecases;

import dev.orion.users.domain.dto.UserQueryDto;
import dev.orion.users.domain.models.User;

import java.util.List;

public interface ListUser {
    List<User> list(UserQueryDto params);
}
