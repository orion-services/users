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

import org.apache.commons.validator.routines.EmailValidator

import dev.orion.users.application.interfaces.WebAuthnUCI

/**
 * Use case implementation for WebAuthn.
 * This is a basic implementation that validates input.
 * The actual WebAuthn processing will be done in the controller layer
 * where we have access to webauthn4j library.
 */
class WebAuthnUC : WebAuthnUCI {

    /** Default blank arguments message. */
    private val BLANK = "Blank arguments"

    /** Default invalid arguments message. */
    private val INVALID = "Invalid arguments"

    /**
     * Starts the WebAuthn registration process.
     *
     * @param email The email of the user
     * @return A JSON string containing PublicKeyCredentialCreationOptions
     * @throws IllegalArgumentException if email is invalid
     */
    override fun startRegistration(email: String): String {
        if (email.isBlank()) {
            throw IllegalArgumentException(BLANK)
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException(INVALID)
        }
        // The actual options will be generated in the controller
        // This method just validates the input
        return ""
    }

    /**
     * Finishes the WebAuthn registration process.
     *
     * @param email    The email of the user
     * @param response The registration response from the client (JSON string)
     * @param origin   The origin (complete site address) where the device was registered
     * @param deviceName Optional name for the device
     * @return true if registration was successful
     * @throws IllegalArgumentException if arguments are invalid
     */
    override fun finishRegistration(email: String, response: String, origin: String, deviceName: String?): Boolean {
        if (email.isBlank() || response.isBlank() || origin.isBlank()) {
            throw IllegalArgumentException(BLANK)
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException(INVALID)
        }
        // The actual validation will be done in the controller
        return true
    }

    /**
     * Starts the WebAuthn authentication process.
     *
     * @param email The email of the user
     * @return A JSON string containing PublicKeyCredentialRequestOptions
     * @throws IllegalArgumentException if email is invalid
     */
    override fun startAuthentication(email: String): String {
        if (email.isBlank()) {
            throw IllegalArgumentException(BLANK)
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException(INVALID)
        }
        // The actual options will be generated in the controller
        return ""
    }

    /**
     * Finishes the WebAuthn authentication process.
     *
     * @param email    The email of the user
     * @param response The authentication response from the client (JSON string)
     * @return true if authentication was successful
     * @throws IllegalArgumentException if arguments are invalid
     */
    override fun finishAuthentication(email: String, response: String): Boolean {
        if (email.isBlank() || response.isBlank()) {
            throw IllegalArgumentException(BLANK)
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException(INVALID)
        }
        // The actual validation will be done in the controller
        return true
    }
}

