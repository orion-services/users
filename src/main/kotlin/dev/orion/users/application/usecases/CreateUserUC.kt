/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.usecases

import dev.orion.users.application.port.`in`.CreateUserUCI
import dev.orion.users.application.utils.PasswordValidator
import dev.orion.users.domain.model.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.validator.routines.EmailValidator

@ApplicationScoped
open class CreateUserUC
    @Inject
    constructor() : CreateUserUCI {
        override fun createUser(
            name: String,
            email: String,
            password: String,
        ): User {
            if (name.isEmpty() || !EmailValidator.getInstance().isValid(email) || password.isEmpty()) {
                throw IllegalArgumentException("Blank arguments or invalid e-mail")
            }
            PasswordValidator.validatePasswordOrThrow(password)
            val user = User()
            user.name = name
            user.email = email
            user.password = encryptPassword(password)
            user.emailValid = false
            return user
        }

        override fun createUser(
            name: String,
            email: String,
            isEmailValid: Boolean,
        ): User {
            if (name.isBlank() || !EmailValidator.getInstance().isValid(email)) {
                throw IllegalArgumentException("Blank arguments or invalid e-mail")
            }
            val user = User()
            user.name = name
            user.email = email
            user.emailValid = isEmailValid
            return user
        }

        private fun encryptPassword(password: String): String = DigestUtils.sha256Hex(password)
    }
