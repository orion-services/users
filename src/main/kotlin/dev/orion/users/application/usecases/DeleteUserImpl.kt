/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.usecases

import dev.orion.users.application.port.`in`.DeleteUser
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
open class DeleteUserImpl @Inject constructor() : DeleteUser {

    override fun deleteUser(email: String): Boolean {
        if (email.isBlank()) {
            throw IllegalArgumentException("Email can not be blank")
        }
        return true
    }
}
