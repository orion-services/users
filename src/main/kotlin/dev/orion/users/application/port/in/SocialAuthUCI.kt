/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.port.`in`

import dev.orion.users.domain.model.User

interface SocialAuthUCI {
    fun validateSocialAuth(email: String, name: String, provider: String): User
}
