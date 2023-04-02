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
import javax.ws.rs.NotFoundException;

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

    /** Default blanck arguments message. */
    private static final String BLANK = "Blank Arguments";

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
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email)
                || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Blank arguments or invalid e-mail");
        } else {
            if (password.length() < SIZE_PASSWORD) {
                throw new IllegalArgumentException(
                        "Password less than eight characters");
            } else {
                User user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setPassword(DigestUtils.sha256Hex(password));
                user.setEmailValid(false);
                return repository.createUser(user);
            }
        }
    }

    /**
     * Creates a user in the service (UC: Authenticate With Google).
     *
     * @param name         : The name of the user
     * @param email        : The e-mail of the user
     * @param isEmailValid : Informs if the e-mail is valid
     * @return An Uni<User> object
     */
    @Override
    public Uni<User> createUser(final String name, final String email,
            final Boolean isEmailValid) {
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(
                    "Blank arguments or invalid e-mail");
        } else {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setEmailValid(isEmailValid);
            return repository.createUser(user);
        }
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
        if (email != null && password != null) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(DigestUtils.sha256Hex(password));
            return repository.authenticate(user);
        } else {
            throw new IllegalArgumentException("All arguments are required");
        }
    }

    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     * @return An Uni<User> object
     */
    @Override
    public Uni<User> updateEmail(final String email, final String newEmail) {
        Uni<User> user = null;
        if (email.isBlank() || newEmail.isBlank()) {
            throw new IllegalArgumentException(BLANK);
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
        if (password.isBlank() || newPassword.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            return repository.changePassword(DigestUtils.sha256Hex(password),
                    DigestUtils.sha256Hex(newPassword), email);
        }
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
        if (email.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            return repository.recoverPassword(email);
        }
    }

    /**
     * Deletes a User from the service.
     *
     * @param email : User email
     *
     * @return Return 1 if user was deleted
     */
    @Override
    public Uni<Void> deleteUser(final String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email can not be blank");
        } else {
            return repository.deleteUser(email);
        }
    }

    /**
     * Validates an e-mail of a user.
     *
     * @param email : The e-mail of a user
     * @param code  : The validation code
     * @return true if the validation code is correct for the respective e-mail
     */
    public Uni<User> validateEmail(final String email, final String code) {
        if (email.isBlank() || code.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            return repository.validateEmail(email, code);
        }
    }

    @Override
    public Uni<User> findUserByEmail(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        }
        return repository.findUserByEmail(email);
    }

    @Override
    public Uni<User> updateUser(User user) {
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return repository.updateUser(user);
    }

}
