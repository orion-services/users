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
package dev.orion.users.application.utils

/**
 * Utility class for password validation.
 * Ensures passwords meet security requirements:
 * - Minimum 8 characters
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one special character
 */
object PasswordValidator {

    /** The minimum size of the password required. */
    private const val MIN_PASSWORD_LENGTH = 8

    /** Regex pattern for uppercase letters. */
    private val UPPERCASE_PATTERN = Regex("[A-Z]")

    /** Regex pattern for lowercase letters. */
    private val LOWERCASE_PATTERN = Regex("[a-z]")

    /** Regex pattern for special characters. */
    private val SPECIAL_CHAR_PATTERN = Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")

    /**
     * Validates if a password meets all security requirements.
     *
     * @param password The password to validate
     * @return PasswordValidationResult containing validation status and error messages
     */
    fun validatePassword(password: String): PasswordValidationResult {
        val errors = mutableListOf<String>()

        if (password.isEmpty()) {
            errors.add("Senha é obrigatória")
            return PasswordValidationResult(false, errors)
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            errors.add("Senha deve ter no mínimo 8 caracteres")
        }

        if (!UPPERCASE_PATTERN.containsMatchIn(password)) {
            errors.add("Senha deve conter pelo menos uma letra maiúscula")
        }

        if (!LOWERCASE_PATTERN.containsMatchIn(password)) {
            errors.add("Senha deve conter pelo menos uma letra minúscula")
        }

        if (!SPECIAL_CHAR_PATTERN.containsMatchIn(password)) {
            errors.add("Senha deve conter pelo menos um caractere especial")
        }

        return PasswordValidationResult(errors.isEmpty(), errors)
    }

    /**
     * Validates a password and throws an IllegalArgumentException if invalid.
     *
     * @param password The password to validate
     * @throws IllegalArgumentException if the password does not meet requirements
     */
    fun validatePasswordOrThrow(password: String) {
        val result = validatePassword(password)
        if (!result.isValid) {
            throw IllegalArgumentException(result.errors.joinToString("; "))
        }
    }
}

/**
 * Data class representing the result of password validation.
 *
 * @param isValid Whether the password meets all requirements
 * @param errors List of error messages describing validation failures
 */
data class PasswordValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

