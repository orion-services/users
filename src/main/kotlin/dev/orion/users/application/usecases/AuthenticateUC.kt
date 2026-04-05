/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.usecases

import dev.orion.users.application.port.`in`.AuthenticateUCI
import dev.orion.users.domain.model.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.codec.digest.DigestUtils

@ApplicationScoped
open class AuthenticateUC
    @Inject
    constructor() : AuthenticateUCI {
        private val blank = "Blank arguments"
        private val invalid = "Invalid arguments"

        override fun authenticate(
            email: String,
            password: String,
        ): User {
            if (email.isNotEmpty() && password.isNotEmpty() && password.length >= 8) {
                val user = User()
                user.email = email
                user.password = DigestUtils.sha256Hex(password)
                return user
            } else {
                throw IllegalArgumentException(invalid)
            }
        }

        override fun requireEmailValidationParams(
            email: String,
            code: String,
        ) {
            if (email.isBlank() || code.isBlank()) {
                throw IllegalArgumentException(blank)
            }
        }

        override fun recoverPassword(email: String): String? {
            if (email.isBlank()) {
                throw IllegalArgumentException(blank)
            }
            return null
        }
    }
