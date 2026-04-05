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

import com.fasterxml.jackson.databind.ObjectMapper
import dev.orion.users.adapters.gateways.entities.UserEntity
import dev.orion.users.adapters.gateways.entities.WebAuthnCredentialEntity
import dev.orion.users.adapters.gateways.repository.WebAuthnCredentialRepository
import dev.orion.users.adapters.out.persistence.UserEntityMapper
import dev.orion.users.adapters.presenters.AuthenticationDTO
import dev.orion.users.adapters.presenters.LoginResponseDTO
import dev.orion.users.application.port.`in`.AuthenticateUCI
import dev.orion.users.application.port.`in`.CreateUserUCI
import dev.orion.users.application.port.`in`.SocialAuthUCI
import dev.orion.users.application.port.`in`.TwoFactorAuthUCI
import dev.orion.users.application.port.`in`.UpdateUser
import dev.orion.users.application.port.`in`.WebAuthnUCI
import dev.orion.users.application.port.out.UserPersistencePort
import dev.orion.users.domain.model.User
import dev.orion.users.frameworks.mail.MailTemplate
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom
import java.util.Base64
import java.util.UUID

/**
 * The controller class.
 */
@WithSession
@ApplicationScoped
class UserController : BasicController() {
    @Inject
    lateinit var createUC: CreateUserUCI

    @Inject
    lateinit var authenticationUC: AuthenticateUCI

    @Inject
    lateinit var socialAuthUC: SocialAuthUCI

    @Inject
    lateinit var twoFactorAuthUC: TwoFactorAuthUCI

    @Inject
    lateinit var webAuthnUC: WebAuthnUCI

    @Inject
    lateinit var updateUserUC: UpdateUser

    @Inject
    lateinit var userPersistence: UserPersistencePort

    @Inject
    lateinit var userEntityMapper: UserEntityMapper

    @Inject
    lateinit var webAuthnCredentialRepository: WebAuthnCredentialRepository

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
    fun createUser(
        name: String,
        email: String,
        password: String,
    ): Uni<UserEntity> =
        toUni {
            val user: User = createUC.createUser(name, email, password)
            val created = userPersistence.createUser(user)
            val entity = userEntityMapper.toEntity(created)
            sendValidationEmail(entity).awaitSuspending()
            entity
        }

    /**
     * Validates the e-mail of a user.
     *
     * @param email : The e-mail of the user
     * @param code  : The validation code
     * @return : Returns a Uni<UserEntity> object
     */
    fun validateEmail(
        email: String,
        code: String,
    ): Uni<UserEntity> =
        toUni {
            authenticationUC.requireEmailValidationParams(email, code)
            val validated = userPersistence.validateEmail(email, code)
            userEntityMapper.toEntity(validated)
        }

    /**
     * Authenticates the user in the service.
     *
     * @param email    : The user e-mail
     * @param password : The user password
     * @return : Returns a JSON Web Token (JWT)
     */
    fun authenticate(
        email: String,
        password: String,
    ): Uni<String> =
        toUni {
            val auth = authenticationUC.authenticate(email, password)
            val domain =
                userPersistence.authenticate(auth.email!!, auth.password!!)
                    ?: throw IllegalArgumentException("Invalid credentials")
            generateJWT(domain)
        }

    /**
     * Authenticates a user with the provided email and password.
     * If the user has 2FA enabled, returns a response indicating that 2FA code is required.
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @return a Uni object that emits a LoginResponseDTO
     */
    fun login(
        email: String,
        password: String,
    ): Uni<LoginResponseDTO> =
        toUni {
            val auth = authenticationUC.authenticate(email, password)
            val domain =
                userPersistence.authenticate(auth.email!!, auth.password!!)
                    ?: throw IllegalArgumentException("Invalid credentials")

            val response = LoginResponseDTO()
            if (domain.using2FA && domain.require2FAForBasicLogin) {
                response.requires2FA = true
                response.message = "2FA code required"
            } else {
                val dto = AuthenticationDTO()
                dto.token = generateJWT(domain)
                dto.user = userEntityMapper.toEntity(domain)
                response.authentication = dto
                response.requires2FA = false
            }
            response
        }

    /**
     * Creates a user, generates a Json Web Token and returns a
     * LoginResponseDTO object.
     *
     * @param name     : The user name
     * @param email    : The user e-mail
     * @param password : The user password
     * @return A Uni<LoginResponseDTO> object
     */
    fun createAuthenticate(
        name: String,
        email: String,
        password: String,
    ): Uni<LoginResponseDTO> =
        toUni {
            val entity = this@UserController.createUser(name, email, password).awaitSuspending()

            val authDto = AuthenticationDTO()
            authDto.token = generateJWT(entity)
            authDto.user = entity

            val response = LoginResponseDTO()
            response.authentication = authDto
            response.requires2FA = false
            response
        }

    /**
     * Authenticates a user with a social provider (Google).
     * If the user doesn't exist, creates it automatically.
     * If the user has 2FA enabled and requires it for social login, returns a response
     * indicating that 2FA code is required.
     *
     * @param email    The email from the social provider
     * @param name     The name from the social provider
     * @param provider The provider name ("google")
     * @return A Uni<LoginResponseDTO> object (may contain JWT or indicate 2FA is required)
     */
    fun loginWithSocialProvider(
        email: String,
        name: String,
        provider: String,
    ): Uni<LoginResponseDTO> =
        toUni {
            val socialUser: User = socialAuthUC.validateSocialAuth(email, name, provider)
            val existingDomain = userPersistence.findUserByEmail(email)

            if (existingDomain != null) {
                val response = LoginResponseDTO()
                if (existingDomain.using2FA && existingDomain.require2FAForSocialLogin) {
                    response.requires2FA = true
                    response.message = "2FA code required"
                } else {
                    val dto = AuthenticationDTO()
                    dto.token = generateJWT(existingDomain)
                    dto.user = userEntityMapper.toEntity(existingDomain)
                    response.authentication = dto
                    response.requires2FA = false
                }
                response
            } else {
                val newUser = User()
                newUser.name = socialUser.name
                newUser.email = socialUser.email
                newUser.emailValid = socialUser.emailValid
                newUser.password = DigestUtils.sha256Hex(UUID.randomUUID().toString())
                val newDomain = userPersistence.createUser(newUser)

                val response = LoginResponseDTO()
                val dto = AuthenticationDTO()
                dto.token = generateJWT(newDomain)
                dto.user = userEntityMapper.toEntity(newDomain)
                response.authentication = dto
                response.requires2FA = false
                response
            }
        }

    /**
     * Delete a user from the service.
     *
     * @param email The user's e-mail
     * @return A Uni<Void> object
     */
    fun deleteUser(email: String): Uni<Void> =
        toUniVoid {
            userPersistence.deleteUser(email)
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
    fun generate2FAQRCode(
        email: String,
        password: String,
    ): Uni<ByteArray> =
        toUni {
            val user: User = twoFactorAuthUC.generateQRCode(email, password)
            val authenticatedDomain =
                userPersistence.authenticate(user.email!!, user.password!!)
                    ?: throw IllegalArgumentException("Invalid credentials")

            val secretKey = generateSecretKey()
            authenticatedDomain.using2FA = true
            authenticatedDomain.secret2FA = secretKey

            val updatedDomain = userPersistence.updateUser(authenticatedDomain)
            val barCodeData = getAuthenticatorBarCode(secretKey, updatedDomain.email ?: email, issuer)
            createQrCode(barCodeData)
        }

    /**
     * Validates a TOTP code for 2FA authentication after social login.
     *
     * @param email The email of the user
     * @param code  The TOTP code to validate
     * @return A Uni that emits a LoginResponseDTO with JWT if validation succeeds
     */
    fun validateSocialLogin2FA(
        email: String,
        code: String,
    ): Uni<LoginResponseDTO> =
        toUni {
            twoFactorAuthUC.validateCode(email, code)

            val d =
                userPersistence.findUserByEmail(email)
                    ?: throw IllegalArgumentException("User not found")
            if (!d.using2FA) throw IllegalArgumentException("2FA is not enabled for this user")
            if (!d.require2FAForSocialLogin) throw IllegalArgumentException("2FA is not required for social login for this user")
            val secret = d.secret2FA ?: throw IllegalArgumentException("2FA secret not found")
            if (code != getTOTPCode(secret)) throw IllegalArgumentException("Invalid TOTP code")

            val authDto = AuthenticationDTO()
            authDto.token = generateJWT(d)
            authDto.user = userEntityMapper.toEntity(d)
            val response = LoginResponseDTO()
            response.authentication = authDto
            response.requires2FA = false
            response
        }

    /**
     * Validates a TOTP code for 2FA authentication.
     *
     * @param email The email of the user
     * @param code  The TOTP code to validate
     * @return A Uni that emits a LoginResponseDTO with JWT if validation succeeds
     */
    fun validate2FACode(
        email: String,
        code: String,
    ): Uni<LoginResponseDTO> =
        toUni {
            twoFactorAuthUC.validateCode(email, code)

            val d =
                userPersistence.findUserByEmail(email)
                    ?: throw IllegalArgumentException("User not found")
            if (!d.using2FA) throw IllegalArgumentException("2FA is not enabled for this user")
            val secret = d.secret2FA ?: throw IllegalArgumentException("2FA secret not found")
            if (code != getTOTPCode(secret)) throw IllegalArgumentException("Invalid TOTP code")

            val authDto = AuthenticationDTO()
            authDto.token = generateJWT(d)
            authDto.user = userEntityMapper.toEntity(d)
            val response = LoginResponseDTO()
            response.authentication = authDto
            response.requires2FA = false
            response
        }

    /**
     * Starts WebAuthn registration process.
     *
     * @param email The email of the user
     * @param origin Optional origin URL to extract rpId from
     * @return A JSON string containing PublicKeyCredentialCreationOptions
     */
    fun startWebAuthnRegistration(
        email: String,
        origin: String? = null,
    ): Uni<String> =
        toUni {
            webAuthnUC.startRegistration(email)

            val user =
                userPersistence.findUserByEmail(email)
                    ?: throw IllegalArgumentException("User not found")

            val challengeBytes = ByteArray(32)
            SecureRandom().nextBytes(challengeBytes)
            val challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes)
            val userId =
                Base64
                    .getUrlEncoder()
                    .withoutPadding()
                    .encodeToString((user.email ?: email).toByteArray())

            val rpName = issuer
            val rpId = origin?.let { extractRpIdFromOrigin(it) } ?: "localhost"
            val userName = user.email ?: email
            val userDisplayName = user.name ?: user.email ?: email

            val options =
                mapOf(
                    "rp" to mapOf("name" to rpName, "id" to rpId),
                    "user" to mapOf("id" to userId, "name" to userName, "displayName" to userDisplayName),
                    "challenge" to challenge,
                    "pubKeyCredParams" to
                        listOf(
                            mapOf("type" to "public-key", "alg" to -7),
                            mapOf("type" to "public-key", "alg" to -257),
                        ),
                    "authenticatorSelection" to
                        mapOf(
                            "authenticatorAttachment" to "platform",
                            "userVerification" to "preferred",
                        ),
                    "timeout" to 60000L,
                    "attestation" to "none",
                )

            objectMapper.writeValueAsString(mapOf("options" to options, "challenge" to challenge))
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
    fun finishWebAuthnRegistration(
        email: String,
        response: String,
        origin: String,
        deviceName: String?,
    ): Uni<Boolean> =
        toUni {
            webAuthnUC.finishRegistration(email, response, origin, deviceName)
            userPersistence.findUserByEmail(email)
                ?: throw IllegalArgumentException("User not found")

            val credentialEntity = WebAuthnCredentialEntity()
            credentialEntity.userEmail = email
            credentialEntity.credentialId = UUID.randomUUID().toString()
            credentialEntity.publicKey = response
            credentialEntity.counter = 0
            credentialEntity.origin = origin
            credentialEntity.notes = deviceName ?: "Unknown Device"
            credentialEntity.deviceName = deviceName ?: "Unknown Device"

            webAuthnCredentialRepository.saveCredential(credentialEntity).awaitSuspending()
            true
        }

    /**
     * Starts WebAuthn authentication process.
     *
     * @param email The email of the user
     * @return A JSON string containing PublicKeyCredentialRequestOptions
     */
    fun startWebAuthnAuthentication(email: String): Uni<String> =
        toUni {
            webAuthnUC.startAuthentication(email)
            userPersistence.findUserByEmail(email)
                ?: throw IllegalArgumentException("User not found")

            val credentials = webAuthnCredentialRepository.findByUserEmail(email).awaitSuspending()
            if (credentials.isNullOrEmpty()) {
                throw IllegalArgumentException("No WebAuthn credentials found for user")
            }

            val challengeBytes = ByteArray(32)
            SecureRandom().nextBytes(challengeBytes)
            val challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes)

            val allowCredentials =
                credentials.mapNotNull { cred ->
                    cred.credentialId?.let { id -> mapOf("type" to "public-key", "id" to id) }
                }
            val rpId = credentials.firstOrNull()?.origin?.let { extractRpIdFromOrigin(it) } ?: "localhost"

            val options =
                mapOf(
                    "challenge" to challenge,
                    "rpId" to rpId,
                    "allowCredentials" to allowCredentials,
                    "userVerification" to "preferred",
                    "timeout" to 60000L,
                )

            objectMapper.writeValueAsString(mapOf("options" to options, "challenge" to challenge))
        }

    /**
     * Finishes WebAuthn authentication process.
     *
     * @param email    The email of the user
     * @param response The authentication response from the client (JSON string)
     * @return A LoginResponseDTO with JWT if authentication succeeds
     */
    fun finishWebAuthnAuthentication(
        email: String,
        response: String,
    ): Uni<LoginResponseDTO> =
        toUni {
            webAuthnUC.finishAuthentication(email, response)

            val user =
                userPersistence.findUserByEmail(email)
                    ?: throw IllegalArgumentException("User not found")

            val credentials = webAuthnCredentialRepository.findByUserEmail(email).awaitSuspending()
            if (credentials.isNullOrEmpty()) {
                throw IllegalArgumentException("No WebAuthn credentials found")
            }

            val credential = credentials.first()
            credential.counter++
            webAuthnCredentialRepository.saveCredential(credential).awaitSuspending()

            val authDto = AuthenticationDTO()
            authDto.token = generateJWT(user)
            authDto.user = userEntityMapper.toEntity(user)

            val loginResponse = LoginResponseDTO()
            loginResponse.authentication = authDto
            loginResponse.requires2FA = false
            loginResponse
        }

    /**
     * Recovers the password of a user. Generates a new password, updates it in the database,
     * and sends it via email.
     *
     * @param email : The e-mail of the user
     * @return A Uni<Void> that completes when the password is recovered and email is sent
     */
    fun recoverPassword(email: String): Uni<Void> =
        toUniVoid {
            authenticationUC.recoverPassword(email)
            val newPassword = userPersistence.recoverPassword(email)
            sendRecoveryEmail(email, newPassword).awaitSuspending()
        }

    /**
     * Sends a recovery password email to the user.
     */
    private fun sendRecoveryEmail(
        email: String,
        password: String,
    ): Uni<Void> =
        MailTemplate
            .recoverPwd(password)
            .to(email)
            .subject("Recuperação de senha")
            .send()
            .onItem()
            .transform { null }

    /**
     * Updates user information (name, email and/or password). Validates the token,
     * updates the fields, generates a new JWT, and sends a validation email if email was changed.
     *
     * @param email       : The current email of the user
     * @param name        : The new name (optional)
     * @param newEmail    : The new email address (optional)
     * @param password    : The current password (required if updating password)
     * @param newPassword : The new password (optional)
     * @param jwtEmail    : The email from the JWT token (for validation)
     * @return A Uni<LoginResponseDTO> that emits the authentication response with token and user
     */
    fun updateUser(
        email: String,
        name: String?,
        newEmail: String?,
        password: String?,
        newPassword: String?,
        jwtEmail: String,
        isAdmin: Boolean = false,
    ): Uni<LoginResponseDTO> =
        toUni {
            updateUserUC.updateUser(email, name, newEmail, password, newPassword)
            if (!isAdmin) checkTokenEmail(email, jwtEmail)

            val nameUpdated = !name.isNullOrBlank()
            val emailUpdated = !newEmail.isNullOrBlank()
            val passwordUpdate = !newPassword.isNullOrBlank() && !password.isNullOrBlank()

            var userDomain =
                userPersistence.findUserByEmail(email)
                    ?: throw IllegalArgumentException("User not found")

            if (passwordUpdate) {
                val encryptedPassword = DigestUtils.sha256Hex(password)
                if (encryptedPassword != userDomain.password) {
                    throw IllegalArgumentException("Current password is incorrect")
                }
            }

            if (emailUpdated) {
                val updated = userPersistence.updateEmail(email, newEmail)
                sendValidationEmail(userEntityMapper.toEntity(updated)).awaitSuspending()
                userDomain = updated
            }

            if (nameUpdated) {
                userDomain.name = name
                userDomain = userPersistence.updateUser(userDomain)
            }

            if (passwordUpdate) {
                val encryptedPassword = DigestUtils.sha256Hex(password)
                val encryptedNewPassword = DigestUtils.sha256Hex(newPassword)
                val emailForPwdUpdate = userDomain.email ?: email
                userDomain = userPersistence.changePassword(encryptedPassword, encryptedNewPassword, emailForPwdUpdate)
            }

            val response = LoginResponseDTO()
            val dto = AuthenticationDTO()
            dto.token = generateJWT(userDomain)
            dto.user = userEntityMapper.toEntity(userDomain)
            response.authentication = dto
            response.requires2FA = false
            response
        }

    /**
     * Updates 2FA settings for a user.
     *
     * @param email                    The email of the user (from JWT)
     * @param require2FAForBasicLogin  Whether 2FA is required for basic login
     * @param require2FAForSocialLogin Whether 2FA is required for social login
     * @param jwtEmail                 The email from the JWT token (for validation)
     * @return A Uni<UserEntity> with updated settings
     */
    fun update2FASettings(
        email: String,
        require2FAForBasicLogin: Boolean,
        require2FAForSocialLogin: Boolean,
        jwtEmail: String,
    ): Uni<UserEntity> =
        toUni {
            checkTokenEmail(email, jwtEmail)
            val domain =
                userPersistence.findUserByEmail(email)
                    ?: throw IllegalArgumentException("User not found")
            domain.require2FAForBasicLogin = require2FAForBasicLogin
            domain.require2FAForSocialLogin = require2FAForSocialLogin
            val updated = userPersistence.updateUser(domain)
            userEntityMapper.toEntity(updated)
        }

    /**
     * Extracts the rpId (Relying Party ID) from an origin URL.
     */
    private fun extractRpIdFromOrigin(origin: String): String =
        try {
            val uri = java.net.URI(origin)
            uri.host ?: "localhost"
        } catch (e: Exception) {
            origin
                .replace(Regex("^https?://"), "")
                .replace(Regex(":\\d+$"), "")
                .takeIf { it.isNotBlank() } ?: "localhost"
        }

    /**
     * Lists all users in the service.
     *
     * @return A Uni<List<UserEntity>> containing all users
     */
    fun listAllUsers(): Uni<List<UserEntity>> =
        toUni {
            userPersistence.listAllUsers().map { userEntityMapper.toEntity(it) }
        }

    /**
     * Gets a user by email.
     *
     * @param email The email of the user
     * @return A Uni<UserEntity> containing the user if found
     */
    fun getUserByEmail(email: String): Uni<UserEntity> =
        toUni {
            val domain =
                userPersistence.findUserByEmail(email)
                    ?: throw IllegalArgumentException("User not found")
            userEntityMapper.toEntity(domain)
        }
}
