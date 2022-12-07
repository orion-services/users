package dev.orion.users.domain.dto.userGroup;

import javax.ws.rs.QueryParam;

public class UserGroupQueryDto {
    @QueryParam("hash")
    public String hash;

    @QueryParam("name")
    public String name;


}
