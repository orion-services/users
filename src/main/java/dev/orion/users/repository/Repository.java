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
 * User repository.
 */
public interface Repository extends PanacheRepository<User> {

    /**
     * Creates a user in the service.
     *
     * @param name     : A name of the user
     * @param email    : A valid e-mail
     * @param password : A password of the user
     *
     * @return Returns a user asynchronously
     */
    Uni<User> createUser(String name, String email, String password);

    /**
     * Returns a user looking for email and password.
     *
     * @param email    : An e-mail of the user
     * @param password : A password
     *
     * @return Returns a user asynchronously
     */
    Uni<User> authenticate(String email, String password);

    /**
    * Changes User password.
    *
    * @param password    : Actual password
    * @param newPassword : New Password
    * @param email       : User's email
    *
    * @return Returns a user asynchronously
    */
    Uni<User> changePassword(String password, String newPassword, String email);

    Uni<String> recoverPassword(String email);
}
