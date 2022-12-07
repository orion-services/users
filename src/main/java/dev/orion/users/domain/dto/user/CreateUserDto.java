package dev.orion.users.domain.dto.user;

import dev.orion.users.domain.models.RoleEnum;

import java.util.List;
import java.util.Set;

public class CreateUserDto {
    public String name;
    public String email;
    public String password;
    public String type;
    public Set<RoleEnum> roles;


    public CreateUserDto() {
    }

    public CreateUserDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public CreateUserDto(String name, String email, String password, Set<RoleEnum> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public CreateUserDto(String name, String email, String password, String type, Set<RoleEnum> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.roles = roles;
    }
}
