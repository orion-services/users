/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.port.`in`

import dev.orion.users.domain.model.User

interface UpdateUser {
    fun updateUser(email: String, name: String?, newEmail: String?, password: String?, newPassword: String?): User
}
