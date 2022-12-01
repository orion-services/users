package dev.orion.users.domain.usecases;
import dev.orion.users.domain.models.User;

public interface UpdateUser {
    User update(UpdateUser updateUserDto);
}
