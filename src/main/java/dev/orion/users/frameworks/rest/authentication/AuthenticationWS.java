/**
 * @License
 * Copyright 2023 Orion Services @ https://github.com/orion-services
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
package dev.orion.users.frameworks.rest.authentication;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * User API.
 */
@PermitAll
@Path("/api/users")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationWS {

    /** Fault tolerance default delay. */
    // protected static final long DELAY = 2000;

    // /** Business logic. */
    // @Inject
    // protected AuthenticationHandler authHandler;

    // @Inject
    // protected AuthenticateUser authenticateUserUseCase;

    // @Inject
    // protected CreateUser createUserUseCase;

    /**
     * Authenticates the user.
     *
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return A JWT (JSON Web Token)
     * @throws ServiceException Returns a HTTP 401 if the services is not able to
     * find the user in the database
     */
    // @POST
    // @Path("/authenticate")
    // @Produces(MediaType.TEXT_PLAIN)
    // @Retry(maxRetries = 1, delay = DELAY)
    // @WithSession
    // public Uni<String> authenticate(
    //         @RestForm @NotEmpty @Email final String email,
    //         @RestForm @NotEmpty final String password) {

    //     return authenticateUserUseCase.authenticate(email, password)
    //             .onItem().ifNotNull()
    //                 .transform(user -> authHandler.generateJWT(user))
    //             .onItem().ifNull()
    //                 .failWith(new ServiceException("User not found",
    //                     Response.Status.UNAUTHORIZED));
    // }

    /**
     * Creates a user and authenticate.
     *
     * @param name     : The name of the user
     * @param email    : The email of the user
     * @param password : The password of the user
     * @return The Authentication DTO
     * @throws ServiceException Returns a HTTP 409 if the e-mail already exists
     * in the database or if the password is lower than eight characters
     */
    // @POST
    // @Path("/createAuthenticate")
    // @Retry(maxRetries = 1, delay = DELAY)
    // @WithSession
    // public Uni<AuthenticationDTO> createAuthenticate(
    //         @FormParam("name") @NotEmpty final String name,
    //         @FormParam("email") @NotEmpty @Email final String email,
    //         @FormParam("password") @NotEmpty final String password) {

    //     try {
    //         return createUserUseCase.createUser(name, email, password)
    //             .onItem().ifNotNull()
    //                 .transform(user -> {
    //                     String token = authHandler.generateJWT(user);
    //                     AuthenticationDTO auth = new AuthenticationDTO();
    //                     auth.setToken(token);
    //                     auth.setUser(user);
    //                     return auth;
    //                 })
    //             .log();
    //     } catch (Exception e) {
    //         throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
    //     }
    // }
}
