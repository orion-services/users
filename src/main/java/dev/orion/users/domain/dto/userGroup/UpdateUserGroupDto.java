package dev.orion.users.domain.dto.userGroup;

import dev.orion.users.domain.models.User;

import java.util.List;

public class UpdateUserGroupDto {
    public String name;
    public List<User> users;
}
