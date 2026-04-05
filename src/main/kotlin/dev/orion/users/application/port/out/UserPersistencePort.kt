/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.port.out

import dev.orion.users.domain.model.User

/**
 * Outbound port: persistence for users.
 * Pure Kotlin — no framework types in the contract.
 */
interface UserPersistencePort {
    suspend fun createUser(user: User): User

    suspend fun findUserByEmail(email: String): User?

    suspend fun authenticate(
        email: String,
        passwordHash: String,
    ): User?

    suspend fun updateEmail(
        email: String,
        newEmail: String,
    ): User

    suspend fun validateEmail(
        email: String,
        code: String,
    ): User

    suspend fun changePassword(
        password: String,
        newPassword: String,
        email: String,
    ): User

    suspend fun recoverPassword(email: String): String

    suspend fun deleteUser(email: String)

    suspend fun updateUser(user: User): User

    suspend fun listAllUsers(): List<User>
}
