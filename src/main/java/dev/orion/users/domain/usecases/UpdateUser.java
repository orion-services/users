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
package dev.orion.users.domain.usecases;

import dev.orion.users.domain.model.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface UpdateUser {
    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     *
     * @return An Uni<User> object
     */
    Uni<User> updateEmail(String email, String newEmail);

    /**
     * Updates the user's password.
     *
     * @param email       : User's email
     * @param password    : Current password
     * @param newPassword : New Password
     *
     * @return An Uni<User> object
     */
    Uni<User> updatePassword(String email, String password, String newPassword);

    /**
     * Updates a user.
     *
     * @param user A user object
     * @return An Uni<User> object
     */
    Uni<User> updateUser(User user);
}
