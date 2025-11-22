/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
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
package dev.orion.users.usecases

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test

import dev.orion.users.application.interfaces.CreateUserUCI
import dev.orion.users.application.usecases.CreateUserUC
import dev.orion.users.enterprise.model.User
import io.smallrye.common.constraint.Assert

/**
 * This class contains unit tests for the CreateUserUC class.
 */
class CreateUserUCTest {

    /** Use cases */
    private val uc: CreateUserUCI = CreateUserUC()

    @Test
    @DisplayName("Create a user with valid arguments")
    @Order(1)
    fun createUserWithValidArguments() {
        val name = "Orion"
        val email = "orion@services.dev"
        val password = "12345678"
        val user: User = uc.createUser(name, email, password)
        Assert.assertNotNull(user)
    }

    @Test
    @DisplayName("Create a user with invalid password")
    @Order(2)
    fun createUserWithInValidPassword() {
        val name = "Orion"
        val email = "orion@services.dev"
        val password = "123"
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            uc.createUser(name, email, password)
        }
    }

    @Test
    @DisplayName("Create a user with no name")
    @Order(3)
    fun createUserWithNoName() {
        val name = ""
        val email = "orion@services.dev"
        val password = "12345678"
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            uc.createUser(name, email, password)
        }
    }

    @Test
    @DisplayName("Create a user with no password")
    @Order(4)
    fun createUserWithNoPassword() {
        val name = "Orion"
        val email = "orion@services.dev"
        val password = ""
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            uc.createUser(name, email, password)
        }
    }

    @Test
    @DisplayName("Create a user with incorrect e-mail")
    @Order(5)
    fun createUserWithIncorrectEmail() {
        val name = "Orion"
        val email = "orionservices.dev"
        val password = "12345678"
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            uc.createUser(name, email, password)
        }
    }
}

