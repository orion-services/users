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
package dev.orion.users.adapters.controllers

import dev.orion.users.adapters.gateways.entities.UserEntity
import dev.orion.users.adapters.gateways.repository.UserRepository
import dev.orion.users.adapters.presenters.AuthenticationDTO
import dev.orion.users.application.interfaces.AuthenticateUCI
import dev.orion.users.application.interfaces.CreateUserUCI
import dev.orion.users.application.usecases.AuthenticateUC
import dev.orion.users.application.usecases.CreateUserUC
import dev.orion.users.enterprise.model.User
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * The controller class.
 */
@ApplicationScoped
@WithSession
class UserController : BasicController() {

    /** Use cases for users. */
    private val createUC: CreateUserUCI = CreateUserUC()

    /** Use cases for authentication. */
    private val authenticationUC: AuthenticateUCI = AuthenticateUC()

    /** Persistence layer. */
    @Inject
    lateinit var userRepository: UserRepository

    /**
     * Create a new user. Validates the business rules, persists the user and
     * sends an e-mail to the user confirming the registration.
     *
     * @param name     : The user name
     * @param email    : The user e-mail
     * @param password : The user password
     * @return : Returns a Uni<UserEntity> object
     */
    fun createUser(name: String, email: String, password: String): Uni<UserEntity> {
        val user: User = createUC.createUser(name, email, password)
        val entity: UserEntity = mapper.map(user, UserEntity::class.java)
        return userRepository.createUser(entity)
            .onItem().ifNotNull().transform { u -> u }
            .onItem().ifNotNull().call { user -> this.sendValidationEmail(user) }
    }

    /**
     * Validates the e-mail of a user.
     *
     * @param email : The e-mail of the user
     * @param code  : The validation code
     * @return : Returns a Uni<UserEntity> object
     */
    fun validateEmail(email: String, code: String): Uni<UserEntity>? {
        var result: Uni<UserEntity>? = null
        if (authenticationUC.validateEmail(email, code) == true) {
            result = userRepository.validateEmail(email, code)
        }
        return result
    }

    /**
     * Authenticates the user in the service.
     *
     * @param email    : The user e-mail
     * @param password : The user password
     * @return : Returns a JSON Web Token (JWT)
     */
    fun authenticate(email: String, password: String): Uni<String> {
        // Creates a user in the model to encrypts the password and
        // converts it to an entity
        val entity: UserEntity = mapper.map(
            authenticationUC.authenticate(email, password),
            UserEntity::class.java
        )

        // Finds the user in the service through email and password and
        // generates a JWT
        return userRepository.authenticate(entity)
            .onItem().ifNotNull()
            .transform { this.generateJWT(it) }
    }

    /**
     * Authenticates a user with the provided email and password.
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @return a Uni object that emits an AuthenticationDTO if the
     * authentication is successful
     */
    fun login(email: String, password: String): Uni<AuthenticationDTO> {
        // Creates a user in the model to encrypts the password and
        // converts it to an entity
        val entity: UserEntity = mapper.map(
            authenticationUC.authenticate(email, password),
            UserEntity::class.java
        )

        return userRepository.authenticate(entity)
            .onItem().ifNotNull().transform { user ->
                val dto = AuthenticationDTO()
                dto.token = this.generateJWT(user)
                dto.user = user
                dto
            }
    }

    /**
     * Creates a user, generates a Json Web Token and returns a
     * AuthenticationDTO object.
     *
     * @param name     : The user name
     * @param email    : The user e-mail
     * @param password : The user password
     * @return A Uni<AuthenticationDTO> object
     */
    fun createAuthenticate(name: String, email: String, password: String): Uni<AuthenticationDTO> {
        return this.createUser(name, email, password)
            .onItem().ifNotNull().transform { user ->
                val dto = AuthenticationDTO()
                dto.token = this.generateJWT(user)
                dto.user = user
                dto
            }
    }

    /**
     * Delete a user from the service.
     *
     * @param email The user's e-mail
     * @return A Uni<Void> object
     */
    fun deleteUser(email: String): Uni<Void> {
        return userRepository.deleteUser(email)
    }
}

