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
import javax.validation.Validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.routines.EmailValidator;

import dev.orion.users.model.User;
import dev.orion.users.repository.UserRepository;
import dev.orion.users.repository.Repository;
import io.smallrye.mutiny.Uni;
import lombok.val;

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
     * @return A Uni<User> object
     */
    @Override
    public Uni<User> createUser(final String name, final String email,
            final String password) {
        Uni<User> user = null;
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email) ||
                password.isBlank()) {
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
     * @return A Uni<User> object
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

}
