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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import dev.orion.users.usecase.UseCase;
import dev.orion.users.usecase.UserUC;
import dev.orion.users.ws.expections.UserWSException;
import io.smallrye.mutiny.Uni;

@Path("/api/user")
@RolesAllowed("user")
@RequestScoped
public class DeleteWS {


        /** Business logic. */
        private UseCase uc = new UserUC();

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
            @FormParam("email") @NotEmpty @Email final String email) {

            return uc.deleteUser(email)
                .log()
                .onFailure().transform(e -> {
                    throw new UserWSException(e.getMessage(),
                        Response.Status.BAD_REQUEST);
                });
    }

}
