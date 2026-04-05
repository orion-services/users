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

import dev.orion.users.application.port.`in`.SocialAuthUCI
import dev.orion.users.domain.model.User
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.commons.validator.routines.EmailValidator

@ApplicationScoped
open class SocialAuthUC
    @Inject
    constructor() : SocialAuthUCI {
        override fun validateSocialAuth(
            email: String,
            name: String,
            provider: String,
        ): User {
            if (email.isBlank() || name.isBlank() || provider.isBlank()) {
                throw IllegalArgumentException("Email, name and provider cannot be blank")
            }
            if (!EmailValidator.getInstance().isValid(email)) {
                throw IllegalArgumentException("Invalid email format")
            }
            if (provider.lowercase() != "google") {
                throw IllegalArgumentException("Unsupported provider: $provider")
            }
            val user = User()
            user.email = email
            user.name = name
            user.emailValid = true
            return user
        }
    }
