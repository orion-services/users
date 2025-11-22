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

import dev.orion.users.application.interfaces.TwoFactorAuthUCI
import dev.orion.users.application.utils.PasswordValidator
import dev.orion.users.enterprise.model.User

/**
 * Use case implementation for Two Factor Authentication.
 */
class TwoFactorAuthUC : TwoFactorAuthUCI {

    /** Default blank arguments message. */
    private val BLANK = "Blank arguments"

    /** Default invalid arguments message. */
    private val INVALID = "Invalid arguments"

    /**
     * Generates a QR code for 2FA setup.
     * This method validates the user credentials and prepares the user for 2FA setup.
     *
     * @param email    The email of the user
     * @param password The password of the user
     * @return A User object with secret2FA set
     * @throws IllegalArgumentException if arguments are invalid
     */
    override fun generateQRCode(email: String, password: String): User {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException(BLANK)
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException(INVALID)
        }
        
        // Validate password requirements
        PasswordValidator.validatePasswordOrThrow(password)

        val user = User()
        user.email = email
        user.password = DigestUtils.sha256Hex(password)
        // The secret will be generated in the controller layer
        return user
    }

    /**
     * Validates a TOTP code for 2FA authentication.
     * This method validates the format of the code but actual TOTP validation
     * happens in the controller layer where we have access to the secret.
     *
     * @param email The email of the user
     * @param code  The TOTP code to validate (6 digits)
     * @return A User object if validation succeeds
     * @throws IllegalArgumentException if arguments are invalid
     */
    override fun validateCode(email: String, code: String): User {
        if (email.isBlank() || code.isBlank()) {
            throw IllegalArgumentException(BLANK)
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException(INVALID)
        }
        // TOTP codes are 6 digits
        if (!code.matches(Regex("\\d{6}"))) {
            throw IllegalArgumentException("Invalid TOTP code format")
        }

        val user = User()
        user.email = email
        return user
    }
}

