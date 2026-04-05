/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.port.`in`

import dev.orion.users.domain.model.User

interface CreateUserUCI {
    fun createUser(name: String, email: String, password: String): User

    fun createUser(name: String, email: String, isEmailValid: Boolean): User
}
