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
package dev.orion.users.usecases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import dev.orion.users.application.interfaces.CreateUserUCI;
import dev.orion.users.application.usecases.CreateUserUC;
import dev.orion.users.enterprise.model.User;
import io.smallrye.common.constraint.Assert;

/**
 * This class contains unit tests for the CreateUserUC class.
 */
class CreateUserUCTest {

    //** Use cases */
    CreateUserUCI uc = new CreateUserUC();

    @Test
    @DisplayName("Create a user with valid arguments")
    @Order(1)
    void createUserWithValidArguments() {
        String name = "Orion";
        String email = "orion@services.dev";
        String password = "12345678";
        User user = uc.createUser(name, email, password);
        Assert.assertNotNull(user);
    }

    @Test
    @DisplayName("Create a user with invalid password")
    @Order(2)
    void createUserWithInValidPassword() {
        String name = "Orion";
        String email = "orion@services.dev";
        String password = "123";
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
            uc.createUser(name, email, password);
        });
    }

    @Test
    @DisplayName("Create a user with no name")
    @Order(3)
    void createUserWithNoName() {
        String name = "";
        String email = "orion@services.dev";
        String password = "12345678";
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
            uc.createUser(name, email, password);
        });
    }

    @Test
    @DisplayName("Create a user with no password")
    @Order(4)
    void createUserWithNoPassword() {
        String name = "Orion";
        String email = "orion@services.dev";
        String password = "";
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
            uc.createUser(name, email, password);
        });
    }

    @Test
    @DisplayName("Create a user with incorrect e-mail")
    @Order(5)
    void createUserWithIncorrectEmail() {
        String name = "Orion";
        String email = "orionservices.dev";
        String password = "12345678";
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> {
            uc.createUser(name, email, password);
        });
    }

}
