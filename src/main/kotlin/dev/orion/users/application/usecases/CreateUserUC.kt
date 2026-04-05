/*
 * Copyright 2026 Orion Services.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.orion.users.application.usecases

import dev.orion.users.application.port.`in`.CreateUserUCI
import dev.orion.users.application.utils.PasswordValidator
import dev.orion.users.domain.model.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.validator.routines.EmailValidator

@ApplicationScoped
open class CreateUserUC
    @Inject
    constructor() : CreateUserUCI {
        override fun createUser(
            name: String,
            email: String,
            password: String,
        ): User {
            if (name.isEmpty() || !EmailValidator.getInstance().isValid(email) || password.isEmpty()) {
                throw IllegalArgumentException("Blank arguments or invalid e-mail")
            }
            PasswordValidator.validatePasswordOrThrow(password)
            val user = User()
            user.name = name
            user.email = email
            user.password = encryptPassword(password)
            user.emailValid = false
            return user
        }

        override fun createUser(
            name: String,
            email: String,
            isEmailValid: Boolean,
        ): User {
            if (name.isBlank() || !EmailValidator.getInstance().isValid(email)) {
                throw IllegalArgumentException("Blank arguments or invalid e-mail")
            }
            val user = User()
            user.name = name
            user.email = email
            user.emailValid = isEmailValid
            return user
        }

        private fun encryptPassword(password: String): String = DigestUtils.sha256Hex(password)
    }
