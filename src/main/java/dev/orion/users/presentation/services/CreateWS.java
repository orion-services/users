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
import jakarta.annotation.security.PermitAll;
import org.eclipse.microprofile.faulttolerance.Retry;

import dev.orion.users.data.usecases.UserUC;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.usecases.UseCase;
import dev.orion.users.presentation.exceptions.UserWSException;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;

@Path("/api/users")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class CreateWS extends BaseWS {

    /** Business logic. */
    private UseCase uc = new UserUC();

    /**
     * Creates a user inside the service.
     *
     * @param name     : The name of the user
     * @param email    : The email of the user
     * @param password : The password of the user
     * @return The user object in JSON format
     * @throws UserWSException Returns a HTTP 409 if the e-mail already exists
     *                         in the database or if the password is lower than
     *                         eight characters
     */
    @POST
    @Path("/create")
    @PermitAll
    @Retry(maxRetries = 1, delay = DELAY)
    @WithSession
    public Uni<User> create(
            @FormParam("name") @NotEmpty final String name,
            @FormParam("email") @NotEmpty @Email final String email,
            @FormParam("password") @NotEmpty final String password) {

        try {
            return uc.createUser(name, email, password)
                    .log()
                    .onItem().ifNotNull()
                    .call(this::sendValidationEmail)
                    .onFailure().transform(e -> {
                        throw new UserWSException(e.getMessage(),
                                Response.Status.BAD_REQUEST);
                    });
        } catch (Exception e) {
            throw new UserWSException(e.getMessage(),
                    Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Validates e-mail, this method is used to confirm the user's e-mail using
     * a code.
     *
     * @param email : The e-mail of the user
     * @param code  : The code sent to the user
     * @return true if was possible to validate the e-mail and HTTP 400
     *         (bad request) if the the em-mail or code is invalid.
     */
    @GET
    @PermitAll
    @Path("/validateEmail")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @WithSession
    public Uni<Boolean> validateEmail(
            @QueryParam("email") @NotEmpty final String email,
            @QueryParam("code") @NotEmpty final String code) {

        return uc.validateEmail(email, code)
                .onFailure().transform(e -> {
                    throw new UserWSException(e.getMessage(),
                            Response.Status.BAD_REQUEST);
                })
                .onItem().ifNotNull().transform(user -> true);
    }

}
