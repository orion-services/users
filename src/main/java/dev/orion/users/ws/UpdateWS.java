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

import dev.orion.users.model.User;
import dev.orion.users.usecase.UseCase;
import dev.orion.users.usecase.UserUC;
import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.qute.CheckedTemplate;
import io.smallrye.mutiny.Uni;

@Path("/api/user")
//@RolesAllowed("user")
public class UpdateWS {

    /** Business logic of the system. */
    private UseCase uc = new UserUC();

    @PUT
    @Path("/update/email")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    public Uni<User> changeEmail(
            @FormParam("email") @NotEmpty @Email final String email,
            @FormParam("newEmail") @NotEmpty @Email final String newEmail) {
        return uc.changeEmail(email, newEmail)
                .onItem().ifNotNull().transform(user -> user)
                .log()
                .onFailure().transform(e -> {
                    String message = e.getMessage();
                    throw new WSException(
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
            @FormParam("newPassword") @NotEmpty final String newPassword) {
        return uc.changePassword(password, newPassword, email)
                .onItem().ifNotNull().transform(user -> user)
                .log()
                .onFailure().transform(e -> {
                    String message = e.getMessage();
                    throw new WSException(
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
    public Uni<Void> sendEmailUsingReactiveMaiwler(
            @FormParam("email") @NotEmpty @Email final String email) {
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
                    throw new WSException(
                            message,
                            Response.Status.BAD_REQUEST);
                });
    }



}
