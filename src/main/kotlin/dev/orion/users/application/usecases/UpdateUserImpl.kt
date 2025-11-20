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
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     * @return An User object
     */
    override fun updateEmail(email: String, newEmail: String): User {
        if (email.isBlank() || newEmail.isBlank()) {
            throw IllegalArgumentException(BLANK)
        }
        
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException("Invalid current email format")
        }
        
        if (!EmailValidator.getInstance().isValid(newEmail)) {
            throw IllegalArgumentException("Invalid new email format")
        }
        
        val user = User()
        user.email = newEmail
        user.emailValid = false
        return user
    }

    /**
     * Changes User password.
     *
     * @param password    : Actual password
     * @param newPassword : New Password
     * @param email       : User's email
     * @return Returns a user asynchronously
     */
    override fun updatePassword(email: String, password: String, newPassword: String): User {
        if (password.isBlank() || newPassword.isBlank() || email.isBlank()) {
            throw IllegalArgumentException(BLANK)
        }
        
        // Validate new password requirements
        PasswordValidator.validatePasswordOrThrow(newPassword)
        
        val user = User()
        user.email = email
        user.password = encryptPassword(newPassword)
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

    /**
     * Updates a user.
     *
     * @param user the user to be updated
     * @return the updated user
     * @throws IllegalArgumentException if the user is null
     */
    override fun updateUser(user: User): User {
        // In Kotlin, non-nullable types can't be null, so this check is not needed
        // but keeping for consistency with original code
        return user
    }
}

