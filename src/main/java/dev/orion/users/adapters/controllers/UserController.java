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
package dev.orion.users.adapters.controllers;

import dev.orion.users.adapters.gateways.entities.UserEntity;
import dev.orion.users.adapters.gateways.repository.UserRepository;
import dev.orion.users.application.interfaces.AuthenticateUCI;
import dev.orion.users.application.interfaces.CreateUserUCI;
import dev.orion.users.application.usecases.AuthenticateUC;
import dev.orion.users.application.usecases.CreateUserUC;
import dev.orion.users.enterprise.model.User;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotEmpty;

@ApplicationScoped
@WithSession
public class UserController extends Controller {

    /** Use cases */
    private CreateUserUCI  createUserUC = new CreateUserUC();
    private AuthenticateUCI authUC = new AuthenticateUC();

    /** Persistence layer */
    @Inject
    UserRepository userRepository;

    /**
     * Create a new user. Validates the business rules, persists the user and
     * sends an e-mail to the user confirming the registration.
     *
     * @param name     : The user name
     * @param email    : The user e-mail
     * @param password : The user password
     * @return : Returns a Uni<UserEntity> object
     */
    public Uni<UserEntity> createUser(String name, String email, String pwd){
        User user = createUserUC.createUser(name, email, pwd);
        UserEntity entity = mapper.map(user, UserEntity.class);
        return userRepository.createUser(entity)
            .onItem().ifNotNull().transform(u -> u)
            .onItem().ifNotNull().call(this::sendValidationEmail);
    }

    /**
     * Validates the e-mail of a user.
     *
     * @param email : The e-mail of the user
     * @param code  : The validation code
     * @return      : Returns a Uni<UserEntity> object
     */
    public Uni<UserEntity> validateEmail(@NotEmpty String email,
        @NotEmpty String code) {
            Uni<UserEntity> result = null;
            if(Boolean.TRUE.equals(authUC.validateEmail(email, code))){
                result = userRepository.validateEmail(email, code);
            }
            return result;
    }
}
