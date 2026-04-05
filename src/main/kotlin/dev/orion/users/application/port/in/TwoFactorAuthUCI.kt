/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.port.`in`

import dev.orion.users.domain.model.User

interface TwoFactorAuthUCI {
    fun generateQRCode(email: String, password: String): User

    fun validateCode(email: String, code: String): User
}
