/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.application.port.`in`

interface WebAuthnUCI {
    fun startRegistration(email: String): String

    fun finishRegistration(
        email: String,
        response: String,
        origin: String,
        deviceName: String?,
    ): Boolean

    fun startAuthentication(email: String): String

    fun finishAuthentication(
        email: String,
        response: String,
    ): Boolean
}
