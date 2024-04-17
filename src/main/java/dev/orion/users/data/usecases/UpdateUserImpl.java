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
package dev.orion.users.data.usecases;

import org.apache.commons.codec.digest.DigestUtils;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.usecases.UpdateUser;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class UpdateUserImpl implements UpdateUser {
    /** Default blanck arguments message. */
    private static final String BLANK = "Blank Arguments";

    @Inject
    protected UserRepository repository;

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
     * Updates the user's data.
     *
     * @param user : The user object
     * @return A Uni<User> object
     */
    @Override
    public Uni<User> updateUser(final User user) {
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return repository.updateUser(user);
    }

}
