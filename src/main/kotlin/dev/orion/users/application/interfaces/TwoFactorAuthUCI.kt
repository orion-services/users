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
package dev.orion.users.application.interfaces

import dev.orion.users.enterprise.model.User

/**
 * Interface for Two Factor Authentication use cases.
 */
interface TwoFactorAuthUCI {
    /**
     * Generates a QR code for 2FA setup.
     *
     * @param email    The email of the user
     * @param password The password of the user
     * @return A User object with secret2FA set
     */
    fun generateQRCode(email: String, password: String): User

    /**
     * Validates a TOTP code for 2FA authentication.
     *
     * @param email The email of the user
     * @param code  The TOTP code to validate
     * @return A User object if validation succeeds
     */
    fun validateCode(email: String, code: String): User
}

