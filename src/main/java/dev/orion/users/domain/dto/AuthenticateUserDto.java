package dev.orion.users.domain.dto;

import javax.validation.constraints.NotBlank;

public class AuthenticateUserDto {
    @NotBlank
    public static String email;
    @NotBlank
    public static String password;

    public AuthenticateUserDto() {
    }

    public AuthenticateUserDto(String email, String password) {
        this();
        if (email != null && password != null && !email.isEmpty() && !password.isEmpty()) {
            this.email = email;
            this.password = password;
        } else {
            throw new IllegalArgumentException("All arguments are required");
        }

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
