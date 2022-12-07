package dev.orion.users.presentation.services;

import dev.orion.users.data.mappers.UserGroupMapper;
import dev.orion.users.domain.dto.user.CreateUserDto;
import dev.orion.users.domain.dto.user.UserQueryDto;
import dev.orion.users.domain.dto.userGroup.CreateUserGroupDto;
import dev.orion.users.domain.dto.userGroup.UpdateUserGroupDto;
import dev.orion.users.domain.dto.userGroup.UserGroupQueryDto;
import dev.orion.users.domain.models.RoleEnum;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.models.UserGroup;
import dev.orion.users.domain.usecases.user.ListUser;
import dev.orion.users.domain.usecases.userGroup.CreateUserGroup;
import dev.orion.users.domain.usecases.userGroup.DeleteUserGroup;
import dev.orion.users.domain.usecases.userGroup.ListUserGroup;
import dev.orion.users.domain.usecases.userGroup.UpdateUserGroup;
import dev.orion.users.presentation.dto.ResponseUserDto;
import dev.orion.users.presentation.dto.ResponseUserGroupDto;
import dev.orion.users.presentation.mappers.UserGroupResponseMapper;
import dev.orion.users.presentation.mappers.UserResponseMapper;
import io.quarkus.security.jpa.Roles;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/userGroup")
public class UserGroupService {

    @Inject
    protected ListUserGroup listUserGroup;

    @Inject
    protected ListUser listUser;
    @Inject
    protected CreateUserGroup createUserGroup;

    @Inject
    protected UpdateUserGroup updateUserGroup;

    @Inject
    protected DeleteUserGroup deleteUserGroup;

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response find(@BeanParam UserGroupQueryDto query){
        try{
            List<UserGroup> userGroups = listUserGroup.find(query);
            return Response
                    .status(Response.Status.OK)
                    .entity(
                            userGroups.stream().map(userGroup -> {
                                List<ResponseUserDto> users = userGroup
                                        .getUsers()
                                        .stream()
                                        .map(user-> {
                                            User userSearched = listUser.list(new UserQueryDto(user)).get(0);
                                            return UserResponseMapper.toResponse(userSearched);
                                        }).collect(Collectors.toList());
                                return UserGroupResponseMapper.toResponse(userGroup,users);
                            }).collect(Collectors.toList())
                    )
                    .build();
        }catch (Exception e){
            throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    @Transactional
    public Response create(@RequestBody CreateUserGroupDto createUserGroupDto) {
        try{
            UserGroup userGroup = createUserGroup.create(createUserGroupDto);
            return Response
                    .status(Response.Status.OK)
                    .entity(userGroup)
                    .build();
        }catch (Exception e){
            throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    @Transactional
    public Response update(@PathParam("id")@NotEmpty final String hash, @RequestBody UpdateUserGroupDto updateUserGroupDto) {
        try{
            return Response
                    .status(Response.Status.OK)
                    .entity(updateUserGroup.update(hash,updateUserGroupDto))
                    .build();
        }catch (Exception e){
            throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    @Transactional
    public Response delete(@FormParam("hash") @NotEmpty final String hash) {
        try{
            return Response
                    .status(Response.Status.OK)
                    .entity(deleteUserGroup.delete(hash))
                    .build();
        }catch (Exception e){
            throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

}
