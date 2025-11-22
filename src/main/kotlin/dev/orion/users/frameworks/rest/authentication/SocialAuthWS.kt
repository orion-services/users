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
import dev.orion.users.adapters.presenters.LoginResponseDTO
import dev.orion.users.frameworks.rest.ServiceException
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.PermitAll
import jakarta.inject.Inject
import jakarta.validation.constraints.Email
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
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions

/**
 * Social Authentication Web Service.
 * Handles OAuth2 authentication with Google.
 */
@PermitAll
@Path("/users")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@WithSession
class SocialAuthWS {

    /** Fault tolerance default delay. */
    protected val DELAY: Long = 2000

    /** Business logic of the system. */
    @Inject
    private lateinit var controller: UserController

    /** Vertx instance for reactive HTTP client. */
    @Inject
    private lateinit var vertx: Vertx

    /** HTTP client for external API calls. */
    private val webClient: WebClient by lazy {
        WebClient.create(vertx, WebClientOptions().setFollowRedirects(true))
    }

    /**
     * Authenticates a user with Google OAuth2.
     * Receives the ID token from Google and validates it.
     * If the user has 2FA enabled and requires it for social login, returns a response indicating that 2FA code is required.
     *
     * @param idToken The Google ID token (JWT)
     * @return LoginResponseDTO (may contain JWT or indicate 2FA is required)
     * @throws ServiceException if authentication fails
     */
    @POST
    @Path("/login/google")
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
            .onItem().transformToUni { responseUni ->
                responseUni.onItem().transform { response ->
                    if (response.requires2FA) {
                        // Return 200 OK but indicate 2FA is required
                        Response.ok(response).status(Response.Status.OK).build()
                    } else {
                        // Normal login response
                        Response.ok(response.authentication).build()
                    }
                }
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Google authentication failed"
                ServiceException(message, Response.Status.UNAUTHORIZED)
            }
    }

    /**
     * Validates a TOTP code for 2FA authentication after social login.
     *
     * @param email The email of the user
     * @param code  The TOTP code to validate
     * @return AuthenticationDTO with JWT token
     * @throws ServiceException if validation fails
     */
    @POST
    @Path("/login/google/2fa")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    fun loginWithGoogle2FA(
        @RestForm @NotEmpty @Email email: String,
        @RestForm @NotEmpty code: String
    ): Uni<Response> {
        return controller.validateSocialLogin2FA(email, code)
            .onItem().transform { dto ->
                Response.ok(dto).build()
            }
            .onFailure().transform { e ->
                val message = e.message ?: "Invalid TOTP code"
                ServiceException(message, Response.Status.UNAUTHORIZED)
            }
    }

    /**
     * Validates Google ID token or access token and extracts user information.
     * If the token is a JWT (id_token), it decodes it directly.
     * If the token is not a JWT (access_token), it fetches user info from Google API.
     *
     * @param token The Google ID token (JWT) or access token
     * @return Pair of (email, name)
     */
    private fun validateGoogleToken(token: String): Uni<Pair<String, String>> {
        return try {
            // Normalize token: remove leading/trailing whitespace and any extra spaces
            val normalizedToken = token.trim().replace("\\s+".toRegex(), "")
            
            // Validate token is not empty
            if (normalizedToken.isEmpty()) {
                return Uni.createFrom().failure(IllegalArgumentException("Invalid Google token: Token is empty"))
            }
            
            // Try to validate as JWT (id_token) first
            val jwtResult = tryValidateAsJWT(normalizedToken)
            if (jwtResult != null) {
                return jwtResult
            }
            
            // If not a valid JWT, assume it's an access_token and fetch user info from Google API
            fetchUserInfoFromGoogleAPI(normalizedToken)
        } catch (e: IllegalArgumentException) {
            Uni.createFrom().failure(IllegalArgumentException("Invalid Google token: ${e.message}"))
        } catch (e: Exception) {
            Uni.createFrom().failure(IllegalArgumentException("Invalid Google token: ${e.javaClass.simpleName} - ${e.message ?: "Unknown error"}"))
        }
    }

    /**
     * Tries to validate the token as a JWT (id_token).
     * Returns Uni<Pair<String, String>> if successful, null otherwise.
     */
    private fun tryValidateAsJWT(normalizedToken: String): Uni<Pair<String, String>>? {
        return try {
            // Validate JWT format: should have exactly 3 parts separated by dots
            val parts = normalizedToken.split(".")
            if (parts.size != 3) {
                return null // Not a JWT, might be an access_token
            }
            
            // Validate parts are not empty
            if (parts[0].isEmpty() || parts[1].isEmpty() || parts[2].isEmpty()) {
                return null // Invalid JWT format
            }

            // Decode payload (base64url)
            val payload: String
            try {
                payload = String(java.util.Base64.getUrlDecoder().decode(parts[1]))
            } catch (e: IllegalArgumentException) {
                return null // Not a valid base64url, might be an access_token
            }
            
            // Parse JSON payload
            val json: com.fasterxml.jackson.databind.JsonNode
            try {
                json = com.fasterxml.jackson.databind.ObjectMapper().readTree(payload)
            } catch (e: Exception) {
                return null // Not valid JSON, might be an access_token
            }

            // Extract email
            val email = json.get("email")?.asText()
                ?: return null // No email in token, might be an access_token
            
            // Extract name (try name, then given_name + family_name, fallback to email)
            val name = json.get("name")?.asText()
                ?: json.get("given_name")?.asText()?.plus(" ").plus(json.get("family_name")?.asText() ?: "")
                ?: email

            Uni.createFrom().item(Pair(email, name))
        } catch (e: Exception) {
            null // Any error means it's not a valid JWT
        }
    }

    /**
     * Fetches user information from Google API using an access_token.
     */
    private fun fetchUserInfoFromGoogleAPI(accessToken: String): Uni<Pair<String, String>> {
        val future = webClient.get(443, "www.googleapis.com", "/oauth2/v2/userinfo")
            .ssl(true)
            .putHeader("Authorization", "Bearer $accessToken")
            .send()
        
        return Uni.createFrom().completionStage(future.toCompletionStage())
            .onItem().transform { response ->
                if (response.statusCode() != 200) {
                    val errorBody = try {
                        response.bodyAsString()
                    } catch (e: Exception) {
                        "Unable to read error response"
                    }
                    throw IllegalArgumentException("Failed to fetch user info from Google API: HTTP ${response.statusCode()} - $errorBody")
                }

                val json: com.fasterxml.jackson.databind.JsonNode
                try {
                    json = com.fasterxml.jackson.databind.ObjectMapper().readTree(response.bodyAsString())
                } catch (e: Exception) {
                    throw IllegalArgumentException("Failed to parse Google API response: ${e.message}")
                }

                val email = json.get("email")?.asText()
                    ?: throw IllegalArgumentException("Email not found in Google API response")
                
                val name = json.get("name")?.asText()
                    ?: json.get("given_name")?.asText()?.plus(" ").plus(json.get("family_name")?.asText() ?: "")
                    ?: email

                Pair(email, name)
            }
            .onFailure().transform { throwable ->
                IllegalArgumentException("Failed to fetch user info from Google API: ${throwable.message ?: throwable.javaClass.simpleName}")
            }
    }
}

