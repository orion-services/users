/**
 * @License
 * Copyright 2024 Orion Services @ https://github.com/orion-services
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

import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.resteasy.reactive.RestForm;

import dev.orion.users.adapters.controllers.UserController;
import dev.orion.users.frameworks.rest.ServiceException;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * User API.
 */
@PermitAll
@Path("/users")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@WithSession
public class AuthenticationWS {

    /** Fault tolerance default delay. */
    protected static final long DELAY = 2000;

    /** Business logic of the system. */
    @Inject
    private UserController controller;

    /**
     * Authenticates the user.
     *
     * @param email    The e-mail of the user
     * @param password The password of the user
     * @return A JWT (JSON Web Token)
     * @throws A Bad Request if the user is not found
     */
    @POST
    @Path("/authenticate")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Retry(maxRetries = 1, delay = DELAY)
    @Deprecated(since = "1.0.0", forRemoval = true)
    public Uni<String> authenticate(
            @RestForm @NotEmpty @Email final String email,
            @RestForm @NotEmpty final String password) {

        return controller.authenticate(email, password)
            .onItem().ifNotNull().transform(jwt -> jwt)
            .onItem().ifNull()
            .failWith(new ServiceException("User not found",
                    Response.Status.UNAUTHORIZED));
    }

    /**
     * Creates and authenticates a user.
     *
     * @param name     The name of the user
     * @param email    The email of the user
     * @param password The password of the user
     * @return The Authentication DTO
     * @throws A Bad Request if the service is unable to create the user
     */
    @POST
    @Path("/createAuthenticate")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = DELAY)
    public Uni<Response> createAuthenticate(
            @FormParam("name") @NotEmpty final String name,
            @FormParam("email") @NotEmpty @Email final String email,
            @FormParam("password") @NotEmpty final String password) {

        return controller.createAuthenticate(name, email, password)
            .log()
            .onItem().ifNotNull().transform(dto -> Response.ok(dto).build())
            .onFailure().transform(e -> {
                String message = e.getMessage();
                throw new ServiceException(message,
                        Response.Status.BAD_REQUEST);
            });
    }

    /**
     * Validates e-mail, this method is used to confirm the user's e-mail using
     * a code.
     *
     * @param email The e-mail of the user
     * @param code  The code sent to the user
     * @return true if was possible to validate the e-mail
     * @throws Bad request if the the em-mail or code is invalid
     */
    @GET
    @PermitAll
    @Path("/validateEmail")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @WithSession
    public Uni<Response> validateEmail(
            @QueryParam("email") @NotEmpty final String email,
            @QueryParam("code") @NotEmpty final String code) {

        return controller.validateEmail(email, code)
            .onItem().ifNotNull().transform(user ->
                Response.ok(true).build())
                .onItem().ifNull().continueWith(() -> {
                    String message = "Invalid e-mail or code";
                    throw new ServiceException(message,
                        Response.Status.BAD_REQUEST);
                })
                .onFailure().transform(e -> {
                    String message = e.getMessage();
                    throw new ServiceException(message,
                        Response.Status.BAD_REQUEST);
            });
    }
}
