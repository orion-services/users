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
package dev.orion.users.adapters.gateways.repository

import dev.orion.users.adapters.gateways.entities.RoleEntity
import dev.orion.users.adapters.gateways.entities.UserEntity
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase
import io.quarkus.panache.common.Parameters
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.apache.commons.codec.digest.DigestUtils
import org.passay.CharacterData
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator
import java.io.IOException

/**
 * Implementation of the UserRepository interface that provides methods for
 * creating, authenticating, updating, and deleting user entities in the
 * service.
 */
@ApplicationScoped
class UserRepositoryImpl : UserRepository {

    /** Setting the default role name. */
    private val DEFAULT_ROLE_NAME = "user"

    /** Default password length. */
    private val PASSWORD_LENGTH = 8

    /** Default user not found message. */
    private val USER_NOT_FOUND_ERROR = "Error: user not found"

    /** E-mail column. */
    private val EMAIL = "email"

    /** Password column. */
    private val PASSWORD = "password"

    /**
     * Creates a user in the service.
     *
     * @param u : A user object
     * @return Returns a user asynchronously
     */
    override fun createUser(u: UserEntity): Uni<UserEntity> {
        return checkEmail(u.email ?: "")
            .onItem().ifNotNull().transform { user -> user }
            .onItem().ifNull().switchTo {
                checkName(u.name ?: "")
                    .onItem().ifNotNull()
                    .failWith(IllegalArgumentException("The name already existis"))
                    .onItem().ifNull().switchTo {
                        checkHash(u.hash)
                            .onItem().ifNotNull()
                            .failWith(IllegalArgumentException("The hash already existis"))
                            .onItem().ifNull().switchTo {
                                if ((u.password ?: "").isBlank()) {
                                    u.password = generateSecurePassword()
                                }
                                persistUser(u)
                            }
                    }
            }
    }

    /**
     * Returns a user searching for e-mail and password.
     *
     * @param user : A user object
     * @return Uni<UserEntity> object
     */
    override fun authenticate(user: UserEntity): Uni<UserEntity> {
        val params = Parameters.with(EMAIL, user.email)
            .and(PASSWORD, user.password).map()
        val repo = this as io.quarkus.hibernate.reactive.panache.PanacheRepository<UserEntity>
        return repo.find("email = :email and password = :password", params)
            .firstResult<UserEntity>()
            .onItem().ifNotNull().transform { loadedUser: UserEntity -> loadedUser }
    }

    /**
     * Updates the user's e-mail.
     *
     * @param email    : User's email
     * @param newEmail : New User's Email
     * @return Uni<UserEntity> object
     */
    override fun updateEmail(email: String, newEmail: String): Uni<UserEntity> {
        return checkEmail(email)
            .onItem().ifNull()
            .failWith(IllegalArgumentException(USER_NOT_FOUND_ERROR))
            .onItem().ifNotNull()
            .transformToUni { user ->
                checkEmail(newEmail)
                    .onItem().ifNotNull()
                    .failWith(IllegalArgumentException("Email already in use"))
                    .onItem().ifNull()
                    .switchTo {
                        user.setEmailValidationCode()
                        user.emailValid = false
                        user.email = newEmail
                        Panache.withTransaction { user.persist() }
                            .onItem().transform { user }
                    }
            }
    }

    /**
     * Validates the user's e-mail, change the emailValid property to true
     * if the code is correct.
     *
     * @param email : User's email
     * @param code  : The validation code
     * @return Uni<UserEntity> object
     */
    override fun validateEmail(email: String, code: String): Uni<UserEntity> {
        val params = Parameters.with(EMAIL, email).and("code", code).map()
        val repo = this as io.quarkus.hibernate.reactive.panache.PanacheRepository<UserEntity>
        return repo.find("email = :email and emailValidationCode = :code", params)
            .firstResult<UserEntity>()
            .onItem().ifNotNull().transformToUni { user: UserEntity ->
                user.emailValid = true
                Panache.withTransaction { user.persist() }
                    .onItem().transform { user }
            }
            .onItem().ifNull()
            .failWith(IllegalArgumentException("Invalid e-mail or code"))
    }

    /**
     * Changes User password.
     *
     * @param password    : Actual password
     * @param newPassword : New Password
     * @param email       : User's email
     * @return Uni<UserEntity> object
     */
    override fun changePassword(password: String, newPassword: String, email: String): Uni<UserEntity> {
        return checkEmail(email)
            .onItem().ifNull()
            .failWith(IllegalArgumentException(USER_NOT_FOUND_ERROR))
            .onItem().ifNotNull()
            .transformToUni { user ->
                if (password == user.password) {
                    user.password = newPassword
                } else {
                    throw IllegalArgumentException("Passwords doesn't match")
                }
                Panache.withTransaction { user.persist() }
                    .onItem().transform { user }
            }
    }

    /**
     * Generates a new password of a user.
     *
     * @param email : The e-mail of the user
     * @return A new password
     * @throws IllegalArgumentException if the user informs a wrong e-mail
     */
    override fun recoverPassword(email: String): Uni<String> {
        val password = generateSecurePassword()
        return checkEmail(email)
            .onItem().ifNull()
            .failWith(IllegalArgumentException("E-mail not found"))
            .onItem().ifNotNull()
            .transformToUni { user ->
                changePassword(user.password ?: "", DigestUtils.sha256Hex(password), email)
                    .onItem().transform { password }
            }
    }

    /**
     * Deletes a User from the service.
     *
     * @param email : User email
     * @return Return 1 if user was deleted
     */
    override fun deleteUser(email: String): Uni<Void> {
        return checkEmail(email)
            .onItem().ifNull().failWith(IllegalArgumentException(USER_NOT_FOUND_ERROR))
            .onItem().ifNotNull().transformToUni { user ->
                Panache.withTransaction<Void> { user.delete() }
            }
    }

    /**
     * Verifies if the e-mail already exists in the database.
     *
     * @param email : An e-mail address
     *
     * @return Returns true if the e-mail already exists
     */
    private fun checkEmail(email: String): Uni<UserEntity> {
        return (this as io.quarkus.hibernate.reactive.panache.PanacheRepository<UserEntity>)
            .find(EMAIL, email).firstResult<UserEntity>()
    }

    /**
     * Verifies if the e-mail already exists in the database.
     *
     * @param email : An e-mail address
     * @return Returns true if the e-mail already exists
     */
    private fun checkName(email: String): Uni<UserEntity> {
        return (this as io.quarkus.hibernate.reactive.panache.PanacheRepository<UserEntity>)
            .find("name", email).firstResult<UserEntity>()
    }

    /**
     * Verifies if the hash already exists in the database.
     *
     * @param hash : A hash to identify an user
     * @return Returns true if the hash already exists
     */
    private fun checkHash(hash: String): Uni<UserEntity> {
        return (this as io.quarkus.hibernate.reactive.panache.PanacheRepository<UserEntity>)
            .find("hash", hash).firstResult<UserEntity>()
    }

    /**
     * Persists a user in the service with a default role (user).
     *
     * @param user : The user object
     * @return Uni<UserEntity> object
     */
    private fun persistUser(user: UserEntity): Uni<UserEntity> {
        return getDefaultRole()
            .onItem().ifNull()
            .failWith(IOException("Role not found"))
            .onItem().ifNotNull()
            .transformToUni { role ->
                user.addRole(role)
                Panache.withTransaction { user.persist() }
                    .onItem().transform { user }
            }
    }

    /**
     * Gets the default role "user" from the database.
     *
     * @return The Uni<Role> object of "user" role.
     */
    private fun getDefaultRole(): Uni<RoleEntity> {
        @Suppress("UNCHECKED_CAST")
        return PanacheEntityBase.find<RoleEntity>("name", DEFAULT_ROLE_NAME).firstResult<RoleEntity>()
    }

    /**
     * Generates a new Secure Password String.
     *
     * @return A new password
     */
    private fun generateSecurePassword(): String {
        // Character rule for lower case characters
        val lcr = CharacterRule(EnglishCharacterData.LowerCase)
        // Set the number of lower case characters
        lcr.numberOfCharacters = 2
        // Character rule for uppercase characters.
        val ucr = CharacterRule(EnglishCharacterData.UpperCase)
        // Set the number of upper case characters
        ucr.numberOfCharacters = 2

        // Character rule for digit characters
        val dr = CharacterRule(EnglishCharacterData.Digit)
        // Set the number of digit characters.
        dr.numberOfCharacters = 2

        // Character rule for special characters
        val special = defineSpecialChar("!@#$%^&*()_+")
        val sr = CharacterRule(special)
        // Set the number of special characters
        sr.numberOfCharacters = 2

        val passGen = PasswordGenerator()
        return passGen.generatePassword(PASSWORD_LENGTH, sr, lcr, ucr, dr)
    }

    /**
     * Define the Special Characters of the password.
     *
     * @param character : Special Characters String
     * @return CharacterData class of the Characters
     */
    private fun defineSpecialChar(character: String): CharacterData {
        return object : CharacterData {
            override fun getErrorCode(): String {
                return "Error"
            }

            override fun getCharacters(): String {
                return character
            }
        }
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to find
     * @return a Uni that emits the user entity if found, or completes empty if
     * not found
     */
    override fun findUserByEmail(email: String): Uni<UserEntity> {
        return (this as io.quarkus.hibernate.reactive.panache.PanacheRepository<UserEntity>)
            .find(EMAIL, email).firstResult<UserEntity>()
    }

    /**
     * Updates a user entity in the repository.
     *
     * @param user The user entity to be updated.
     * @return A Uni that emits the updated user entity.
     */
    override fun updateUser(user: UserEntity): Uni<UserEntity> {
        return Panache.withTransaction { user.persist() }
            .onItem().transform { user }
    }
}

