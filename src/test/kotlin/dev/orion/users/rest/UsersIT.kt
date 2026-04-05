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
package dev.orion.users.rest

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.response.ValidatableResponse
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test

/**
 * This class contains test cases for the Users REST API.
 */
@QuarkusTest
class UsersIT {
    /**
     * Represents the HTTP status code for a successful request.
     */
    private val ok = 200

    /**
     * The HTTP status code for a bad request.
     */
    private val badRequest = 400

    /**
     * The HTTP status code for an unauthorized request.
     */
    private val unauthorized = 401

    /**
     * Test case for creating a user.
     */
    private val name = "Orion"
    private val email = "orion@test.com"
    private val password = "12345678"

    private val paramName = "name"
    private val paramEmail = "email"
    private val paramPassword = "password"

    /**
     * Test case for creating a user.
     */
    @Test
    @Order(1)
    fun createUser() {
        val response: ValidatableResponse =
            given()
                .`when`()
                .param(paramName, name)
                .param(paramEmail, email)
                .param(paramPassword, password)
                .post("/users/create")
                .then()
                .statusCode(ok)
                .body(
                    paramName,
                    `is`(name),
                    paramEmail,
                    `is`(email),
                )
        assertEquals(ok, response.extract().statusCode())
    }

    /**
     * Test case to verify the behavior of creating a user with an invalid
     * password.
     */
    @Test
    @Order(2)
    fun createUserWithWrongPassword() {
        val response: ValidatableResponse =
            given()
                .`when`()
                .param(paramName, name)
                .param(paramEmail, email)
                .param(paramPassword, "123")
                .post("/users/create")
                .then()
                .statusCode(badRequest)
        assertEquals(badRequest, response.extract().statusCode())
    }

    /**
     * Test case for the login functionality.
     *
     * This method sends a POST request to the "/users/login" endpoint with the
     * specified email and password parameters. It then validates the response
     * status code and asserts that the returned user's name matches the
     * expected name.
     */
    @Test
    @Order(3)
    fun login() {
        given()
            .`when`()
            .param(paramName, name)
            .param(paramEmail, email)
            .param(paramPassword, password)
            .post("/users/create")

        val response: ValidatableResponse =
            given()
                .`when`()
                .param(paramEmail, email)
                .param(paramPassword, password)
                .post("/users/login")
                .then()
                .statusCode(ok)

        assertEquals(
            name,
            response
                .extract()
                .body()
                .jsonPath()
                .getString("user.name"),
        )
    }

    /**
     * Test case to verify the behavior of the loginWithWrongPassword method.
     * This method tests the scenario where a user tries to login with an
     * incorrect password.
     */
    @Test
    @Order(4)
    fun loginWithWrongPassword() {
        given()
            .`when`()
            .param(paramName, name)
            .param(paramEmail, email)
            .param(paramPassword, password)
            .post("/users/create")

        val response: ValidatableResponse =
            given()
                .`when`()
                .param(paramEmail, email)
                .param(paramPassword, "123")
                .post("/users/login")
                .then()
                .statusCode(unauthorized)

        assertEquals(unauthorized, response.extract().statusCode())
    }

    /**
     * Test case for logging in without providing a password.
     *
     * This test sends a POST request to the "/users/login" endpoint without
     * providing a password. It expects the server to respond with a 400 Bad
     * Request status code. The test asserts that the response status code
     * matches the expected value.
     */
    @Test
    @Order(5)
    fun loginWithoutPassword() {
        given()
            .`when`()
            .param(paramName, name)
            .param(paramEmail, email)
            .param(paramPassword, password)
            .post("/users/create")

        val response: ValidatableResponse =
            given()
                .`when`()
                .param(paramPassword, password)
                .post("/users/login")
                .then()
                .statusCode(badRequest)

        assertEquals(badRequest, response.extract().statusCode())
    }

    /**
     * Test case to verify the behavior when attempting to login with a
     * nonexistent user.
     * The test sends a POST request to the "/users/login" endpoint with a
     * nonexistent user's email and a password.
     * The expected behavior is a response with a status code of
     * 400 (BAD_REQUEST).
     */
    @Test
    @Order(6)
    fun loginWithNonexistentUser() {
        val response: ValidatableResponse =
            given()
                .`when`()
                .param(paramEmail, "nonexistent@orion-services.dev")
                .param(paramPassword, password)
                .post("/users/login")
                .then()
                .statusCode(badRequest)

        assertEquals(badRequest, response.extract().statusCode())
    }
}
