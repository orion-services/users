/**
 * @License
 * Copyright 2022 Orion Services @ https://github.com/orion-services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.orion.users.presentation.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.data.usecases.*;
import dev.orion.users.domain.dto.AuthenticateUserDto;
import dev.orion.users.domain.dto.UserQueryDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.*;
import dev.orion.users.infra.repositories.UserPanacheRepository;
import dev.orion.users.presentation.dto.ResponseUserDto;
import dev.orion.users.presentation.mappers.ResponseMapper;
import dev.orion.users.validation.dto.Authentication;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;

import dev.orion.users.domain.dto.CreateUserDto;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

/**
 * User API.
 */
@Path("/api/user")
public class Service {

        /* Configure the issuer for JWT generation. */
        @ConfigProperty(name = "user.issuer")
        public Optional<String> issuer;

        private UserRepository repository = new UserPanacheRepository();

        private AuthenticateUser authUser = new AuthenticateUserImpl(repository);

        private CreateUser createUser = new CreateUserImpl(repository);

        private ListUser listUser = new ListUserImpl(repository);

        private RemoveUser removeUser = new RemoveUserImpl(repository);

        private BlockUser blockUser = new BlockUserImpl(repository);

        @GET
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Transactional
        public List<ResponseUserDto> find(@BeanParam UserQueryDto query) {
                try {
                        List<User> users = listUser.list(query);

                        return users.stream().map(user -> ResponseMapper.toResponse(user)).collect(Collectors.toList());
                } catch (Exception e) {
                        throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
                }

        }

        @DELETE
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Transactional
        public Response remove(@FormParam("hash") @NotEmpty final String hash) {
                try {
                        return Response.status(Response.Status.ACCEPTED)
                                        .entity(removeUser.removeUser(hash))
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
        public Response block(@FormParam("hash") @NotEmpty final String hash) {
                try {
                        return Response.status(Response.Status.ACCEPTED)
                                        .entity(ResponseMapper.toResponse(blockUser.block(hash)))
                                        .build();
                } catch (Exception e) {
                        throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
                }
        }

        /**
         * Creates a user inside the service.
         *
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
                                        .entity(ResponseMapper.toResponse(createUser.create(createUserDto)))
                                        .build();
                } catch (Exception e) {
                        throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
                }
        }

        /**
         * Creates a user and authenticate.
         *
         *
         * @return The Authentication DTO
         * @throws ServiceException Returns a HTTP 409 if the e-mail already
         *                          exists in the database or if the password is lower
         *                          than eight
         *                          characters
         */
        @POST
        @Path("/createAuthenticate")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = 2000)
        @Transactional
        public Authentication createAuthenticate(@RequestBody CreateUserDto createUserDto) {

                try {
                        User user = createUser.create(createUserDto);
                        String token = this.generateJWT(user);
                        Authentication auth = new Authentication();
                        auth.setToken(token);
                        auth.setUser(user);
                        return auth;
                } catch (Exception e) {
                        String message = e.getMessage();
                        throw new ServiceException(
                                        message,
                                        Response.Status.BAD_REQUEST);
                }
        }

        /**
         * Authenticates the user.
         *
         * @param email    : The e-mail of the user
         * @param password : The password of the user
         *
         * @return A JWT (JSON Web Token)
         * @throws ServiceException Returns an HTTP 401 if the services is not
         *                          able to find the user in the database
         */
        @POST
        @Path("/authenticate")
        @PermitAll
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = 2000)
        @Transactional
        public String authenticate(@RequestBody AuthenticateUserDto authDto) {
                try {
                        User user = authUser.authenticate(authDto);
                        if (user == null) {
                                throw new ServiceException("User not found",
                                                Response.Status.UNAUTHORIZED);
                        }
                        return this.generateJWT(user);
                } catch (Exception e) {
                        throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
                }

        }

        /**
         * Creates a JWT (JSON Web Token) to a user.
         *
         * @param user : The user object
         *
         * @return Returns the JWT
         */
        private String generateJWT(final User user) {
                return Jwt.issuer(issuer.orElse("http://localhost:8080"))
                                .upn(user.getEmail().getAddress())
                                .groups(new HashSet<>(Arrays.asList("user")))
                                .claim(Claims.c_hash, user.getHash())
                                .sign();
        }

}
