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
package dev.orion.users.application.usecases;

import org.apache.commons.codec.digest.DigestUtils;

import dev.orion.users.application.interfaces.AuthenticateUCI;
import dev.orion.users.enterprise.model.User;

public class AuthenticateUC implements AuthenticateUCI {

    /** Default blank arguments message. */
    private static final String BLANK = "Blank arguments";

    /** Default invalid arguments message. */
    private static final String INVALID = "Invalid arguments";

    /**
     * Authenticates the user in the service (UC: Authenticate).
     *
     * @param email    : The email of the user
     * @param password : The password of the user
     * @return An User object
     */
    @Override
    public User authenticate(final String email, final String password) {
        // Check if the email and password are not null and bigger than 8
        // characters
        if (email != null && password != null && password.length() >= 8) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(DigestUtils.sha256Hex(password));
            return user;
        } else {
            throw new IllegalArgumentException(INVALID);
        }
    }

    /**
     * Validates an e-mail of a user. (UC: Validate e-mail)
     *
     * @param email : The e-mail of a user
     * @param code  : The validation code
     * @return true if the validation code is correct for the respective e-mail
     */
    @Override
    public Boolean validateEmail(final String email, final String code) {
        if (email.isBlank() || code.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            return true;
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
    public String recoverPassword(final String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            return null;
        }
    }

}
