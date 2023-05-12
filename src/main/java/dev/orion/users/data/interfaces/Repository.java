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
package dev.orion.users.data.interfaces;

import jakarta.enterprise.context.ApplicationScoped;
import dev.orion.users.domain.model.User;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

/**
 * User repository interface.
 */
@ApplicationScoped
public interface Repository extends PanacheRepository<User> {

    /**
     * Creates a user in the service.
     *
     * @param user : An user object
     * @return A Uni<User> object
     */
    Uni<User> createUser(User user);

    Uni<User> findUserByEmail(String email);

    /**
     * Returns a user searching for email and password.
     *
     * @param user : The user object
     * @return A Uni<User> object
     */
    Uni<User> authenticate(User user);

    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     *
     * @return A Uni<User> object
     */
    Uni<User> updateEmail(String email, String newEmail);

    Uni<User> updateUser(User user);

    /**
     * Validates an e-mail of a user.
     *
     * @param email : The e-mail of a user
     * @param code  : The validation code
     * @return true if the validation code is correct for the respective e-mail
     */
    Uni<User> validateEmail(String email, String code);

    /**
     * Changes User password.
     *
     * @param password    : Actual password
     * @param newPassword : New Password
     * @param email       : User's email
     * @return A Uni<User> object
     */
    Uni<User> changePassword(String password, String newPassword, String email);

    /**
     * Generates a new password of a user.
     *
     * @param email : The e-mail of the user
     * @return A new password
     * @throws IllegalArgumentException if the user informs a wrong e-mail
     */
    Uni<String> recoverPassword(String email);

    /**
     * Deletes a User from the service.
     *
     * @param email : User e-mail
     * @return Returns a Long 1 if user was deleted
     */
    Uni<Void> deleteUser(String email);

}
