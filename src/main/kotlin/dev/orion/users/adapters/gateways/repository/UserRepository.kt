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
package dev.orion.users.adapters.gateways.repository

import dev.orion.users.adapters.gateways.entities.UserEntity
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

/**
 * User repository interface.
 */
@ApplicationScoped
interface UserRepository : PanacheRepository<UserEntity> {

    /**
     * Creates a UserEntity in the service.
     *
     * @param user : An UserEntity object
     * @return A Uni<UserEntity> object
     */
    fun createUser(user: UserEntity): Uni<UserEntity>

    /**
     * Returns a user searching for email.
     *
     * @param email : The user e-mail
     * @return A Uni<UserEntity> object
     */
    fun findUserByEmail(email: String): Uni<UserEntity>

    /**
     * Returns a user searching for email and password.
     *
     * @param user : The user object
     * @return A Uni<UserEntity> object
     */
    fun authenticate(user: UserEntity): Uni<UserEntity>

    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     *
     * @return A Uni<UserEntity> object
     */
    fun updateEmail(email: String, newEmail: String): Uni<UserEntity>

    /**
     * Updates the user.
     * @param user : The user object
     * @return A Uni<UserEntity> object
     */
    fun updateUser(user: UserEntity): Uni<UserEntity>

    /**
     * Validates an e-mail of a user.
     *
     * @param email : The e-mail of a user
     * @param code  : The validation code
     * @return true if the validation code is correct for the respective e-mail
     */
    fun validateEmail(email: String, code: String): Uni<UserEntity>

    /**
     * Changes User password.
     *
     * @param password    : Actual password
     * @param newPassword : New Password
     * @param email       : User's email
     * @return A Uni<UserEntity> object
     */
    fun changePassword(password: String, newPassword: String, email: String): Uni<UserEntity>

    /**
     * Generates a new password of a user.
     *
     * @param email : The e-mail of the user
     * @return A new password
     * @throws IllegalArgumentException if the user informs a wrong e-mail
     */
    fun recoverPassword(email: String): Uni<String>

    /**
     * Deletes a User from the service.
     *
     * @param email : User e-mail
     * @return Returns a Long 1 if user was deleted
     */
    fun deleteUser(email: String): Uni<Void>

    /**
     * Lists all users in the service.
     *
     * @return A Uni<List<UserEntity>> containing all users
     */
    fun listAllUsers(): Uni<List<UserEntity>>
}

