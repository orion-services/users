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

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import dev.orion.users.model.User;
import dev.orion.users.usecase.UseCase;
import dev.orion.users.usecase.UserUC;
import dev.orion.users.ws.exceptions.UserWSException;
import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.CheckedTemplate;
import io.smallrye.mutiny.Uni;

@Path("/api/user")
@RolesAllowed("user")
@RequestScoped
public class UpdateWS extends BaseWS {

    @Inject
    private Mailer mailer;

    @Inject
    private ReactiveMailer reactiveMailer;


    /** Business logic of the system. */
    private UseCase uc = new UserUC();

    /** Retrieve the e-mail from jwt. */
    @Inject
    @Claim(standard = Claims.email)
    private String jwtEmail;

    /**
     * Updates the e-mail of a user. A JWT with role user is mandatory to
     * execute this method. Returns a new JWT to replace the old one because
     * the e-mail is a JWT claim.
     *
     * @param email    : Current e-mail
     * @param newEmail : New e-mail
     * @return A new JWT
     * @throws UserWSException Returns a HTTP 400 if the current jwt is
     * outdated or if there are other problems such as username not found
     * or email already used
     */
    @PUT
    @Path("/update/email")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Retry(maxRetries = 0, delay = 2000)
    public Uni<String> updateEmail(
        @FormParam("email") @NotEmpty @Email final String email,
        @FormParam("newEmail") @NotEmpty @Email final String newEmail) {

        // Checks the e-mail of the token
        checkTokenEmail(email, jwtEmail);

        return uc.updateEmail(email, newEmail)
            .log()
            .onItem().ifNotNull()
                .transform(this::generateJWT)
            .onFailure()
                .transform(e -> {
                    throw new UserWSException(e.getMessage(),
                        Response.Status.BAD_REQUEST);
                });
    }

    /**
     * Change a password of a user. A JWT with role user is mandatory to
     * execute this method.
     *
     * @param email       : User's Email
     * @param password    : Actual User password
     * @param newPassword : New User password
     * @return Returns the User who have his password change in JSON format
     * @throws UserWSException Returns a HTTP 400 if the current jwt is
     * outdated or if there are other problems such as e-mail not found
     */
    @PUT
    @Path("/update/password")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
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
     * Recoveries the user password. A JWT with role user is mandatory to
     * execute this method.
     *
     * @param email : The current e-mail of the user
     * @return Returns the User who have his password change in JSON format
     * @throws UserWSException Returns a HTTP 400 if the current jwt is
     * outdated or if there are other problems such as e-mail not found
     */
    @POST
    @Path("/recoverPassword")
    public Uni<Void> sendEmailUsingReactiveMailer(
            @FormParam("email") @NotEmpty @Email final String email) {

         // Checks the e-mail of the token
         checkTokenEmail(email, jwtEmail);

         return uc.recoverPassword(email)
                .onItem().ifNotNull().transformToUni(password -> {
                    return Templates.recoverPassword(password)
                        .to(email)
                        .subject("Recuperação de senha")
                        .send();
                })
                .log()
                .onFailure().transform(e -> {
                    throw new UserWSException(
                        e.getMessage(),
                            Response.Status.BAD_REQUEST);
                });
    }
     /**
     * Class to load mail templates.
     */
    @CheckedTemplate
    public static class Templates {

        /**
         * Generates a mail template object.
         *
         * @param password : The new password of the user
         * @return A MailTemplateInstance object
         */
        public static native MailTemplateInstance recoverPassword(
            String password);
    }

}
