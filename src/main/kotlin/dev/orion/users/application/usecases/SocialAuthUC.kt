/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.usecases

import dev.orion.users.application.port.`in`.SocialAuthUCI
import dev.orion.users.domain.model.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.validator.routines.EmailValidator

@ApplicationScoped
open class SocialAuthUC @Inject constructor() : SocialAuthUCI {

    override fun validateSocialAuth(email: String, name: String, provider: String): User {
        if (email.isBlank() || name.isBlank() || provider.isBlank()) {
            throw IllegalArgumentException("Email, name and provider cannot be blank")
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException("Invalid email format")
        }
        if (provider.lowercase() != "google") {
            throw IllegalArgumentException("Unsupported provider: $provider")
        }
        val user = User()
        user.email = email
        user.name = name
        user.emailValid = true
        return user
    }
}
