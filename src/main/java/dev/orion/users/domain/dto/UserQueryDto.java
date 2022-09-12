package dev.orion.users.domain.dto;

import lombok.Getter;
import lombok.Setter;
import javax.ws.rs.QueryParam;

@Getter
@Setter
public class UserQueryDto {
    @QueryParam("hash")
    private String hash;

    @QueryParam("name")
    private String name;

    @QueryParam("email")
    private String email;
}
