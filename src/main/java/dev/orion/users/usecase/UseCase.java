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

import dev.orion.users.domain.model.User;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Use cases interface for User entity.
 */


public interface UseCase {

    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return A Uni<User> object
     */
     Uni<User> createUser(String name, String email, String password);

    /**
     * Authenticates the user in the service (UC: Authenticate).
     *
     * @param email  : The email of the user
     * @param password : The password of the user
     * @return A Uni<User> object
     */
     Uni<User> authenticate(String email, String password);

     Uni<List<User>> listUser();

     Uni<User> removeUser();

     Uni<User> deleteUser();
}
