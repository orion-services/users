package dev.orion.users.data.interfaces;

import dev.orion.users.domain.dto.user.AuthenticateUserDto;
import dev.orion.users.domain.dto.user.UserQueryDto;
import dev.orion.users.domain.models.User;

import java.util.List;

public interface UserRepository {

    User create(User user);

    User authenticate(AuthenticateUserDto authDto);

    List<User> findByQuery(UserQueryDto query);

    User findByEmail(String email);

    Boolean removeUser(String id);

    User blockUser(String id);

    User update(User user);
}
