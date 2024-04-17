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

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.usecases.DeleteUser;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DeleteUserImpl implements DeleteUser {

    @Inject
    protected UserRepository repository;

    /**
     * Deletes a User from the service.
     *
     * @param email : User email
     *
     * @return Return 1 if user was deleted
     */
    @Override
    public Uni<Void> deleteUser(final String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email can not be blank");
        } else {
            return repository.deleteUser(email);
        }
    }

}
