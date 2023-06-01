/**
 * @License
 * Copyright 2023 Orion Services @ https://github.com/orion-services
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

import dev.orion.users.application.interfaces.UpdateUser;
import dev.orion.users.enterprise.model.User;

public class UpdateUserImpl implements UpdateUser {

    /** Default blanck arguments message. */
    private static final String BLANK = "Blank Arguments";

    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     * @return An User object
     */
    @Override
    public User updateEmail(final String email, final String newEmail) {
        User user = null;
        if (email.isBlank() || newEmail.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            //user = repository.updateEmail(email, newEmail);
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
    public User updatePassword(final String email, final String password,
            final String newPassword) {
        if (password.isBlank() || newPassword.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            // return repository.changePassword(DigestUtils.sha256Hex(password),
            //         DigestUtils.sha256Hex(newPassword), email);
            return null;
        }
    }

    @Override
    public User updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(BLANK);
        }
        return user;
    }

}
