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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.resteasy.reactive.RestForm;

import dev.orion.users.dto.Authentication;
import dev.orion.users.model.User;
import dev.orion.users.usecase.UseCase;
import dev.orion.users.usecase.UserUC;
import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.CheckedTemplate;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;

/**
 * User API.
 */
@Path("/api/user")
public class Service {

        @Inject
        Mailer mailer;

        @Inject
        ReactiveMailer reactiveMailer;

        /* Configure the issuer for JWT generation. */
        @ConfigProperty(name = "user.issuer")
        private Optional<String> issuer;

        /** Business logic of the system. */
        private UseCase uc = new UserUC();

        /**
        * Creates a user inside the service.
         *
         * @param name     : The name of the user
         * @param email    : The email of the user
         * @param password : The password of the user
         *
         * @return The user object in JSON format
         * @throws ServiceException Returns a HTTP 409 if the e-mail already
         * exists in the database or if the password is lower than eight
         * characters
         */
        @POST
        @Path("/create")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<User> create(
                @FormParam("name") @NotEmpty final String name,
                @FormParam("email") @NotEmpty @Email final String email,
                @FormParam("password") @NotEmpty final String password) {

                try {
                        return uc.createUser(name, email, password)
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
         * exists in the database or if the password is lower than eight
         * characters
         */
        @POST
        @Path("/createAuthenticate")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<Authentication> createAuthenticate(
                @FormParam("name") @NotEmpty final String name,
                @FormParam("email") @NotEmpty @Email final String email,
                @FormParam("password") @NotEmpty final String password) {

                try {
                        return
                        uc.createUser(name, email, password)
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
         *      able to find the user in the database
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

                return uc.authenticate(email, password)
                        .onItem()
                                .ifNotNull()
                                .transform(this::generateJWT)
                        .onItem()
                                .ifNull()
                                .failWith(new ServiceException("User not found",
                                                Response.Status.UNAUTHORIZED));
        }

        @PUT
        @Path("/update/email")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<User> changeEmail(
                @FormParam("email") @NotEmpty @Email final String email,
                @FormParam("newEmail") @NotEmpty @Email final String newEmail
        ) {
                return uc.changeEmail(email, newEmail)
                        .onItem().ifNotNull().transform(user -> user)
                        .log()
                        .onFailure().transform(e -> {
                                String message = e.getMessage();
                                throw new ServiceException(
                                        message,
                                        Response.Status.BAD_REQUEST);
                        });
        }

        /**
         * Change a password of a logged user.
         *
         * @param email       : User's Email
         * @param password    : Actual User password
         * @param newPassword : New User password
         *
         * @return Returns the User who have his password change in JSON format
         */
        @PUT
        @Path("/update/password")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = 2000)
        public Uni<User> changePassword(
                @FormParam("email") @NotEmpty @Email final String email,
                @FormParam("password") @NotEmpty final String password,
                @FormParam("newPassword") @NotEmpty final String newPassword
        ) {
                return uc.changePassword(password, newPassword, email)
                        .onItem().ifNotNull().transform(user -> user)
                        .log()
                        .onFailure().transform(e -> {
                                String message = e.getMessage();
                                throw new ServiceException(
                                        message,
                                        Response.Status.BAD_REQUEST);
                        });
        }

        @CheckedTemplate
        public static class Templates {
                public static native MailTemplateInstance recoverPassword(String password);
       }

        @POST
        @Path("/recoverPassword")
        public Uni<Void> sendEmailUsingReactiveMailer(
                @FormParam("email") @NotEmpty @Email final String email
        ) {
                return uc.recoverPassword(email)
                .onItem().ifNotNull()
                .transformToUni(password -> {
                        return Templates.recoverPassword(password)
                        .to(email)
                        .subject("Recuperação de senha")
                        .send();

                }).log()
                .onFailure().transform(e -> {
                        String message = e.getMessage();
                        throw new ServiceException(
                                message,
                                Response.Status.BAD_REQUEST);
                });
        }

        /**
         * Deletes a User from the Service
         *
         * @param email : User's email
         *
         * @return Returns the number of deleted Users
         */
        @DELETE
        @Path("/delete")
        public Uni<Long> deleteUser(
                @FormParam("email") @NotEmpty @Email final String email
        ) {
                return User.delete("email", email)
                .log()
                .onFailure().transform(e -> {
                        String message = e.getMessage();
                        throw new ServiceException(
                                message,
                                Response.Status.BAD_REQUEST);
                });
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
