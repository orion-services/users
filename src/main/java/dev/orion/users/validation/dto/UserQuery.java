package dev.orion.users.validation.dto;

import javax.ws.rs.QueryParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQuery {
    @QueryParam("userHash")
    private String hash;

    @QueryParam("userName")
    private String name;

}
