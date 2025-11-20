/**
 * @License
 * Copyright 2024 Orion Services @ https://orion-services.dev
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
package dev.orion.users.frameworks.rest.authentication

import dev.orion.users.adapters.controllers.UserController
import dev.orion.users.frameworks.rest.ServiceException
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.faulttolerance.Retry
import org.jboss.resteasy.reactive.RestForm

/**
 * User API.
 */
@PermitAll
@Path("/users")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@WithSession
class AuthenticationWS {

    /** Fault tolerance default delay. */
    protected val DELAY: Long = 2000

    /** Business logic of the system. */
    @Inject
    private lateinit var controller: UserController

    /**
     * @deprecated This method is deprecated and will be removed in a future
     * release. Please, use the login method instead.
     *
     * Authenticates a user.
     *
     * @param email    The email of the user
     * @param password The password of the user
     * @return The JWT (JSON Web Token)
     * @throws A ServiceException if the user is not found
     */
    @POST
    @Path("/authenticate")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Retry(maxRetries = 1, delay = 2000)
    @Deprecated("Use login method instead", ReplaceWith("login(email, password)"))
    fun authenticate(
        @RestForm @NotEmpty @Email email: String,
        @RestForm @NotEmpty password: String
    ): Uni<String> {
        return controller.authenticate(email, password)
            .onItem().ifNotNull().transform { jwt -> jwt }
            .onItem().ifNull()
            .failWith(ServiceException("User not found", Response.Status.UNAUTHORIZED))
    }

    /**
     * Authenticates a user.
     *
     * @param email    The email of the user
     * @param password The password of the user
     * @return The JWT (JSON Web Token)
     * @throws A ServiceException if the user is not found
     */
    @POST
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun login(
        @RestForm @NotEmpty @Email email: String,
        @RestForm @NotEmpty password: String
    ): Uni<Response> {
        return controller.login(email, password)
            .log()
            .onItem().ifNotNull()
            .transform { dto -> Response.ok(dto).build() }
            .onItem().ifNull()
            .failWith(ServiceException("User not found", Response.Status.UNAUTHORIZED))
            .onFailure().transform { e ->
                val message = e.message ?: "Unknown error"
                throw ServiceException(message, Response.Status.BAD_REQUEST)
            }
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
    @Retry(maxRetries = 1, delay = 2000)
    fun createAuthenticate(
        @FormParam("name") @NotEmpty name: String,
        @FormParam("email") @NotEmpty @Email email: String,
        @FormParam("password") @NotEmpty password: String
    ): Uni<Response> {
        return controller.createAuthenticate(name, email, password)
            .log()
            .onItem().ifNotNull().transform { dto -> Response.ok(dto).build() }
            .onFailure().transform { e ->
                val message = e.message ?: "Unknown error"
                throw ServiceException(message, Response.Status.BAD_REQUEST)
            }
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
    fun validateEmail(
        @QueryParam("email") @NotEmpty email: String,
        @QueryParam("code") @NotEmpty code: String
    ): Uni<Response> {
        val result = controller.validateEmail(email, code)
        return if (result != null) {
            result
                .onItem().ifNotNull().transform { user ->
                    Response.ok(true).build()
                }
                .onItem().ifNull().continueWith {
                    val message = "Invalid e-mail or code"
                    throw ServiceException(message, Response.Status.BAD_REQUEST)
                }
                .onFailure().transform { e ->
                    val message = e.message ?: "Unknown error"
                    throw ServiceException(message, Response.Status.BAD_REQUEST)
                }
        } else {
            Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).build())
        }
    }
}

