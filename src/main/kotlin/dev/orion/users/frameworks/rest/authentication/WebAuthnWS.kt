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
package dev.orion.users.frameworks.rest.authentication

import dev.orion.users.adapters.controllers.UserController
import dev.orion.users.adapters.presenters.LoginResponseDTO
import dev.orion.users.frameworks.rest.ServiceException
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.faulttolerance.Retry
import org.jboss.resteasy.reactive.RestForm

/**
 * WebAuthn Web Service.
 */
@PermitAll
@Path("/users/webauthn")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@WithSession
class WebAuthnWS {

    /** Fault tolerance default delay. */
    protected val DELAY: Long = 2000

    /** Business logic of the system. */
    @Inject
    private lateinit var controller: UserController

    /**
     * Starts the WebAuthn registration process.
     *
     * @param email The email of the user
     * @return A JSON string containing PublicKeyCredentialCreationOptions
     * @throws ServiceException if the user is not found
     */
    @POST
    @Path("/register/start")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun startRegistration(
        @RestForm @NotEmpty @Email email: String,
        @RestForm origin: String?
    ): Uni<Response> {
        return controller.startWebAuthnRegistration(email, origin)
            .onItem().transform { optionsJson ->
                Response.ok(optionsJson).build()
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Failed to start WebAuthn registration"
                ServiceException(message, Response.Status.BAD_REQUEST)
            }
    }

    /**
     * Finishes the WebAuthn registration process.
     *
     * @param email     The email of the user
     * @param response  The registration response from the client (JSON string)
     * @param origin    The origin (complete site address) where the device was registered
     * @param deviceName Optional name for the device
     * @return true if registration was successful
     * @throws ServiceException if registration fails
     */
    @POST
    @Path("/register/finish")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun finishRegistration(
        @RestForm @NotEmpty @Email email: String,
        @RestForm @NotEmpty response: String,
        @RestForm @NotEmpty origin: String,
        @RestForm deviceName: String?
    ): Uni<Response> {
        return controller.finishWebAuthnRegistration(email, response, origin, deviceName)
            .onItem().transform { success ->
                val result = mapOf("success" to success, "message" to "WebAuthn credential registered successfully")
                Response.ok(result).build()
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Failed to finish WebAuthn registration"
                ServiceException(message, Response.Status.BAD_REQUEST)
            }
    }

    /**
     * Starts the WebAuthn authentication process.
     *
     * @param email The email of the user
     * @return A JSON string containing PublicKeyCredentialRequestOptions
     * @throws ServiceException if the user is not found or has no credentials
     */
    @POST
    @Path("/authenticate/start")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun startAuthentication(
        @RestForm @NotEmpty @Email email: String
    ): Uni<Response> {
        return controller.startWebAuthnAuthentication(email)
            .onItem().transform { optionsJson ->
                Response.ok(optionsJson).build()
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Failed to start WebAuthn authentication"
                ServiceException(message, Response.Status.BAD_REQUEST)
            }
    }

    /**
     * Finishes the WebAuthn authentication process.
     *
     * @param email    The email of the user
     * @param response The authentication response from the client (JSON string)
     * @return The LoginResponseDTO with JWT token
     * @throws ServiceException if authentication fails
     */
    @POST
    @Path("/authenticate/finish")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun finishAuthentication(
        @RestForm @NotEmpty @Email email: String,
        @RestForm @NotEmpty response: String
    ): Uni<Response> {
        return controller.finishWebAuthnAuthentication(email, response)
            .onItem().transform { loginResponse ->
                Response.ok(loginResponse).build()
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Invalid WebAuthn authentication"
                ServiceException(message, Response.Status.UNAUTHORIZED)
            }
    }
}

