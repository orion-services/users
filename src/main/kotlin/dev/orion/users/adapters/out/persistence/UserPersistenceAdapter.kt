/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.adapters.out.persistence

import dev.orion.users.adapters.gateways.entities.RoleEntity
import dev.orion.users.adapters.gateways.entities.UserEntity
import dev.orion.users.adapters.gateways.repository.RoleRepository
import dev.orion.users.application.port.out.UserPersistencePort
import dev.orion.users.domain.model.User
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.codec.digest.DigestUtils
import org.passay.CharacterData
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator
import java.io.IOException

/**
 * Panache implementation of [UserPersistencePort].
 * Suspend overrides delegate to internal Uni chains via [awaitSuspending].
 */
@ApplicationScoped
class UserPersistenceAdapter
    @Inject
    constructor(
        private val roleRepository: RoleRepository,
        private val mapper: UserEntityMapper,
    ) : PanacheRepository<UserEntity>,
        UserPersistencePort {
        private val defaultRoleName = "user"
        private val passwordLength = 8
        private val userNotFoundError = "Error: user not found"
        private val email = "email"
        private val password = "password"

        // ---- suspend overrides (port contract) ----

        override suspend fun createUser(user: User): User {
            val entity = mapper.toEntity(user)
            entity.id = null
            val persisted = createUserEntity(entity).awaitSuspending()
            return mapper.toDomain(persisted)
        }

        override suspend fun findUserByEmail(email: String): User? {
            val entity = findUserEntityByEmail(email).awaitSuspending()
            return entity?.let { mapper.toDomain(it) }
        }

        override suspend fun authenticate(
            email: String,
            passwordHash: String,
        ): User? {
            val entity = UserEntity()
            entity.email = email
            entity.password = passwordHash
            val result = authenticateEntity(entity).awaitSuspending()
            return result?.let { mapper.toDomain(it) }
        }

        override suspend fun updateEmail(
            email: String,
            newEmail: String,
        ): User {
            val result = updateEmailEntity(email, newEmail).awaitSuspending()
            return mapper.toDomain(result)
        }

        override suspend fun validateEmail(
            email: String,
            code: String,
        ): User {
            val result = validateEmailEntity(email, code).awaitSuspending()
            return mapper.toDomain(result)
        }

        override suspend fun changePassword(
            password: String,
            newPassword: String,
            email: String,
        ): User {
            val result = changePasswordEntity(password, newPassword, email).awaitSuspending()
            return mapper.toDomain(result)
        }

        override suspend fun recoverPassword(email: String): String = recoverPasswordEntity(email).awaitSuspending()

        override suspend fun deleteUser(email: String) {
            deleteUserEntity(email).awaitSuspending()
        }

        override suspend fun updateUser(user: User): User {
            val id = user.id
            if (id != null) {
                val entity =
                    findById(id)
                        .onItem()
                        .ifNull()
                        .failWith(IllegalArgumentException("User not found"))
                        .awaitSuspending()!!
                applyDomainScalarsToEntity(user, entity)
                val updated = updateUserEntity(entity).awaitSuspending()
                return mapper.toDomain(updated)
            }
            val entity = mapper.toEntity(user)
            val updated = updateUserEntity(entity).awaitSuspending()
            return mapper.toDomain(updated)
        }

        override suspend fun listAllUsers(): List<User> {
            val entities = listAllEntities().awaitSuspending()
            return entities.map { mapper.toDomain(it) }
        }

        // ---- private helpers (stay as Uni — Panache internals) ----

        private fun applyDomainScalarsToEntity(
            domain: User,
            entity: UserEntity,
        ) {
            entity.hash = domain.hash
            entity.name = domain.name
            entity.email = domain.email
            entity.password = domain.password
            entity.emailValid = domain.emailValid
            entity.emailValidationCode = domain.emailValidationCode
            entity.isUsing2FA = domain.using2FA
            entity.secret2FA = domain.secret2FA
            entity.require2FAForBasicLogin = domain.require2FAForBasicLogin
            entity.require2FAForSocialLogin = domain.require2FAForSocialLogin
        }

        private fun createUserEntity(u: UserEntity): Uni<UserEntity> =
            checkEmail(u.email ?: "")
                .onItem()
                .ifNotNull()
                .transform { user -> user!! }
                .onItem()
                .ifNull()
                .switchTo {
                    checkName(u.name ?: "")
                        .onItem()
                        .ifNotNull()
                        .failWith(IllegalArgumentException("The name already existis"))
                        .onItem()
                        .ifNull()
                        .switchTo {
                            checkHash(u.hash)
                                .onItem()
                                .ifNotNull()
                                .failWith(IllegalArgumentException("The hash already existis"))
                                .onItem()
                                .ifNull()
                                .switchTo {
                                    if ((u.password ?: "").isBlank()) {
                                        u.password = generateSecurePassword()
                                    }
                                    persistUser(u)
                                }
                        }
                }

        private fun authenticateEntity(user: UserEntity): Uni<UserEntity?> =
            find("email = :email and password = :password", mapOf(EMAIL to user.email, PASSWORD to user.password))
                .firstResult<UserEntity>()

        private fun updateEmailEntity(
            email: String,
            newEmail: String,
        ): Uni<UserEntity> =
            checkEmail(email)
                .onItem()
                .ifNull()
                .failWith(IllegalArgumentException(USER_NOT_FOUND_ERROR))
                .onItem()
                .ifNotNull()
                .transformToUni { user ->
                    val u = user!!
                    checkEmail(newEmail)
                        .onItem()
                        .ifNotNull()
                        .failWith(IllegalArgumentException("Email already in use"))
                        .onItem()
                        .ifNull()
                        .switchTo {
                            u.setEmailValidationCode()
                            u.emailValid = false
                            u.email = newEmail
                            Panache
                                .withTransaction { u.persist() }
                                .onItem()
                                .transform { u }
                        }
                }

        private fun validateEmailEntity(
            email: String,
            code: String,
        ): Uni<UserEntity> =
            find("email = :email and emailValidationCode = :code", mapOf(EMAIL to email, "code" to code))
                .firstResult<UserEntity>()
                .onItem()
                .ifNotNull()
                .transformToUni { user: UserEntity ->
                    user.emailValid = true
                    Panache
                        .withTransaction { user.persist() }
                        .onItem()
                        .transform { user }
                }.onItem()
                .ifNull()
                .failWith(IllegalArgumentException("Invalid e-mail or code"))

        private fun changePasswordEntity(
            password: String,
            newPassword: String,
            email: String,
        ): Uni<UserEntity> =
            checkEmail(email)
                .onItem()
                .ifNull()
                .failWith(IllegalArgumentException(USER_NOT_FOUND_ERROR))
                .onItem()
                .ifNotNull()
                .transformToUni { user ->
                    val u = user!!
                    if (password == u.password) {
                        u.password = newPassword
                    } else {
                        throw IllegalArgumentException("Passwords doesn't match")
                    }
                    Panache
                        .withTransaction { u.persist() }
                        .onItem()
                        .transform { u }
                }

        private fun recoverPasswordEntity(email: String): Uni<String> {
            val password = generateSecurePassword()
            val hashedPassword = DigestUtils.sha256Hex(password)
            return checkEmail(email)
                .onItem()
                .ifNull()
                .failWith(IllegalArgumentException("E-mail not found"))
                .onItem()
                .ifNotNull()
                .transformToUni { user ->
                    val u = user!!
                    u.password = hashedPassword
                    Panache
                        .withTransaction { u.persist() }
                        .onItem()
                        .transform { password }
                }
        }

        private fun deleteUserEntity(email: String): Uni<Void> =
            checkEmail(email)
                .onItem()
                .ifNull()
                .failWith(IllegalArgumentException(USER_NOT_FOUND_ERROR))
                .onItem()
                .ifNotNull()
                .transformToUni { user ->
                    Panache.withTransaction<Void> { user!!.delete() }
                }

        private fun updateUserEntity(user: UserEntity): Uni<UserEntity> =
            Panache
                .withTransaction { user.persist() }
                .onItem()
                .transform { user }

        private fun findUserEntityByEmail(email: String): Uni<UserEntity?> = find(EMAIL, email).firstResult<UserEntity>()

        private fun listAllEntities(): Uni<List<UserEntity>> = listAll()

        private fun checkEmail(email: String): Uni<UserEntity?> = find(EMAIL, email).firstResult<UserEntity>()

        private fun checkName(name: String): Uni<UserEntity?> = find("name", name).firstResult<UserEntity>()

        private fun checkHash(hash: String): Uni<UserEntity?> = find("hash", hash).firstResult<UserEntity>()

        private fun persistUser(user: UserEntity): Uni<UserEntity> =
            getDefaultRole()
                .onItem()
                .ifNull()
                .failWith(IOException("Role not found"))
                .onItem()
                .ifNotNull()
                .transformToUni { role ->
                    user.id = null
                    user.addRole(role)
                    Panache
                        .withTransaction { user.persist() }
                        .onItem()
                        .transform { user }
                }

        private fun getDefaultRole(): Uni<RoleEntity> = roleRepository.findByName(DEFAULT_ROLE_NAME)

        private fun generateSecurePassword(): String {
            val lcr = CharacterRule(EnglishCharacterData.LowerCase)
            lcr.numberOfCharacters = 1
            val ucr = CharacterRule(EnglishCharacterData.UpperCase)
            ucr.numberOfCharacters = 1
            val dr = CharacterRule(EnglishCharacterData.Digit)
            dr.numberOfCharacters = 1
            val specialChars = "!@#\$%^&*()_+-=\\[\\]{};':\"\\\\|,.<>/?"
            val special = defineSpecialChar(specialChars)
            val sr = CharacterRule(special)
            sr.numberOfCharacters = 1
            val passGen = PasswordGenerator()
            return passGen.generatePassword(PASSWORD_LENGTH, sr, lcr, ucr, dr)
        }

        private fun defineSpecialChar(character: String): CharacterData =
            object : CharacterData {
                override fun getErrorCode(): String = "Error"

                override fun getCharacters(): String = character
            }
    }
