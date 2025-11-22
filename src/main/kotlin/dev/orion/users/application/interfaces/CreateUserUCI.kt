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

interface CreateUserUCI {
    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return A User object
     */
    fun createUser(name: String, email: String, password: String): User

    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name         : The name of the user
     * @param email        : The e-mail of the user
     * @param isEmailValid : Confirm if the e-mail is valid or not
     * @return A User object
     */
    fun createUser(name: String, email: String, isEmailValid: Boolean): User
}

