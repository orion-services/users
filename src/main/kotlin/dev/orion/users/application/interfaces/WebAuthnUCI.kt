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

/**
 * Interface for WebAuthn use cases.
 */
interface WebAuthnUCI {
    /**
     * Starts the WebAuthn registration process.
     *
     * @param email The email of the user
     * @return A JSON string containing PublicKeyCredentialCreationOptions
     */
    fun startRegistration(email: String): String

    /**
     * Finishes the WebAuthn registration process.
     *
     * @param email    The email of the user
     * @param response The registration response from the client (JSON string)
     * @param deviceName Optional name for the device
     * @return true if registration was successful
     */
    fun finishRegistration(email: String, response: String, deviceName: String?): Boolean

    /**
     * Starts the WebAuthn authentication process.
     *
     * @param email The email of the user
     * @return A JSON string containing PublicKeyCredentialRequestOptions
     */
    fun startAuthentication(email: String): String

    /**
     * Finishes the WebAuthn authentication process.
     *
     * @param email    The email of the user
     * @param response The authentication response from the client (JSON string)
     * @return true if authentication was successful
     */
    fun finishAuthentication(email: String, response: String): Boolean
}

