/**
 * @License
 * Copyright 2024 Orion Services @ https://orion-services.dev
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
import dev.orion.users.adapters.presenters.AuthenticationDTO;
import dev.orion.users.application.interfaces.AuthenticateUCI;
import dev.orion.users.application.interfaces.CreateUserUCI;
import dev.orion.users.application.usecases.AuthenticateUC;
import dev.orion.users.application.usecases.CreateUserUC;
import dev.orion.users.enterprise.model.User;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The controller class.
 */
@ApplicationScoped
@WithSession
public class UserController extends BasicController {

    /** Use cases for users. */
    private final CreateUserUCI createUC = new CreateUserUC();

    /** Use cases for authentication. */
    private final AuthenticateUCI authenticationUC = new AuthenticateUC();

    /** Persistence layer. */
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
    public Uni<UserEntity> createUser(final String name, final String email,
            final String password) {
        User user = createUC.createUser(name, email, password);
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
     * @return : Returns a Uni<UserEntity> object
     */
    public Uni<UserEntity> validateEmail(final String email,
            final String code) {
        Uni<UserEntity> result = null;
        if (Boolean.TRUE.equals(authenticationUC.validateEmail(email, code))) {
            result = userRepository.validateEmail(email, code);
        }
        return result;
    }

    /**
     * Authenticates the user in the service.
     *
     * @param email    : The user e-mail
     * @param password : The user password
     * @return : Returns a JSON Web Token (JWT)
     */
    public Uni<String> authenticate(final String email, final String password) {
        // Creates a user in the model to encrypts the password and
        // converts it to an entity
        UserEntity entity = mapper.map(
                authenticationUC.authenticate(email, password),
                UserEntity.class);

        // Finds the user in the service through email and password and
        // generates a JWT
        return userRepository.authenticate(entity)
                .onItem().ifNotNull()
                .transform(this::generateJWT);
    }

    /**
     * Authenticates a user with the provided email and password.
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @return a Uni object that emits an AuthenticationDTO if the
     * authentication is successful
     */
    public Uni<AuthenticationDTO> login(final String email,
            final String password) {
        // Creates a user in the model to encrypts the password and
        // converts it to an entity
        UserEntity entity = mapper.map(
                authenticationUC.authenticate(email, password),
                UserEntity.class);

        return userRepository.authenticate(entity)
            .onItem().ifNotNull().transform(user -> {
                AuthenticationDTO dto = new AuthenticationDTO();
                dto.setToken(this.generateJWT(user));
                dto.setUser(user);
                return dto;
            });
    }

    /**
     * Creates a user, generates a Json Web Token and returns a
     * AuthenticationDTO object.
     *
     * @param name     : The user name
     * @param email    : The user e-mail
     * @param password : The user password
     * @return A Uni<AuthenticationDTO> object
     */
    public Uni<AuthenticationDTO> createAuthenticate(final String name,
            final String email, final String password) {

        return this.createUser(name, email, password)
                .onItem().ifNotNull().transform(user -> {
                    AuthenticationDTO dto = new AuthenticationDTO();
                    dto.setToken(this.generateJWT(user));
                    dto.setUser(user);
                    return dto;
                });
    }

    /**
     * Delete a user from the service.
     *
     * @param email The user's e-mail
     * @return A Uni<Void> object
     */
    public Uni<Void> deleteUser(final String email) {
        return userRepository.deleteUser(email);
    }
}
