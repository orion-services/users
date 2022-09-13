package dev.orion.users.domain.dto;

import javax.ws.rs.QueryParam;

public class UserQueryDto {
    @QueryParam("hash")
    public String hash;

    @QueryParam("name")
    public String name;

    @QueryParam("email")
    public String email;

}
