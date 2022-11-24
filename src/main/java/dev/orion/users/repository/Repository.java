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

import dev.orion.users.model.User;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

/**
 * User repository interface.
 */
public interface Repository extends PanacheRepository<User> {

    /**
     * Creates a user in the service.
     *
     * @param name     : A name of the user
     * @param email    : A valid e-mail
     * @param password : A password of the user
     *
     * @return A Uni<User> object
     */
    Uni<User> createUser(String name, String email, String password);

    /**
     * Returns a user looking for email and password.
     *
     * @param email    : An e-mail of the user
     * @param password : A password
     *
     * @return A Uni<User> object
     */
    Uni<User> authenticate(String email, String password);

    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     *
     * @return A Uni<User> object
     */
    Uni<User> updateEmail(String email, String newEmail);

    /**
     * Changes User password.
     *
     * @param password    : Actual password
     * @param newPassword : New Password
     * @param email       : User's email
     *
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
     * @param email : User email
     *
     * @return Return 1 if user was deleted
     */
    Uni<Long> deleteUser(final String email);
}
