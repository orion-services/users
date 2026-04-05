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

import dev.orion.users.application.port.`in`.AuthenticateUCI
import dev.orion.users.domain.model.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.codec.digest.DigestUtils

@ApplicationScoped
open class AuthenticateUC
    @Inject
    constructor() : AuthenticateUCI {
        private val blank = "Blank arguments"
        private val invalid = "Invalid arguments"

        override fun authenticate(
            email: String,
            password: String,
        ): User {
            if (email.isNotEmpty() && password.isNotEmpty() && password.length >= 8) {
                val user = User()
                user.email = email
                user.password = DigestUtils.sha256Hex(password)
                return user
            } else {
                throw IllegalArgumentException(invalid)
            }
        }

        override fun requireEmailValidationParams(
            email: String,
            code: String,
        ) {
            if (email.isBlank() || code.isBlank()) {
                throw IllegalArgumentException(blank)
            }
        }

        override fun recoverPassword(email: String): String? {
            if (email.isBlank()) {
                throw IllegalArgumentException(blank)
            }
            return null
        }
    }
