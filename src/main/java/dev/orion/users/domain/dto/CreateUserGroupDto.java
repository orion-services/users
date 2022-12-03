package dev.orion.users.domain.dto;

import dev.orion.users.domain.models.User;

import java.util.List;

public class CreateUserGroupDto {
    public String name;
    public List<User> users;
}
