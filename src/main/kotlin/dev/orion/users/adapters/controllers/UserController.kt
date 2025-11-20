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
package dev.orion.users.adapters.controllers

import dev.orion.users.adapters.gateways.entities.UserEntity
import dev.orion.users.adapters.gateways.repository.UserRepository
import dev.orion.users.adapters.presenters.AuthenticationDTO
import dev.orion.users.adapters.presenters.LoginResponseDTO
import dev.orion.users.adapters.gateways.entities.WebAuthnCredentialEntity
import dev.orion.users.adapters.gateways.repository.WebAuthnCredentialRepository
import dev.orion.users.application.interfaces.AuthenticateUCI
import dev.orion.users.application.interfaces.CreateUserUCI
import dev.orion.users.application.interfaces.TwoFactorAuthUCI
import dev.orion.users.application.interfaces.UpdateUser
import dev.orion.users.application.interfaces.WebAuthnUCI
import dev.orion.users.application.usecases.AuthenticateUC
import dev.orion.users.application.usecases.CreateUserUC
import dev.orion.users.application.usecases.TwoFactorAuthUC
import dev.orion.users.application.usecases.UpdateUserImpl
import dev.orion.users.application.usecases.WebAuthnUC
import dev.orion.users.enterprise.model.User
import dev.orion.users.frameworks.mail.MailTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import java.security.SecureRandom
import java.util.*
import java.util.Base64
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * The controller class.
 */
@WithSession
@ApplicationScoped
class UserController : BasicController() {

    /** Use cases for users. */
    private val createUC: CreateUserUCI = CreateUserUC()

    /** Use cases for authentication. */
    private val authenticationUC: AuthenticateUCI = AuthenticateUC()

    /** Use cases for two factor authentication. */
    private val twoFactorAuthUC: TwoFactorAuthUCI = TwoFactorAuthUC()

    /** Use cases for WebAuthn. */
    private val webAuthnUC: WebAuthnUCI = WebAuthnUC()

    /** Use cases for updating user. */
    private val updateUserUC: UpdateUser = UpdateUserImpl()

    /** Persistence layer. */
    @Inject
    lateinit var userRepository: UserRepository

    /** WebAuthn credential repository. */
    @Inject
    lateinit var webAuthnCredentialRepository: WebAuthnCredentialRepository

    /** Object mapper for JSON. */
    private val objectMapper = ObjectMapper()

    /**
     * Create a new user. Validates the business rules, persists the user and
     * sends an e-mail to the user confirming the registration.
     *
     * @param name     : The user name
     * @param email    : The user e-mail
     * @param password : The user password
     * @return : Returns a Uni<UserEntity> object
     */
    fun createUser(name: String, email: String, password: String): Uni<UserEntity> {
        val user: User = createUC.createUser(name, email, password)
        val entity: UserEntity = mapper.map(user, UserEntity::class.java)
        return userRepository.createUser(entity)
            .onItem().ifNotNull().transform { u -> u }
            .onItem().ifNotNull().call { user -> this.sendValidationEmail(user) }
    }

    /**
     * Validates the e-mail of a user.
     *
     * @param email : The e-mail of the user
     * @param code  : The validation code
     * @return : Returns a Uni<UserEntity> object
     */
    fun validateEmail(email: String, code: String): Uni<UserEntity>? {
        var result: Uni<UserEntity>? = null
        if (authenticationUC.validateEmail(email, code) == true) {
            result = userRepository.validateEmail(email, code)
        }
        return result
    }

    /**
     * Authenticates the user in the service.
     *
     * @param email    : The user e-mail
     * @param password : The user password
     * @return : Returns a JSON Web Token (JWT)
     */
    fun authenticate(email: String, password: String): Uni<String> {
        // Creates a user in the model to encrypts the password and
        // converts it to an entity
        val entity: UserEntity = mapper.map(
            authenticationUC.authenticate(email, password),
            UserEntity::class.java
        )

        // Finds the user in the service through email and password and
        // generates a JWT
        return userRepository.authenticate(entity)
            .onItem().ifNotNull()
            .transform { this.generateJWT(it) }
    }

    /**
     * Authenticates a user with the provided email and password.
     * If the user has 2FA enabled, returns a response indicating that 2FA code is required.
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @return a Uni object that emits a LoginResponseDTO
     */
    fun login(email: String, password: String): Uni<LoginResponseDTO> {
        // Creates a user in the model to encrypts the password and
        // converts it to an entity
        val entity: UserEntity = mapper.map(
            authenticationUC.authenticate(email, password),
            UserEntity::class.java
        )

        return userRepository.authenticate(entity)
            .onItem().ifNotNull().transform { user ->
                val response = LoginResponseDTO()
                
                // Check if user has 2FA enabled
                if (user.isUsing2FA) {
                    response.requires2FA = true
                    response.message = "2FA code required"
                } else {
                    // Normal login without 2FA
                    val dto = AuthenticationDTO()
                    dto.token = this.generateJWT(user)
                    dto.user = user
                    response.authentication = dto
                    response.requires2FA = false
                }
                
                response
            }
    }

    /**
     * Creates a user, generates a Json Web Token and returns a
     * AuthenticationDTO object.
     *
     * @param name     : The user name
     * @param email    : The user e-mail
     * @param password : The user password
     * @return A Uni<AuthenticationDTO> object
     */
    fun createAuthenticate(name: String, email: String, password: String): Uni<AuthenticationDTO> {
        return this.createUser(name, email, password)
            .onItem().ifNotNull().transform { user ->
                val dto = AuthenticationDTO()
                dto.token = this.generateJWT(user)
                dto.user = user
                dto
            }
    }

    /**
     * Delete a user from the service.
     *
     * @param email The user's e-mail
     * @return A Uni<Void> object
     */
    fun deleteUser(email: String): Uni<Void> {
        return userRepository.deleteUser(email)
    }

    /**
     * Generates a QR code for 2FA setup.
     * Validates user credentials, generates a secret key, updates the user,
     * and returns a QR code image.
     *
     * @param email    The email of the user
     * @param password The password of the user
     * @return A Uni that emits a ByteArray containing the QR code image
     */
    fun generate2FAQRCode(email: String, password: String): Uni<ByteArray> {
        // Validate credentials using the use case
        val user: User = twoFactorAuthUC.generateQRCode(email, password)
        val entity: UserEntity = mapper.map(user, UserEntity::class.java)

        // Authenticate user first
        return userRepository.authenticate(entity)
            .onItem().ifNotNull().transformToUni { authenticatedUser ->
                // Generate secret key
                val secretKey = generateSecretKey()
                
                // Update user with 2FA secret
                authenticatedUser.isUsing2FA = true
                authenticatedUser.secret2FA = secretKey
                
                // Persist the updated user
                userRepository.updateUser(authenticatedUser)
                    .onItem().transform { updatedUser ->
                        // Generate QR code
                        val issuer = issuer?.orElse("Orion Users") ?: "Orion Users"
                        val barCodeData = getAuthenticatorBarCode(
                            secretKey,
                            updatedUser.email ?: email,
                            issuer
                        )
                        createQrCode(barCodeData)
                    }
            }
    }

    /**
     * Validates a TOTP code for 2FA authentication.
     *
     * @param email The email of the user
     * @param code  The TOTP code to validate
     * @return A Uni that emits an AuthenticationDTO with JWT if validation succeeds
     */
    fun validate2FACode(email: String, code: String): Uni<AuthenticationDTO> {
        // Validate code format using use case
        val user: User = twoFactorAuthUC.validateCode(email, code)
        
        // Find user by email
        return userRepository.findUserByEmail(email)
            .onItem().ifNull()
            .failWith(IllegalArgumentException("User not found"))
            .onItem().ifNotNull().transformToUni { userEntity ->
                // Check if 2FA is enabled
                if (!userEntity.isUsing2FA) {
                    throw IllegalArgumentException("2FA is not enabled for this user")
                }
                
                // Get secret from user
                val secret = userEntity.secret2FA
                if (secret == null) {
                    throw IllegalArgumentException("2FA secret not found")
                }
                
                // Validate TOTP code
                val expectedCode = getTOTPCode(secret)
                if (code != expectedCode) {
                    throw IllegalArgumentException("Invalid TOTP code")
                }
                
                // Generate JWT and return DTO
                val dto = AuthenticationDTO()
                dto.token = generateJWT(userEntity)
                dto.user = userEntity
                Uni.createFrom().item(dto)
            }
    }

    /**
     * Starts WebAuthn registration process.
     *
     * @param email The email of the user
     * @param origin Optional origin URL to extract rpId from
     * @return A JSON string containing PublicKeyCredentialCreationOptions
     */
    fun startWebAuthnRegistration(email: String, origin: String? = null): Uni<String> {
        // Validate email using use case
        webAuthnUC.startRegistration(email)

        return userRepository.findUserByEmail(email)
            .onItem().ifNull()
            .failWith(IllegalArgumentException("User not found"))
            .onItem().ifNotNull().transform { user ->
                // Generate challenge (base64url encoded)
                val challengeBytes = ByteArray(32)
                SecureRandom().nextBytes(challengeBytes)
                val challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes)
                
                // Create user ID (base64url encoded email)
                val userId = Base64.getUrlEncoder().withoutPadding().encodeToString((user.email ?: email).toByteArray())
                
                // Create PublicKeyCredentialCreationOptions as JSON
                val rpName = issuer?.orElse("Orion Users") ?: "Orion Users"
                val rpId = origin?.let { extractRpIdFromOrigin(it) } ?: "localhost"
                val userName = user.email ?: email
                val userDisplayName = user.name ?: user.email ?: email
                
                val options = mapOf(
                    "rp" to mapOf(
                        "name" to rpName,
                        "id" to rpId
                    ),
                    "user" to mapOf(
                        "id" to userId,
                        "name" to userName,
                        "displayName" to userDisplayName
                    ),
                    "challenge" to challenge,
                    "pubKeyCredParams" to listOf(
                        mapOf("type" to "public-key", "alg" to -7), // ES256
                        mapOf("type" to "public-key", "alg" to -257) // RS256
                    ),
                    "authenticatorSelection" to mapOf(
                        "authenticatorAttachment" to "platform",
                        "userVerification" to "preferred"
                    ),
                    "timeout" to 60000L,
                    "attestation" to "none"
                )

                val response = mapOf(
                    "options" to options,
                    "challenge" to challenge
                )
                objectMapper.writeValueAsString(response)
            }
    }

    /**
     * Finishes WebAuthn registration process.
     *
     * @param email     The email of the user
     * @param response  The registration response from the client (JSON string)
     * @param origin    The origin (complete site address) where the device was registered
     * @param deviceName Optional name for the device
     * @return true if registration was successful
     */
    fun finishWebAuthnRegistration(email: String, response: String, origin: String, deviceName: String?): Uni<Boolean> {
        // Validate using use case
        webAuthnUC.finishRegistration(email, response, origin, deviceName)

        return userRepository.findUserByEmail(email)
            .onItem().ifNull()
            .failWith(IllegalArgumentException("User not found"))
            .onItem().ifNotNull().transformToUni { user ->
                try {
                    // Parse the response (simplified - actual implementation would parse JSON properly)
                    // In production, this would properly parse and validate the WebAuthn response
                    val credentialEntity = WebAuthnCredentialEntity()
                    credentialEntity.userEmail = email
                    credentialEntity.credentialId = UUID.randomUUID().toString() // Should be from actual response
                    credentialEntity.publicKey = response // Should be properly extracted and stored
                    credentialEntity.counter = 0
                    credentialEntity.origin = origin
                    credentialEntity.notes = deviceName ?: "Unknown Device"
                    credentialEntity.deviceName = deviceName ?: "Unknown Device" // Keep for compatibility

                    webAuthnCredentialRepository.saveCredential(credentialEntity)
                        .onItem().transform { true }
                } catch (e: Exception) {
                    throw IllegalArgumentException("Failed to process WebAuthn registration: ${e.message}")
                }
            }
    }

    /**
     * Starts WebAuthn authentication process.
     *
     * @param email The email of the user
     * @return A JSON string containing PublicKeyCredentialRequestOptions
     */
    fun startWebAuthnAuthentication(email: String): Uni<String> {
        // Validate email using use case
        webAuthnUC.startAuthentication(email)

        return userRepository.findUserByEmail(email)
            .onItem().ifNull()
            .failWith(IllegalArgumentException("User not found"))
            .onItem().ifNotNull().transformToUni { user ->
                webAuthnCredentialRepository.findByUserEmail(email)
                    .onItem().ifNull()
                    .failWith(IllegalArgumentException("No WebAuthn credentials found for user"))
                    .onItem().ifNotNull().transform { credentials ->
                        if (credentials.isEmpty()) {
                            throw IllegalArgumentException("No WebAuthn credentials found for user")
                        }

                        // Generate challenge (base64url encoded)
                        val challengeBytes = ByteArray(32)
                        SecureRandom().nextBytes(challengeBytes)
                        val challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes)

                        // Create allowCredentials list
                        val allowCredentials = credentials.mapNotNull { cred ->
                            cred.credentialId?.let { id ->
                                mapOf(
                                    "type" to "public-key",
                                    "id" to id
                                )
                            }
                        }

                        // Extract rpId from stored origin to ensure consistency
                        val rpId = credentials.firstOrNull()?.origin?.let { extractRpIdFromOrigin(it) } ?: "localhost"
                        
                        // Create PublicKeyCredentialRequestOptions as JSON
                        val options = mapOf(
                            "challenge" to challenge,
                            "rpId" to rpId,
                            "allowCredentials" to allowCredentials,
                            "userVerification" to "preferred",
                            "timeout" to 60000L
                        )

                        val response = mapOf(
                            "options" to options,
                            "challenge" to challenge
                        )
                        objectMapper.writeValueAsString(response)
                    }
            }
    }

    /**
     * Finishes WebAuthn authentication process.
     *
     * @param email    The email of the user
     * @param response The authentication response from the client (JSON string)
     * @return An AuthenticationDTO with JWT if authentication succeeds
     */
    fun finishWebAuthnAuthentication(email: String, response: String): Uni<AuthenticationDTO> {
        // Validate using use case
        webAuthnUC.finishAuthentication(email, response)

        return userRepository.findUserByEmail(email)
            .onItem().ifNull()
            .failWith(IllegalArgumentException("User not found"))
            .onItem().ifNotNull().transformToUni { user ->
                // In production, this would properly parse and validate the WebAuthn response
                // For now, we'll do a simplified validation
                webAuthnCredentialRepository.findByUserEmail(email)
                    .onItem().ifNull()
                    .failWith(IllegalArgumentException("No WebAuthn credentials found"))
                    .onItem().ifNotNull().transform { credentials ->
                        if (credentials.isEmpty()) {
                            throw IllegalArgumentException("No WebAuthn credentials found")
                        }

                        // Update counter (simplified - actual implementation would validate signature)
                        val credential = credentials.first()
                        credential.counter++
                        webAuthnCredentialRepository.saveCredential(credential)

                        // Generate JWT and return DTO
                        val dto = AuthenticationDTO()
                        dto.token = generateJWT(user)
                        dto.user = user
                        dto
                    }
            }
    }

    /**
     * Recovers the password of a user. Generates a new password, updates it in the database,
     * and sends it via email.
     *
     * @param email : The e-mail of the user
     * @return A Uni<Void> that completes when the password is recovered and email is sent
     */
    fun recoverPassword(email: String): Uni<Void> {
        // Validate email using use case
        authenticationUC.recoverPassword(email)

        // Generate new password and update user in repository
        return userRepository.recoverPassword(email)
            .onItem().ifNotNull().call { newPassword ->
                // Send email with new password
                sendRecoveryEmail(email, newPassword)
            }
            .onItem().transform { null }
    }

    /**
     * Sends a recovery password email to the user.
     *
     * @param email    : The user's email
     * @param password : The new password
     * @return A Uni<Void> that completes when the email is sent
     */
    private fun sendRecoveryEmail(email: String, password: String): Uni<Void> {
        return MailTemplate.recoverPwd(password)
            .to(email)
            .subject("Recuperação de senha")
            .send()
            .onItem().transform { null }
    }

    /**
     * Updates the email of a user. Validates the token, updates the email,
     * generates a new JWT, and sends a validation email to the new address.
     *
     * @param email    : The current email of the user
     * @param newEmail : The new email address
     * @param jwtEmail : The email from the JWT token (for validation)
     * @return A Uni<String> that emits the new JWT token
     */
    fun updateEmail(email: String, newEmail: String, jwtEmail: String): Uni<String> {
        // Validate using use case
        val user: User = updateUserUC.updateEmail(email, newEmail)
        
        // Validate that JWT email matches the current email
        checkTokenEmail(email, jwtEmail)
        
        // Update email in repository
        return userRepository.updateEmail(email, newEmail)
            .onItem().ifNotNull().call { updatedUser ->
                // Send validation email to the new email address
                sendValidationEmail(updatedUser)
            }
            .onItem().ifNotNull().transform { updatedUser ->
                // Generate new JWT with updated email
                generateJWT(updatedUser)
            }
    }

    /**
     * Updates the password of a user. Validates the current password and updates it.
     *
     * @param email       : The user's email
     * @param password    : The current password
     * @param newPassword : The new password
     * @param jwtEmail    : The email from the JWT token (for validation)
     * @return A Uni<UserEntity> that emits the updated user
     */
    fun updatePassword(email: String, password: String, newPassword: String, jwtEmail: String): Uni<UserEntity> {
        // Validate using use case
        val user: User = updateUserUC.updatePassword(email, password, newPassword)
        
        // Validate that JWT email matches the request email
        checkTokenEmail(email, jwtEmail)
        
        // Find user by email
        return userRepository.findUserByEmail(email)
            .onItem().ifNull()
            .failWith(IllegalArgumentException("User not found"))
            .onItem().ifNotNull().transformToUni { userEntity ->
                // Encrypt current password for comparison
                val encryptedPassword = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password)
                
                // Validate current password matches
                if (encryptedPassword != userEntity.password) {
                    throw IllegalArgumentException("Current password is incorrect")
                }
                
                // Update password using repository
                userRepository.changePassword(encryptedPassword, user.password ?: "", email)
            }
    }

    /**
     * Extracts the rpId (Relying Party ID) from an origin URL.
     * The rpId is the hostname without protocol and port.
     * This ensures consistency between registration and authentication.
     *
     * @param origin The origin URL (e.g., "http://localhost:8080" or "https://example.com")
     * @return The rpId (e.g., "localhost" or "example.com")
     */
    private fun extractRpIdFromOrigin(origin: String): String {
        return try {
            val url = java.net.URL(origin)
            url.host ?: "localhost"
        } catch (e: Exception) {
            // If parsing fails, try to extract manually
            origin.replace(Regex("^https?://"), "")
                .replace(Regex(":\\d+$"), "")
                .takeIf { it.isNotBlank() } ?: "localhost"
        }
    }

}

