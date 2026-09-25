/*
 * Copyright 2026 Orion Services.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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
 *
 * Each test uses its own unique name/e-mail pair. The "/users/create"
 * endpoint enforces both e-mail and name uniqueness (see
 * UserPersistenceAdapter#createUserEntity), and the underlying database
 * is shared across test methods within a single Quarkus test run, so
 * reusing the same name/e-mail across tests would make them interfere
 * with each other.
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
     * A password that satisfies PasswordValidator's requirements
     * (8+ chars, uppercase, lowercase and a special character).
     */
    private val password = "Password@123"

    private val paramName = "name"
    private val paramEmail = "email"
    private val paramPassword = "password"

    /**
     * Test case for creating a user.
     */
    @Test
    @Order(1)
    fun createUser() {
        val name = "Orion Create"
        val email = "orion.create@test.com"

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
        val name = "Orion WrongPassword"
        val email = "orion.wrongpassword@test.com"

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
        val name = "Orion Login"
        val email = "orion.login@test.com"

        given()
            .`when`()
            .param(paramName, name)
            .param(paramEmail, email)
            .param(paramPassword, password)
            .post("/users/create")
            .then()
            .statusCode(ok)

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
                .getString("authentication.user.name"),
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
        val name = "Orion LoginWrongPassword"
        val email = "orion.loginwrongpassword@test.com"

        given()
            .`when`()
            .param(paramName, name)
            .param(paramEmail, email)
            .param(paramPassword, password)
            .post("/users/create")
            .then()
            .statusCode(ok)

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
        val name = "Orion LoginNoPassword"
        val email = "orion.loginnopassword@test.com"

        given()
            .`when`()
            .param(paramName, name)
            .param(paramEmail, email)
            .param(paramPassword, password)
            .post("/users/create")
            .then()
            .statusCode(ok)

        val response: ValidatableResponse =
            given()
                .`when`()
                .param(paramEmail, email)
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
     * 401 (UNAUTHORIZED), the same as a wrong password for an existing
     * user, so the API does not leak whether an account exists.
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
                .statusCode(unauthorized)

        assertEquals(unauthorized, response.extract().statusCode())
    }

    /**
     * Test case to verify that registering with an e-mail that already
     * exists is rejected with a Bad Request, instead of silently reusing
     * the existing account with its old password.
     *
     * This is a regression test: previously, registering again with an
     * already-used e-mail would return HTTP 200 with the pre-existing
     * user (old password untouched), giving the impression that the
     * account was created/updated with the new password. Logging in
     * afterwards with the "new" password would then unexpectedly fail
     * with "Invalid credentials" (HTTP 401), while the original password
     * would still work (HTTP 200).
     */
    @Test
    @Order(7)
    fun createUserWithDuplicateEmailDoesNotOverwritePassword() {
        val name = "Orion Duplicate"
        val duplicateEmail = "orion.duplicate@test.com"
        val originalPassword = "Original@123"
        val newPassword = "Different@456"

        given()
            .`when`()
            .param(paramName, name)
            .param(paramEmail, duplicateEmail)
            .param(paramPassword, originalPassword)
            .post("/users/create")
            .then()
            .statusCode(ok)

        val duplicateResponse: ValidatableResponse =
            given()
                .`when`()
                .param(paramName, "Another Name")
                .param(paramEmail, duplicateEmail)
                .param(paramPassword, newPassword)
                .post("/users/create")
                .then()
                .statusCode(badRequest)
        assertEquals(badRequest, duplicateResponse.extract().statusCode())

        val loginWithOriginalPassword: ValidatableResponse =
            given()
                .`when`()
                .param(paramEmail, duplicateEmail)
                .param(paramPassword, originalPassword)
                .post("/users/login")
                .then()
                .statusCode(ok)
        assertEquals(ok, loginWithOriginalPassword.extract().statusCode())

        val loginWithNewPassword: ValidatableResponse =
            given()
                .`when`()
                .param(paramEmail, duplicateEmail)
                .param(paramPassword, newPassword)
                .post("/users/login")
                .then()
                .statusCode(unauthorized)
        assertEquals(unauthorized, loginWithNewPassword.extract().statusCode())
    }
}
