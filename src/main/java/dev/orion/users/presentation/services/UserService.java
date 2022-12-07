/**
 * @License
 * Copyright 2022 Orion Services @ https://github.com/orion-services
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.orion.users.presentation.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dev.orion.users.domain.dto.user.UpdateUserDto;
import dev.orion.users.domain.dto.user.UserQueryDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.user.*;
import dev.orion.users.presentation.mappers.UserResponseMapper;

import io.quarkus.vertx.web.Body;
import org.eclipse.microprofile.faulttolerance.Retry;

import dev.orion.users.domain.dto.user.CreateUserDto;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

/**
 * User API.
 */
@Path("/api/user")
public class UserService {



        @Inject
        protected CreateUser createUser;

        @Inject
        protected ListUser listUser;

        @Inject
        protected RemoveUser removeUser;

        @Inject
        protected BlockUser blockUser;


        @GET
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Transactional
        public Response find(@BeanParam UserQueryDto query) {
                try {
                        List<User> users = listUser.list(query);
                        return Response
                                .status(Response.Status.OK)
                                .entity(users.stream().map(UserResponseMapper::toResponse).collect(Collectors.toList()))
                                .build();
                } catch (Exception e) {
                        throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
                }

        }

        /**
         * Creates a user inside the service.
         *
         * @return The user object in JSON format
         * @throws ServiceException Returns an HTTP 409 if the e-mail already
         *                          exists in the database or if the password is lower
         *                          than eight
         *                          characters
         */
        @POST
        @Path("/create")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = 2000)
        @Transactional
        public Response create(@RequestBody CreateUserDto createUserDto) {
                try {
                        return Response.status(Response.Status.CREATED)
                                        .entity(UserResponseMapper.toResponse(createUser.create(createUserDto)))
                                        .build();
                } catch (Exception e) {
                        throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
                }
        }

        @PUT
        @Path("/block")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Transactional
//        @RolesAllowed("ADMIN")
        public Response block(@FormParam("hash") @NotEmpty final String hash) {
                try {
                        return Response.status(Response.Status.ACCEPTED)
                                        .entity(UserResponseMapper.toResponse(blockUser.block(hash)))
                                        .build();
                } catch (Exception e) {
                        throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
                }
        }
        @PUT
        @Path("/update/{id}")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Transactional
//        @RolesAllowed("ADMIN")
        public Response update(@PathParam("id") @NotEmpty final String hash, @RequestBody UpdateUserDto updateUserDto) {
                try {
                        return Response.status(Response.Status.ACCEPTED)
                                .entity(UserResponseMapper.toResponse(blockUser.block(hash)))
                                .build();
                } catch (Exception e) {
                        throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
                }
        }

        @DELETE
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Transactional
//        @RolesAllowed("ADMIN")
        public Response remove(@FormParam("hash") @NotEmpty final String hash) {
                try {
                        return Response.status(Response.Status.ACCEPTED)
                                        .entity(removeUser.removeUser(hash))
                                        .build();
                } catch (Exception e) {
                        throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
                }

        }



}
