package dev.orion.users.domain.dto.user;

import dev.orion.users.domain.models.User;

import javax.management.relation.Role;
import java.util.Set;

public class UpdateUserDto {
    public String name;
    public String password;
    public Set<Role> roles;
}
