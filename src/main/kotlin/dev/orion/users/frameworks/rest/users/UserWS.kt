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
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
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
     * Updates user information (name, email and/or password). Requires authentication via JWT token with role "user".
     * At least one field (name, newEmail or newPassword) must be provided.
     *
     * @param email       The current email of the user
     * @param name        The new name (optional)
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
        @RestForm name: String?,
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

        // Extract groups/roles from JWT token
        val groups: Set<String> = try {
            jwt.getClaim<Set<String>>(Claims.groups.name) 
                ?: jwt.getClaim<List<String>>("groups")?.toSet()
                ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
        
        // Verifica se é admin (admins também têm role "user")
        val isAdmin = groups.contains("admin")
        
        // Se não for admin, só pode atualizar seu próprio usuário
        // Se for admin, pode atualizar qualquer usuário
        if (!isAdmin && email != jwtEmail) {
            throw ServiceException(
                "You can only update your own user",
                Response.Status.FORBIDDEN
            )
        }

        // Normaliza campos vazios para null
        val normalizedName = if (name.isNullOrBlank()) null else name.trim()
        val normalizedNewEmail = if (newEmail.isNullOrBlank()) null else newEmail.trim()
        val normalizedPassword = if (password.isNullOrBlank()) null else password
        val normalizedNewPassword = if (newPassword.isNullOrBlank()) null else newPassword

        return controller.updateUser(email, normalizedName, normalizedNewEmail, normalizedPassword, normalizedNewPassword, jwtEmail, isAdmin)
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

    /**
     * Updates 2FA settings for a user. Requires authentication via JWT token.
     * Allows the user to configure if 2FA is required for basic login and/or social login.
     *
     * @param email                    The current email of the user
     * @param require2FAForBasicLogin  Whether 2FA is required for basic login (optional, defaults to false)
     * @param require2FAForSocialLogin Whether 2FA is required for social login (optional, defaults to false)
     * @return The updated user object
     * @throws Bad request if the service was unable to update the settings
     */
    @POST
    @Path("/2fa/settings")
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun update2FASettings(
        @RestForm @NotEmpty @Email email: String,
        @RestForm require2FAForBasicLogin: Boolean?,
        @RestForm require2FAForSocialLogin: Boolean?
    ): Uni<Response> {
        // Extract email from JWT token
        val jwtEmail = jwt.getClaim<String>(Claims.email.name) 
            ?: jwt.getClaim<String>("email")
            ?: throw ServiceException(
                "Invalid token",
                Response.Status.UNAUTHORIZED
            )

        // Use provided values or default to false
        val requireBasic = require2FAForBasicLogin ?: false
        val requireSocial = require2FAForSocialLogin ?: false

        return controller.update2FASettings(email, requireBasic, requireSocial, jwtEmail)
            .onItem().transform { user -> Response.ok(user).build() }
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

    /**
     * Lists all users in the service. Requires admin role.
     *
     * @return A list of all users in JSON format
     * @throws Unauthorized if the user is not an admin
     */
    @GET
    @Path("/list")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun listUsers(): Uni<Response> {
        return controller.listAllUsers()
            .onItem().transform { users -> Response.ok(users).build() }
            .onFailure().transform { e ->
                val message = e.message ?: "Unknown error"
                val status = if (message.contains("Unauthorized") || message.contains("token")) {
                    Response.Status.UNAUTHORIZED
                } else {
                    Response.Status.INTERNAL_SERVER_ERROR
                }
                throw ServiceException(message, status)
            }
    }

    /**
     * Gets a user by email. Requires admin role.
     *
     * @param email The email of the user to retrieve
     * @return The user object in JSON format
     * @throws Bad request if the user is not found
     * @throws Unauthorized if the user is not an admin
     */
    @GET
    @Path("/by-email")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun getUserByEmail(
        @QueryParam("email") @NotEmpty @Email email: String
    ): Uni<Response> {
        return controller.getUserByEmail(email)
            .onItem().transform { user -> Response.ok(user).build() }
            .onFailure().transform { e ->
                val message = e.message ?: "Unknown error"
                val status = when {
                    message.contains("not found") -> Response.Status.NOT_FOUND
                    message.contains("Unauthorized") || message.contains("token") -> Response.Status.UNAUTHORIZED
                    else -> Response.Status.BAD_REQUEST
                }
                throw ServiceException(message, status)
            }
    }
}

