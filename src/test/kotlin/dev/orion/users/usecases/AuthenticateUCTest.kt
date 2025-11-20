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
package dev.orion.users.usecases

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test

import dev.orion.users.application.interfaces.AuthenticateUCI
import dev.orion.users.application.usecases.AuthenticateUC
import dev.orion.users.enterprise.model.User
import io.smallrye.common.constraint.Assert

/**
 * This class contains unit tests for the CreateUserUC class.
 */
class AuthenticateUCTest {

    /** Use cases */
    private val uc: AuthenticateUCI = AuthenticateUC()

    @Test
    @DisplayName("Authenticates a user with valid arguments")
    @Order(1)
    fun authenticate() {
        val email = "orion@services.dev"
        val password = "12345678"
        val user: User = uc.authenticate(email, password)
        Assert.assertNotNull(user)
    }

    @Test
    @DisplayName("Authenticates a user with valid arguments")
    @Order(2)
    fun authenticateWithInValidPassword() {
        val email = "orion@services.dev"
        val password = "123"
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            uc.authenticate(email, password)
        }
    }

    @Test
    @DisplayName("Authenticates a empty e-mail")
    @Order(3)
    fun authenticateWithNoEmail() {
        val email = ""
        val password = "12345678"
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            uc.authenticate(email, password)
        }
    }

    @Test
    @DisplayName("Authenticates a empty password")
    @Order(4)
    fun authenticateWithNoPassword() {
        val email = "orion@services.dev"
        val password = ""
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            uc.authenticate(email, password)
        }
    }
}

