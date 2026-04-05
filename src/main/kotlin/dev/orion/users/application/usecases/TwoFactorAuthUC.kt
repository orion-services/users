/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.usecases

import dev.orion.users.application.port.`in`.TwoFactorAuthUCI
import dev.orion.users.application.utils.PasswordValidator
import dev.orion.users.domain.model.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.validator.routines.EmailValidator

@ApplicationScoped
open class TwoFactorAuthUC
    @Inject
    constructor() : TwoFactorAuthUCI {
        private val blank = "Blank arguments"
        private val invalid = "Invalid arguments"

        override fun generateQRCode(
            email: String,
            password: String,
        ): User {
            if (email.isBlank() || password.isBlank()) {
                throw IllegalArgumentException(BLANK)
            }
            if (!EmailValidator.getInstance().isValid(email)) {
                throw IllegalArgumentException(INVALID)
            }
            PasswordValidator.validatePasswordOrThrow(password)
            val user = User()
            user.email = email
            user.password = DigestUtils.sha256Hex(password)
            return user
        }

        override fun validateCode(
            email: String,
            code: String,
        ): User {
            if (email.isBlank() || code.isBlank()) {
                throw IllegalArgumentException(BLANK)
            }
            if (!EmailValidator.getInstance().isValid(email)) {
                throw IllegalArgumentException(INVALID)
            }
            if (!code.matches(Regex("\\d{6}"))) {
                throw IllegalArgumentException("Invalid TOTP code format")
            }
            val user = User()
            user.email = email
            return user
        }
    }
