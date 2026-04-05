/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.port.`in`

interface DeleteUser {
    fun deleteUser(email: String): Boolean
}
