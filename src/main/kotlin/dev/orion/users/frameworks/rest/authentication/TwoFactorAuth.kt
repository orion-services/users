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
import dev.orion.users.adapters.presenters.AuthenticationDTO
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
 * Two Factor Authentication Web Service.
 */
@PermitAll
@Path("/users/google/2FAuth")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@WithSession
class TwoFactorAuth {

    /** Fault tolerance default delay. */
    protected val DELAY: Long = 2000

    /** Business logic of the system. */
    @Inject
    private lateinit var controller: UserController

    /**
     * Generates a QR code for 2FA setup.
     *
     * @param email    The email of the user
     * @param password The password of the user
     * @return The QR code image as PNG
     * @throws ServiceException if the user is not found or credentials are invalid
     */
    @POST
    @Path("/qrCode")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("image/png")
    @Retry(maxRetries = 1, delay = 2000)
    fun generateQRCode(
        @RestForm @NotEmpty @Email email: String,
        @RestForm @NotEmpty password: String
    ): Uni<Response> {
        return controller.generate2FAQRCode(email, password)
            .onItem().transform { qrCodeBytes ->
                Response.ok(qrCodeBytes)
                    .type("image/png")
                    .build()
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Failed to generate QR code"
                ServiceException(message, Response.Status.BAD_REQUEST)
            }
    }

    /**
     * Validates a TOTP code for 2FA authentication.
     *
     * @param email The email of the user
     * @param code  The TOTP code to validate
     * @return The AuthenticationDTO with JWT token
     * @throws ServiceException if validation fails
     */
    @POST
    @Path("/validate")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun validateCode(
        @RestForm @NotEmpty @Email email: String,
        @RestForm @NotEmpty code: String
    ): Uni<Response> {
        return controller.validate2FACode(email, code)
            .onItem().transform { dto ->
                Response.ok(dto).build()
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Invalid TOTP code"
                ServiceException(message, Response.Status.UNAUTHORIZED)
            }
    }
}

