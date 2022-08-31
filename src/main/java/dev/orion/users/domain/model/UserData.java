package dev.orion.users.domain.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

import io.smallrye.common.constraint.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserData {
    @FormParam("name")
    @NotEmpty(message = "Name must not be empty!")
    @NotNull()
    public String name;

    @FormParam("email")
    @Email(message = "Invalid E-mail!")
    @NotEmpty(message = "E-mail must not be empty!")
    public String email;

    @FormParam("password")
    @NotEmpty(message = "Password must not be empty!")
    @Size(min = 8)
    public String password;
}
