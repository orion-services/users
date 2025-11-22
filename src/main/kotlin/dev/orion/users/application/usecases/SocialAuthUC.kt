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

import dev.orion.users.application.interfaces.SocialAuthUCI
import dev.orion.users.enterprise.model.User
import org.apache.commons.validator.routines.EmailValidator

class SocialAuthUC : SocialAuthUCI {

    /** Default blank arguments message. */
    private val BLANK = "Blank arguments"

    /** Default invalid arguments message. */
    private val INVALID = "Invalid arguments"

    /**
     * Validates social authentication data and creates a User object.
     *
     * @param email The email from the social provider
     * @param name The name from the social provider
     * @param provider The provider name (e.g., "google")
     * @return A User object with validated data
     * @throws IllegalArgumentException if the data is invalid
     */
    override fun validateSocialAuth(email: String, name: String, provider: String): User {
        // Validate email is not blank
        if (email.isBlank()) {
            throw IllegalArgumentException("$BLANK: email cannot be blank")
        }

        // Validate email format
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException("$INVALID: invalid email format")
        }

        // Validate name is not blank
        if (name.isBlank()) {
            throw IllegalArgumentException("$BLANK: name cannot be blank")
        }

        // Validate provider
        if (provider.isBlank() || provider != "google") {
            throw IllegalArgumentException("$INVALID: provider must be 'google'")
        }

        // Create user object (password will be null for social auth users)
        val user = User()
        user.email = email
        user.name = name
        user.password = null // Social auth users don't have passwords
        user.emailValid = true // Social providers already validated the email

        return user
    }
}

