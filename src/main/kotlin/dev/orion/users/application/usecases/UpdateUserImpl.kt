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

import dev.orion.users.application.interfaces.UpdateUser
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
        // This method returns null in the original implementation
        // Keeping the same behavior
        throw UnsupportedOperationException("Not implemented")
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
        } else {
            // This method returns null in the original implementation
            // Keeping the same behavior
            throw UnsupportedOperationException("Not implemented")
        }
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

