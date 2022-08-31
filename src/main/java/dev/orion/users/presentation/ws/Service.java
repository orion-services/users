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
package dev.orion.users.presentation.ws;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dev.orion.users.usecase.AuthenticateUser;
import dev.orion.users.usecase.CreateUser;
import dev.orion.users.usecase.ListUser;
import dev.orion.users.usecase.RemoveUser;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.resteasy.reactive.RestForm;

import dev.orion.users.validation.dto.Authentication;
import dev.orion.users.validation.dto.UserQuery;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.model.UserData;
import dev.orion.users.usecase.UseCase;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

/**
 * User API.
 */
@Path("/api/user")
public class Service {

        /* Configure the issuer for JWT generation. */
        @ConfigProperty(name = "user.issuer")
        public Optional<String> issuer;

        /** Business logic of the system. */

        private UseCase authUser = new AuthenticateUser();

        private UseCase createUser = new CreateUser();

        private UseCase listUser = new ListUser();

        private UseCase removeUser = new RemoveUser();

        @GET
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        public Multi<User> find(@BeanParam UserQuery query) {

                return listUser.listUser(query);
        }

        @DELETE
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        public Uni<Long> remove(@FormParam("hash") @NotEmpty final String hash) {
                return removeUser.removeUser(hash);
        }

        /**
         * Creates a user inside the service.
         *
         * @param name     : The name of the user
         * @param email    : The email of the user
         * @param password : The password of the user
         *
         * @return The user object in JSON format
         * @throws ServiceException Returns a HTTP 409 if the e-mail already
         *                          exists in the database or if the password is lower
         *                          than eight
         *                          characters
         */
        @POST
        @Path("/create")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<User> create(@BeanParam UserData userData) {

                try {
                        return createUser.createUser(userData)
                                        .onItem().ifNotNull().transform(user -> user)
                                        .log()
                                        .onFailure().transform(e -> {
                                                String message = e.getMessage();
                                                throw new ServiceException(
                                                                message,
                                                                Response.Status.BAD_REQUEST);
                                        });
                } catch (Exception e) {
                        String message = e.getMessage();
                        throw new ServiceException(
                                        message,
                                        Response.Status.BAD_REQUEST);
                }
        }

        /**
         * Creates a user and authenticate.
         *
         * @param name     : The name of the user
         * @param email    : The email of the user
         * @param password : The password of the user
         *
         * @return The Authentication DTO
         * @throws ServiceException Returns a HTTP 409 if the e-mail already
         *                          exists in the database or if the password is lower
         *                          than eight
         *                          characters
         */
        @POST
        @Path("/createAuthenticate")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<Authentication> createAuthenticate(@BeanParam UserData userData) {

                try {
                        return createUser.createUser(userData)
                                        .onItem().ifNotNull().transform(user -> {
                                                String token = generateJWT(user);
                                                Authentication auth = new Authentication();
                                                auth.setToken(token);
                                                auth.setUser(user);
                                                return auth;
                                        })
                                        .log();
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
         * @throws ServiceException Returns a HTTP 401 if the services is not
         *                          able to find the user in the database
         */
        @POST
        @Path("/authenticate")
        @PermitAll
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.TEXT_PLAIN)
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<String> authenticate(
                        @RestForm @NotEmpty @Email final String email,
                        @RestForm @NotEmpty final String password) {

                return authUser.authenticate(email, password)
                                .onItem()
                                .ifNotNull()
                                .transform(this::generateJWT)
                                .onItem()
                                .ifNull()
                                .failWith(new ServiceException("User not found",
                                                Response.Status.UNAUTHORIZED));
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
                                .upn(user.getEmail())
                                .groups(new HashSet<>(Arrays.asList("user")))
                                .claim(Claims.c_hash, user.getHash())
                                .sign();
        }

}
