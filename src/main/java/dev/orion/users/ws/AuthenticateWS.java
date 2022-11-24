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
package dev.orion.users.ws;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.resteasy.reactive.RestForm;

import dev.orion.users.dto.Authentication;
import dev.orion.users.model.User;
import dev.orion.users.usecase.UseCase;
import dev.orion.users.usecase.UserUC;
import dev.orion.users.ws.expections.UserWSException;
import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;

/**
 * User API.
 */
@Path("/api/user")
@PermitAll
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticateWS extends BaseWS {

        @Inject
        Mailer mailer;

        @Inject
        ReactiveMailer reactiveMailer;

        /** Business logic. */
        private UseCase uc = new UserUC();

        /**
         * Authenticates the user.
         *
         * @param email    : The e-mail of the user
         * @param password : The password of the user
         * @return A JWT (JSON Web Token)
         * @throws UserWSException Returns a HTTP 401 if the services is not
         * able to find the user in the database
         */
        @POST
        @Path("/authenticate")
        @Produces(MediaType.TEXT_PLAIN)
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<String> authenticate(
                        @RestForm @NotEmpty @Email final String email,
                        @RestForm @NotEmpty final String password) {

                return uc.authenticate(email, password)
                        .onItem().ifNotNull()
                        .transform(super::generateJWT)
                        .onItem().ifNull()
                        .failWith(new UserWSException("User not found",
                                        Response.Status.UNAUTHORIZED));
        }

        /**
         * Creates a user inside the service.
         *
         * @param name     : The name of the user
         * @param email    : The email of the user
         * @param password : The password of the user
         * @return The user object in JSON format
         * @throws UserWSException Returns a HTTP 409 if the e-mail already
         * exists in the database or if the password is lower than eight
         * characters
         */
        @POST
        @Path("/create")
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<User> create(
                        @FormParam("name") @NotEmpty final String name,
                        @FormParam("email") @NotEmpty @Email final String email,
                        @FormParam("password") @NotEmpty final String password) {

                try {
                        return uc.createUser(name, email, password)
                                .log()
                                .onItem().ifNotNull().transform(user -> user)
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
         * Creates a user and authenticate.
         *
         * @param name     : The name of the user
         * @param email    : The email of the user
         * @param password : The password of the user
         * @return The Authentication DTO
         * @throws UserWSException Returns a HTTP 409 if the e-mail already
         * exists in the database or if the password is lower than eight
         * characters
         */
        @POST
        @Path("/createAuthenticate")
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<Authentication> createAuthenticate(
                @FormParam("name") @NotEmpty final String name,
                @FormParam("email") @NotEmpty @Email final String email,
                @FormParam("password") @NotEmpty final String password) {

                try {
                        return uc.createUser(name, email, password)
                                .onItem().ifNotNull().transform(user -> {
                                        String token = generateJWT(user);
                                        Authentication auth = new Authentication();
                                        auth.setToken(token);
                                        auth.setUser(user);
                                        return auth;
                                })
                                .log();
                } catch (Exception e) {
                        throw new UserWSException(e.getMessage(),
                                Response.Status.BAD_REQUEST);
                }
        }
}