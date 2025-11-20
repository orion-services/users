/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
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
package dev.orion.users.frameworks.rest.users

import dev.orion.users.adapters.controllers.UserController
import dev.orion.users.frameworks.rest.ServiceException
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.faulttolerance.Retry
import org.eclipse.microprofile.jwt.Claims
import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.resteasy.reactive.RestForm

/**
 * Create a user endpoints.
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
class UserWS {

    /** Business logic of the system. */
    @Inject
    lateinit var controller: UserController

    /** JWT token for authentication. */
    @Inject
    lateinit var jwt: JsonWebToken

    /** Fault tolerance default delay. */
    protected val DELAY: Long = 2000

    /**
     * Creates a user inside the service.
     *
     * @param name     The name of the user
     * @param email    The email of the user
     * @param password The password of the user
     * @return The user object in JSON format
     * @throws Bad request if the service was unable to create the user
     */
    @POST
    @Path("/create")
    @PermitAll
    @Retry(maxRetries = 1, delay = 2000)
    fun create(
        @FormParam("name") @NotEmpty name: String,
        @FormParam("email") @NotEmpty @Email email: String,
        @FormParam("password") @NotEmpty password: String
    ): Uni<Response> {
        return controller.createUser(name, email, password)
            .log()
            .onItem().ifNotNull().transform { user -> Response.ok(user).build() }
            .onFailure().transform { e ->
                val message = e.message ?: "Unknown error"
                throw ServiceException(message, Response.Status.BAD_REQUEST)
            }
    }

    /**
     * Deletes a user inside the service.
     *
     * @param email    The email of the user
     * @return A boolean
     * @throws Bad request if the service was unable to create the user
     */
    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    @Retry(maxRetries = 1, delay = 2000)
    fun delete(
        @FormParam("email") @NotEmpty @Email email: String
    ): Uni<Response> {
        return controller.deleteUser(email)
            .log()
            .onItem().ifNotNull().transform { result ->
                Response.ok(true).build()
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Unknown error"
                throw ServiceException(message, Response.Status.BAD_REQUEST)
            }
    }

    /**
     * Updates user information (email and/or password). Requires authentication via JWT token.
     * At least one field (newEmail or newPassword) must be provided.
     *
     * @param email       The current email of the user
     * @param newEmail    The new email address (optional)
     * @param password    The current password (required if updating password)
     * @param newPassword The new password (optional)
     * @return A LoginResponseDTO with AuthenticationDTO containing token and updated user
     * @throws Bad request if the service was unable to update the user
     */
    @PUT
    @Path("/update")
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun updateUser(
        @RestForm @NotEmpty @Email email: String,
        @RestForm newEmail: String?,
        @RestForm password: String?,
        @RestForm newPassword: String?
    ): Uni<Response> {
        // Extract email from JWT token
        val jwtEmail = jwt.getClaim<String>(Claims.email.name) 
            ?: jwt.getClaim<String>("email")
            ?: throw ServiceException(
                "Invalid token",
                Response.Status.UNAUTHORIZED
            )

        return controller.updateUser(email, newEmail, password, newPassword, jwtEmail)
            .onItem().transform { response -> Response.ok(response).build() }
            .onFailure().transform { e ->
                val message = e.message ?: "Unknown error"
                val status = if (message.contains("Unauthorized") || message.contains("token")) {
                    Response.Status.UNAUTHORIZED
                } else {
                    Response.Status.BAD_REQUEST
                }
                throw ServiceException(message, status)
            }
    }
}

