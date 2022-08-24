package dev.orion.users.validation.dto;

import javax.ws.rs.QueryParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQuery {
    @QueryParam("userId")
    private String userId;

    @QueryParam("userName")
    private String userName;

}
