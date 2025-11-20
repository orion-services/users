/**
 * @License
 * Copyright 2024 Orion Services @ https://orion-services.dev
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

interface UpdateUser {
    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     *
     * @return An User object
     */
    fun updateEmail(email: String, newEmail: String): User

    /**
     * Updates the user's password.
     *
     * @param email       : User's email
     * @param password    : Current password
     * @param newPassword : New Password
     *
     * @return An User object
     */
    fun updatePassword(email: String, password: String, newPassword: String): User

    /**
     * Updates a user.
     *
     * @param user A user object
     * @return An User object
     */
    fun updateUser(user: User): User
}

