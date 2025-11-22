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
package dev.orion.users.adapters.presenters

import dev.orion.users.adapters.gateways.entities.UserEntity

/**
 * Authentication DTO.
 */
data class AuthenticationDTO(
    /** The user object. */
    var user: UserEntity? = null,
    /** The authentication token (jwt). */
    var token: String? = null
)

