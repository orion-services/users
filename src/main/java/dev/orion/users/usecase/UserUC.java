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
package dev.orion.users.usecase;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;

import dev.orion.users.model.User;
import dev.orion.users.repository.Repository;
import dev.orion.users.repository.UserRepository;
import io.smallrye.mutiny.Uni;

/**
 * Implements the use cases for user entity.
 */
@ApplicationScoped
public class UserUC implements UseCase {

    /** The minimum size of the password required. */
    private static final int SIZE_PASSWORD = 8;

    /** User repository. */
    private Repository repository = new UserRepository();

    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return An Uni<User> object
     */
    @Override
    public Uni<User> createUser(final String name, final String email,
            final String password) {
        Uni<User> user = null;
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email)
                || password.isBlank()) {
            throw new IllegalArgumentException("Blank arguments or invalid e-mail");
        } else {
            if (password.length() < SIZE_PASSWORD) {
                throw new IllegalArgumentException(
                        "Password less than eight characters");
            } else {
                user = repository.createUser(name, email,
                        DigestUtils.sha256Hex(password));
            }
        }
        return user;
    }

    /**
     * Authenticates the user in the service (UC: Authenticate).
     *
     * @param email    : The email of the user
     * @param password : The password of the user
     * @return An Uni<User> object
     */
    @Override
    public Uni<User> authenticate(final String email, final String password) {
        Uni<User> user = null;
        if ((email != null) && (password != null)) {
            user = repository.authenticate(email,
                    DigestUtils.sha256Hex(password));
        } else {
            throw new IllegalArgumentException("All arguments are required");
        }
        return user;
    }

    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     * @return An Uni<User> object
     */
    @Override
    public Uni<User> updateEmail(String email, String newEmail) {
        Uni<User> user = null;
        if (email.isBlank()
                || newEmail.isBlank()) {
            throw new IllegalArgumentException("Blank Arguments");
        } else {
            user = repository.updateEmail(email, newEmail);
        }
        return user;
    }

    /**
     * Changes User password.
     *
     * @param password    : Actual password
     * @param newPassword : New Password
     * @param email       : User's email
     * @return Returns a user asynchronously
     */
    @Override
    public Uni<User> updatePassword(final String email, final String password,
            final String newPassword) {
        Uni<User> user = null;
        if (password.isBlank() || newPassword.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException("Blank Arguments");
        } else {
            user = repository.changePassword(DigestUtils.sha256Hex(password),
                    DigestUtils.sha256Hex(newPassword), email);
        }
        return user;
    }

    /**
     * Generates a new password of a user.
     *
     * @param email : The e-mail of the user
     * @return A new password
     * @throws IllegalArgumentException if the user informs a blank e-mail
     */
    @Override
    public Uni<String> recoverPassword(final String email) {
        Uni<String> response = null;
        if (email.isBlank()) {
            throw new IllegalArgumentException("Blank Arguments");
        } else {
            response = repository.recoverPassword(email);
        }
        return response;
    }

    /**
     * Deletes a User from the service.
     *
     * @param email : User email
     *
     * @return Return 1 if user was deleted
     */
    @Override
    public Uni<Long> deleteUser(final String email) {
        Uni<Long> response = null;
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        } else {
            response = repository.deleteUser(email);
        }
        return response;
    }
}
