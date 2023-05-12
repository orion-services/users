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

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import dev.orion.users.data.usecases.UserUC;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.usecases.UseCase;
import dev.orion.users.presentation.exceptions.UserWSException;
import dev.orion.users.presentation.mail.MailTemplate;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;

@Path("/api/users")
@RolesAllowed("user")
@RequestScoped
public class UpdateWS extends BaseWS {

        /** Business logic of the system. */
        private UseCase uc = new UserUC();

        /** Retrieve the e-mail from jwt. */
        @Inject
        @Claim(standard = Claims.email)
        String jwtEmail;

        /**
         * Updates the e-mail of a user. A JWT with role user is mandatory to
         * execute this method. Returns a new JWT to replace the old one because
         * the e-mail is a JWT claim.
         *
         * @param email    : The current e-mail
         * @param newEmail : The new e-mail of the user
         * @return A new JWT
         * @throws UserWSException Returns a HTTP 400 if the current jwt is
         *                         outdated or if there are other problems such as
         *                         username not found
         *                         or email already used
         */
        @PUT
        @Path("/update/email")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.TEXT_PLAIN)
        @Retry(maxRetries = 0, delay = DELAY)
        @WithSession
        public Uni<String> updateEmail(
                        @FormParam("email") @NotEmpty @Email final String email,
                        @FormParam("newEmail") @NotEmpty @Email final String newEmail) {

                // Checks the e-mail of the token
                checkTokenEmail(email, jwtEmail);

                Uni<User> uni = uc.updateEmail(email, newEmail)
                                .log()
                                .onItem().ifNotNull()
                                .call(this::sendEmail)
                                .onFailure()
                                .transform(e -> {
                                        throw new UserWSException(e.getMessage(),
                                                        Response.Status.BAD_REQUEST);
                                });
                return uni.onItem().transform(this::generateJWT);
        }

        /**
         * Helper method to send an email confirmation message to users.
         *
         * @param user : An user object
         * @return Uni<User>
         */
        private Uni<User> sendEmail(final User user) {
                return sendValidationEmail(user)
                                .onItem().transform(u -> u);
        }

        /**
         * Change a password of a user. A JWT with role user is mandatory to
         * execute this method.
         *
         * @param email       : User's Email
         * @param password    : Actual User password
         * @param newPassword : New User password
         * @return Returns the User who have his password change in JSON format
         * @throws UserWSException Returns a HTTP 400 if the current jwt is outdated
         *                         or if there are other problems such as e-mail not
         *                         found
         */
        @PUT
        @Path("/update/password")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_JSON)
        @Retry(maxRetries = 1, delay = DELAY)
        @WithSession
        public Uni<User> changePassword(
                        @FormParam("email") @NotEmpty @Email final String email,
                        @FormParam("password") @NotEmpty final String password,
                        @FormParam("newPassword") @NotEmpty final String newPassword) {

                // Checks the e-mail of the token
                checkTokenEmail(email, jwtEmail);

                return uc.updatePassword(email, password, newPassword)
                                .onItem().ifNotNull()
                                .transform(user -> user)
                                .log()
                                .onFailure()
                                .transform(e -> {
                                        throw new UserWSException(e.getMessage(),
                                                        Response.Status.BAD_REQUEST);
                                });
        }

        /**
         * Recoveries the user password.
         *
         * @param email : The current e-mail of the user
         * @return Returns HTTP 204 (No Content) if the method executed with success
         * @throws UserWSException Returns a HTTP 400 if the current jwt is
         *                         outdated or if there are other problems such as
         *                         e-mail not found
         */
        @POST
        @PermitAll
        @Path("/recoverPassword")
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @WithSession
        public Uni<Void> sendEmailUsingReactiveMailer(
                        @FormParam("email") @NotEmpty @Email final String email) {

                return uc.recoverPassword(email)
                                .onItem().ifNotNull().transformToUni(password -> MailTemplate.recoverPwd(password)
                                                .to(email)
                                                .subject("Recover Password")
                                                .send())
                                .log()
                                .onFailure().transform(e -> {
                                        throw new UserWSException(e.getMessage(),
                                                        Response.Status.BAD_REQUEST);
                                });
        }

}
