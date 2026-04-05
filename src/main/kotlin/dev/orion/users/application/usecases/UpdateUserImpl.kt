/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.usecases

import dev.orion.users.application.port.`in`.UpdateUser
import dev.orion.users.application.utils.PasswordValidator
import dev.orion.users.domain.model.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.validator.routines.EmailValidator

@ApplicationScoped
open class UpdateUserImpl
    @Inject
    constructor() : UpdateUser {
        private val blank = "Blank Arguments"

        override fun updateUser(
            email: String,
            name: String?,
            newEmail: String?,
            password: String?,
            newPassword: String?,
        ): User {
            if (email.isBlank()) {
                throw IllegalArgumentException(blank)
            }
            if (name.isNullOrBlank() && newEmail.isNullOrBlank() && newPassword.isNullOrBlank()) {
                throw IllegalArgumentException("At least one field (name, newEmail or newPassword) must be provided for update")
            }
            if (!EmailValidator.getInstance().isValid(email)) {
                throw IllegalArgumentException("Invalid current email format")
            }
            val user = User()
            user.email = email
            if (!name.isNullOrBlank()) {
                if (name.trim().isEmpty()) {
                    throw IllegalArgumentException("Name cannot be empty")
                }
                user.name = name.trim()
            }
            if (!newEmail.isNullOrBlank()) {
                if (!EmailValidator.getInstance().isValid(newEmail)) {
                    throw IllegalArgumentException("Invalid new email format")
                }
                user.email = newEmail
                user.emailValid = false
            }
            if (!newPassword.isNullOrBlank()) {
                if (password.isNullOrBlank()) {
                    throw IllegalArgumentException("Current password is required when updating password")
                }
                PasswordValidator.validatePasswordOrThrow(newPassword)
                user.password = encryptPassword(newPassword)
            }
            return user
        }

        private fun encryptPassword(password: String): String = DigestUtils.sha256Hex(password)
    }
