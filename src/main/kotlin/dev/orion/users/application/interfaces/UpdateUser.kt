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

interface UpdateUser {
    /**
     * Updates user information (email and/or password).
     * At least one field must be provided for update.
     *
     * @param email       : Current user's email
     * @param newEmail    : New email (optional)
     * @param password    : Current password (required if updating password)
     * @param newPassword : New password (optional)
     * @return An User object with updated fields
     * @throws IllegalArgumentException if no fields are provided for update or validation fails
     */
    fun updateUser(email: String, newEmail: String?, password: String?, newPassword: String?): User
}

