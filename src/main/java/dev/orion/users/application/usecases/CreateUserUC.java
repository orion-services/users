/**
 * @License
 * Copyright 2024 Orion Services @ https://github.com/orion-services
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
package dev.orion.users.application.usecases;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;

import dev.orion.users.application.interfaces.CreateUserUCI;
import dev.orion.users.enterprise.model.User;

public class CreateUserUC implements CreateUserUCI {

    /** The minimum size of the password required. */
    private static final int SIZE_PASSWORD = 8;

    /**
     * Creates a user in the service (UC: Create the user).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return An User object
     */
    @Override
    public User createUser(final String name, final String email,
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
                // String secretKey = twoFactorAuthHandler.generateSecretKey();
                User user = new User();
                // user.setSecret2FA(secretKey);
                user.setName(name);
                user.setEmail(email);
                user.setPassword(encryptPassword(password));
                user.setEmailValid(false);
                return user;
            }
        }
    }

    /**
     * Creates a user in the service (UC: Authenticate With Google).
     *
     * @param name         : The name of the user
     * @param email        : The e-mail of the user
     * @param isEmailValid : Informs if the e-mail is valid
     * @return An User object
     */
    @Override
    public User createUser(final String name, final String email,
            final Boolean isEmailValid) {
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(
                    "Blank arguments or invalid e-mail");
        } else {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setEmailValid(isEmailValid);
            return user;
        }
    }

    /**
     * Encrypts the password with SHA-256.
     *
     * @param password : The password to be encrypted
     * @return The encrypted password
     */
    private String encryptPassword(final String password) {
        return DigestUtils.sha256Hex(password);
    }

}
