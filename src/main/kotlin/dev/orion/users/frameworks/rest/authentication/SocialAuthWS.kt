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
import jakarta.validation.constraints.NotEmpty
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.faulttolerance.Retry
import org.jboss.resteasy.reactive.RestForm

/**
 * Social Authentication Web Service.
 * Handles OAuth2 authentication with Google and Apple.
 */
@PermitAll
@Path("/users/login")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@WithSession
class SocialAuthWS {

    /** Fault tolerance default delay. */
    protected val DELAY: Long = 2000

    /** Business logic of the system. */
    @Inject
    private lateinit var controller: UserController

    /**
     * Authenticates a user with Google OAuth2.
     * Receives the ID token from Google and validates it.
     *
     * @param idToken The Google ID token (JWT)
     * @return AuthenticationDTO with user and JWT token
     * @throws ServiceException if authentication fails
     */
    @POST
    @Path("/google")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun loginWithGoogle(
        @RestForm @NotEmpty idToken: String
    ): Uni<Response> {
        return validateGoogleToken(idToken)
            .onItem().transform { (email, name) ->
                controller.loginWithSocialProvider(email, name, "google")
            }
            .onItem().transformToUni { authUni ->
                authUni.onItem().transform { dto ->
                    Response.ok(dto).build()
                }
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Google authentication failed"
                ServiceException(message, Response.Status.UNAUTHORIZED)
            }
    }

    /**
     * Authenticates a user with Apple OAuth2.
     * Receives the ID token from Apple and validates it.
     *
     * @param idToken The Apple ID token (JWT)
     * @return AuthenticationDTO with user and JWT token
     * @throws ServiceException if authentication fails
     */
    @POST
    @Path("/apple")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun loginWithApple(
        @RestForm @NotEmpty idToken: String
    ): Uni<Response> {
        return validateAppleToken(idToken)
            .onItem().transform { (email, name) ->
                controller.loginWithSocialProvider(email, name, "apple")
            }
            .onItem().transformToUni { authUni ->
                authUni.onItem().transform { dto ->
                    Response.ok(dto).build()
                }
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Apple authentication failed"
                ServiceException(message, Response.Status.UNAUTHORIZED)
            }
    }

    /**
     * Validates Google ID token and extracts user information.
     * This is a simplified validation - in production, you should validate
     * the token signature and expiration using Google's public keys.
     *
     * @param idToken The Google ID token
     * @return Pair of (email, name)
     */
    private fun validateGoogleToken(idToken: String): Uni<Pair<String, String>> {
        return try {
            // Decode JWT token (simplified - in production, validate signature)
            val parts = idToken.split(".")
            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid token format")
            }

            // Decode payload (base64url)
            val payload = String(java.util.Base64.getUrlDecoder().decode(parts[1]))
            val json = com.fasterxml.jackson.databind.ObjectMapper().readTree(payload)

            val email = json.get("email")?.asText()
                ?: throw IllegalArgumentException("Email not found in token")
            val name = json.get("name")?.asText()
                ?: json.get("given_name")?.asText()?.plus(" ").plus(json.get("family_name")?.asText() ?: "")
                ?: email

            Uni.createFrom().item(Pair(email, name))
        } catch (e: Exception) {
            Uni.createFrom().failure(IllegalArgumentException("Invalid Google token: ${e.message}"))
        }
    }

    /**
     * Validates Apple ID token and extracts user information.
     * This is a simplified validation - in production, you should validate
     * the token signature and expiration using Apple's public keys.
     *
     * @param idToken The Apple ID token
     * @return Pair of (email, name)
     */
    private fun validateAppleToken(idToken: String): Uni<Pair<String, String>> {
        return try {
            // Decode JWT token (simplified - in production, validate signature)
            val parts = idToken.split(".")
            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid token format")
            }

            // Decode payload (base64url)
            val payload = String(java.util.Base64.getUrlDecoder().decode(parts[1]))
            val json = com.fasterxml.jackson.databind.ObjectMapper().readTree(payload)

            val email = json.get("email")?.asText()
                ?: throw IllegalArgumentException("Email not found in token")
            val name = json.get("name")?.let { nameNode ->
                if (nameNode.isObject) {
                    val firstName = nameNode.get("firstName")?.asText() ?: ""
                    val lastName = nameNode.get("lastName")?.asText() ?: ""
                    "$firstName $lastName".trim()
                } else {
                    nameNode.asText()
                }
            } ?: email

            Uni.createFrom().item(Pair(email, name))
        } catch (e: Exception) {
            Uni.createFrom().failure(IllegalArgumentException("Invalid Apple token: ${e.message}"))
        }
    }
}

