/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.orion.users.application.usecases

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.validator.routines.EmailValidator

import dev.orion.users.application.interfaces.UpdateUser
import dev.orion.users.application.utils.PasswordValidator
import dev.orion.users.enterprise.model.User

class UpdateUserImpl : UpdateUser {

    /** Default blank arguments message. */
    private val BLANK = "Blank Arguments"

    /**
     * Updates user information (name, email and/or password).
     * At least one field must be provided for update.
     *
     * @param email       : Current user's email
     * @param name        : New name (optional)
     * @param newEmail    : New email (optional)
     * @param password    : Current password (required if updating password)
     * @param newPassword : New password (optional)
     * @return An User object with updated fields
     * @throws IllegalArgumentException if no fields are provided for update or validation fails
     */
    override fun updateUser(email: String, name: String?, newEmail: String?, password: String?, newPassword: String?): User {
        if (email.isBlank()) {
            throw IllegalArgumentException(BLANK)
        }
        
        // Validate that at least one field is being updated
        if (name.isNullOrBlank() && newEmail.isNullOrBlank() && newPassword.isNullOrBlank()) {
            throw IllegalArgumentException("At least one field (name, newEmail or newPassword) must be provided for update")
        }
        
        // Validate current email format
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException("Invalid current email format")
        }
        
        val user = User()
        user.email = email
        
        // Update name if provided
        if (!name.isNullOrBlank()) {
            if (name.trim().isEmpty()) {
                throw IllegalArgumentException("Name cannot be empty")
            }
            user.name = name.trim()
        }
        
        // Update email if provided
        if (!newEmail.isNullOrBlank()) {
            if (!EmailValidator.getInstance().isValid(newEmail)) {
                throw IllegalArgumentException("Invalid new email format")
            }
            user.email = newEmail
            user.emailValid = false
        }
        
        // Update password if provided
        if (!newPassword.isNullOrBlank()) {
            if (password.isNullOrBlank()) {
                throw IllegalArgumentException("Current password is required when updating password")
            }
            // Validate new password requirements
            PasswordValidator.validatePasswordOrThrow(newPassword)
            user.password = encryptPassword(newPassword)
        }
        
        return user
    }

    /**
     * Encrypts the password with SHA-256.
     *
     * @param password : The password to be encrypted
     * @return The encrypted password
     */
    private fun encryptPassword(password: String): String {
        return DigestUtils.sha256Hex(password)
    }
}

