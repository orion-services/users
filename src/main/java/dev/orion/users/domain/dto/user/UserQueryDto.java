package dev.orion.users.domain.dto.user;

import javax.ws.rs.QueryParam;

public class UserQueryDto {
    @QueryParam("hash")
    public String hash;

    @QueryParam("name")
    public String name;

    @QueryParam("email")
    public String email;

    public UserQueryDto() {
    }

    public UserQueryDto(String hash) {
        this.hash = hash;
    }
}
