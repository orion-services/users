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

import dev.orion.users.application.interfaces.CreateUserUCI
import dev.orion.users.enterprise.model.User

class CreateUserUC : CreateUserUCI {

    /** The minimum size of the password required. */
    private val SIZE_PASSWORD = 8

    /**
     * Creates a user in the service (UC: Create the user).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return An User object
     */
    override fun createUser(name: String, email: String, password: String): User {
        if (name.isEmpty() || !EmailValidator.getInstance().isValid(email) || password.isEmpty()) {
            throw IllegalArgumentException("Blank arguments or invalid e-mail")
        } else {
            if (password.length < SIZE_PASSWORD) {
                throw IllegalArgumentException("Password less than eight characters")
            } else {
                val user = User()
                user.name = name
                user.email = email
                user.password = encryptPassword(password)
                user.emailValid = false
                return user
            }
        }
    }

    /**
     * Creates a user in the service (UC: Authenticate With Google).
     *
     * @param name         : The name of the user
     * @param email        : The e-mail of the user
     * @param isEmailValid : Informs if the e-mail is valid
     * @return An User object
     */
    override fun createUser(name: String, email: String, isEmailValid: Boolean): User {
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email)) {
            throw IllegalArgumentException("Blank arguments or invalid e-mail")
        } else {
            val user = User()
            user.name = name
            user.email = email
            user.emailValid = isEmailValid
            return user
        }
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

