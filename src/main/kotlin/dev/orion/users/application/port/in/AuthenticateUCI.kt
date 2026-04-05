/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.port.`in`

import dev.orion.users.domain.model.User

/** Inbound port: authentication use cases. */
interface AuthenticateUCI {
    fun authenticate(email: String, password: String): User

    /**
     * Precondition for email validation: non-blank email and code.
     * @throws IllegalArgumentException if invalid
     */
    fun requireEmailValidationParams(email: String, code: String)

    fun recoverPassword(email: String): String?
}
