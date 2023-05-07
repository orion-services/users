package dev.orion.users.dto;

import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryDto {
    @QueryParam("hash")
    public String hash;

    @QueryParam("name")
    public String name;

    @QueryParam("email")
    public String email;

    @QueryParam("status")
    public String status;
}
