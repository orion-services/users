package dev.orion.users.domain.usecases;

import dev.orion.users.domain.dto.UserQueryDto;
import dev.orion.users.domain.models.User;

import java.util.List;
import java.util.Map;

public interface ListUser {
    List<User> list(UserQueryDto params);
}
