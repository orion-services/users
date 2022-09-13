package dev.orion.users.domain.dto;

public class CreateUserDto {
    public String name;
    public String email;
    public String password;

    public CreateUserDto() {
    }

    public CreateUserDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
