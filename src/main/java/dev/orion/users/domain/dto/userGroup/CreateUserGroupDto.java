package dev.orion.users.domain.dto.userGroup;

import dev.orion.users.domain.models.User;

import java.util.List;
import java.util.Set;

public class CreateUserGroupDto {
    public String name;
    public Set<String> users;
}
