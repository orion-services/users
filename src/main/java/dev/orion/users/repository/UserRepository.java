/**
 * @License
 * Copyright 2022 Orion Services @ https://github.com/orion-services
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
package dev.orion.users.repository;

import java.io.IOException;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.commons.codec.digest.DigestUtils;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import dev.orion.users.model.Role;
import dev.orion.users.model.User;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;

/**
 * Implements the repository pattern for the user entity.
 */
@ApplicationScoped
public class UserRepository implements Repository {

    /** Setting the default role name. */
    private static final String DEFAULT_ROLE_NAME = "user";

    /** Default password length. */
    private static final int PASSWORD_LENGTH = 8;

    /** Default user not found message. */
    private static final String USER_NOT_FOUND_ERROR = "Error: user not found";

    /** E-mail column. */
    private static final String EMAIL = "email";

    /**
     * Creates a user in the service.
     *
     * @param u : A user object
     * @return Returns a user asynchronously
     */
    @Override
    public Uni<User> createUser(final User u) {
        return checkEmail(u.getEmail())
                .onItem().ifNotNull().transform(user -> user)
                .onItem().ifNull().switchTo(() -> {
                    return checkName(u.getName())
                            .onItem().ifNotNull()
                            .failWith(new IllegalArgumentException(
                                    "The name already existis"))
                            .onItem().ifNull().switchTo(() -> {
                                return checkHash(u.getHash())
                                        .onItem().ifNotNull()
                                        .failWith(new IllegalArgumentException(
                                                "The hash already existis"))
                                        .onItem().ifNull().switchTo(() -> {
                                            if (u.getPassword().isBlank()) {
                                                u.setPassword(generateSecurePassword());
                                            }
                                            return persistUser(u);
                                        });
                            });
                });
    }

    /**
     * Returns a user searching for e-mail and password.
     *
     * @param user : A user object
     * @return Uni<User> object
     */
    @Override
    public Uni<User> authenticate(final User user) {
        Map<String, Object> params = Parameters.with(EMAIL,
            user.getEmail()).and("password", user.getPassword()).map();
        return find("email = :email and password = :password", params)
                .firstResult();
    }

    /**
     * Updates the user's e-mail.
     *
     * @param email    : User's email
     * @param newEmail : New User's Email
     * @return Uni<User> object
     */
    @Override
    public Uni<User> updateEmail(
            final String email,
            final String newEmail) {
        return checkEmail(email)
            .onItem().ifNull()
                .failWith(new IllegalArgumentException(USER_NOT_FOUND_ERROR))
            .onItem().ifNotNull()
                .transformToUni(user -> {
                    return checkEmail(newEmail)
                            .onItem().ifNotNull()
                            .failWith(new IllegalArgumentException(
                                    "Email already in use"))
                            .onItem().ifNull()
                            .switchTo(() -> {
                                user.setEmailValidationCode();
                                user.setEmailValid(false);
                                user.setEmail(newEmail);
                                return Panache.<User>withTransaction(
                                        user::persist);
                            });
                });
    }

    /**
     * Validates the user's e-mail, change the emailValid property to true
     * if the code is correct.
     *
     * @param email : User's email
     * @param code  : The validation code
     * @return Uni<User> object
     */
    @Override
    public Uni<User> validateEmail(final String email, final String code) {
        Map<String, Object> params = Parameters.with(EMAIL,
        email).and("code", code).map();
        return find("email = :email and emailValidationCode = :code",
                params)
                .firstResult()
                .onItem().ifNotNull().transformToUni(user -> {
                    user.setEmailValid(true);
                    return Panache.<User>withTransaction(user::persist);
                })
                .onItem().ifNull()
                .failWith(new IllegalArgumentException(
                        "Invalid e-mail or code"));
    }

    /**
     * Changes User password.
     *
     * @param password    : Actual password
     * @param newPassword : New Password
     * @param email       : User's email
     * @return Uni<User> object
     */
    @Override
    public Uni<User> changePassword(
            final String password,
            final String newPassword,
            final String email) {
        return checkEmail(email)
            .onItem().ifNull()
                .failWith(new IllegalArgumentException(USER_NOT_FOUND_ERROR))
            .onItem().ifNotNull()
                .transformToUni(user -> {
                    if (password.equals(user.getPassword())) {
                        user.setPassword(newPassword);
                    } else {
                        throw new IllegalArgumentException(
                            "Passwords doesn't match");
                    }
                    return Panache.<User>withTransaction(user::persist);
                });
    }

    /**
     * Generates a new password of a user.
     *
     * @param email : The e-mail of the user
     * @return A new password
     * @throws IllegalArgumentException if the user informs a wrong e-mail
     */
    @Override
    public Uni<String> recoverPassword(final String email) {
        String password = generateSecurePassword();
        return checkEmail(email)
                .onItem().ifNull()
                .failWith(new IllegalArgumentException("E-mail not found"))
                .onItem().ifNotNull()
                .transformToUni(user -> changePassword(user.getPassword(),
                        DigestUtils.sha256Hex(password), email)
                        .onItem().transform(item -> {
                            return password;
                        }));
    }

    /**
     * Deletes a User from the service.
     *
     * @param email : User email
     * @return Return 1 if user was deleted
     */
    @Override
    public Uni<Void> deleteUser(final String email) {
        return checkEmail(email)
            .onItem().ifNull()
                .failWith(new IllegalArgumentException(USER_NOT_FOUND_ERROR))
            .onItem().ifNotNull()
                .transformToUni(user -> {
                    return Panache.<Void>withTransaction(user::delete);
                });
    }

    /**
     * Verifies if the e-mail already exists in the database.
     *
     * @param email : An e-mail address
     *
     * @return Returns true if the e-mail already exists
     */
    private Uni<User> checkEmail(final String email) {
        return find(EMAIL, email).firstResult();
    }

    /**
     * Verifies if the e-mail already exists in the database.
     *
     * @param email : An e-mail address
     * @return Returns true if the e-mail already exists
     */
    private Uni<User> checkName(final String email) {
        return find("name", email).firstResult();
    }

    /**
     * Verifies if the hash already exists in the database.
     *
     * @param hash : A hash to identify an user
     * @return Returns true if the hash already exists
     */
    private Uni<User> checkHash(final String hash) {
        return find("hash", hash).firstResult();
    }

    /**
     * Persists a user in the service with a default role (user).
     *
     * @param user : The user object
     * @return Uni<User> object
     */
    private Uni<User> persistUser(final User user) {
        return getDefaultRole()
                .onItem().ifNull()
                .failWith(new IOException("Role not found"))
            .onItem().ifNotNull()
                .transformToUni(role -> {
                    user.addRole(role);
                    return Panache.<User>withTransaction(user::persist);
                });
    }

    /**
     * Gets the default role "user" from the database.
     *
     * @return The Uni<Role> object of "user" role.
     */
    private Uni<Role> getDefaultRole() {
        return Role.find("name", DEFAULT_ROLE_NAME).firstResult();
    }

    /**
     * Generates a new Secure Password String.
     *
     * @return A new password
     */
    private static String generateSecurePassword() {
        // Character rule for lower case characters
        CharacterRule lcr = new CharacterRule(EnglishCharacterData.LowerCase);
        // Set the number of lower case characters
        lcr.setNumberOfCharacters(2);
        // Character rule for uppercase characters.
        CharacterRule ucr = new CharacterRule(EnglishCharacterData.UpperCase);
        // Set the number of upper case characters
        ucr.setNumberOfCharacters(2);

        // Character rule for digit characters
        CharacterRule dr = new CharacterRule(EnglishCharacterData.Digit);
        // Set the number of digit characters.
        dr.setNumberOfCharacters(2);

        // Character rule for special characters
        CharacterData special = defineSpecialChar("!@#$%^&*()_+");
        CharacterRule sr = new CharacterRule(special);
        // Set the number of special characters
        sr.setNumberOfCharacters(2);

        PasswordGenerator passGen = new PasswordGenerator();
        return passGen.generatePassword(PASSWORD_LENGTH, sr, lcr, ucr, dr);
    }

    /**
     * Define the Special Characters of the password.
     *
     * @param character : Special Characters String
     * @return CharacterData class of the Characters
     */
    private static CharacterData defineSpecialChar(final String character) {
        return new CharacterData() {

            @Override
            public String getErrorCode() {
                return "Error";
            }

            @Override
            public String getCharacters() {
                return character;
            }
        };
    }

    @Override
    public Uni<User> findUserByEmail(String email) {
        return find(EMAIL,email).firstResult();
    }

    @Override
    public Uni<User> updateUser(User user) {

        return Panache.<User>withTransaction(user::persist);
    }
}
