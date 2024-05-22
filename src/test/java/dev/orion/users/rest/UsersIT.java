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
package dev.orion.users.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;

/**
 * This class contains test cases for the Users REST API.
 */
@QuarkusTest
class UsersIT {

    /**
     * Represents the HTTP status code for a successful request.
     */
    private static final int OK = 200;

    /**
     * The HTTP status code for a bad request.
     */
    private static final int BAD_REQUEST = 400;

    /**
     * The HTTP status code for an unauthorized request.
     */
    private static final int UNAUTHORIZED = 401;

    /**
     * Test case for creating a user.
     */
    private static final String NAME = "Orion";
    private static final String EMAIL = "orion@test.com";
    private static final String PASSWORD = "12345678";

    private static final String PARAM_NAME = "name";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_PASSWORD = "password";

    /**
     * Test case for creating a user.
     */
    @Test
    @Order(1)
    void createUser() {
        ValidatableResponse response = given().when()
                .param(PARAM_NAME, NAME)
                .param(PARAM_EMAIL, EMAIL)
                .param(PARAM_PASSWORD, PASSWORD)
                .post("/users/create")
                .then()
                .statusCode(OK)
                .body(PARAM_NAME, is(NAME),
                        PARAM_EMAIL, is(EMAIL));
        assertEquals(OK, response.extract().statusCode());
    }

    /**
     * Test case to verify the behavior of creating a user with an invalid
     * password.
     */
    @Test
    @Order(2)
    void createUserWithWrongPassword() {
        ValidatableResponse response = given().when()
                .param(PARAM_NAME, NAME)
                .param(PARAM_EMAIL, EMAIL)
                .param(PARAM_PASSWORD, "123")
                .post("/users/create")
                .then()
                .statusCode(BAD_REQUEST);
        assertEquals(BAD_REQUEST, response.extract().statusCode());
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
    void login() {
        given().when()
                .param(PARAM_NAME, NAME)
                .param(PARAM_EMAIL, EMAIL)
                .param(PARAM_PASSWORD, PASSWORD)
                .post("/users/create");

        ValidatableResponse response = given().when()
                .param(PARAM_EMAIL, EMAIL)
                .param(PARAM_PASSWORD, PASSWORD)
                .post("/users/login")
                .then()
                .statusCode(OK);

        assertEquals(NAME, response.extract()
                .body().jsonPath().getString("user.name"));
    }

    /**
     * Test case to verify the behavior of the loginWithWrongPassword method.
     * This method tests the scenario where a user tries to login with an
     * incorrect password.
     */
    @Test
    @Order(4)
    void loginWithWrongPassword() {
        given().when()
                .param(PARAM_NAME, NAME)
                .param(PARAM_EMAIL, EMAIL)
                .param(PARAM_PASSWORD, PASSWORD)
                .post("/users/create");

        ValidatableResponse response = given().when()
                .param(PARAM_EMAIL, EMAIL)
                .param(PARAM_PASSWORD, "123")
                .post("/users/login")
                .then()
                .statusCode(UNAUTHORIZED);

        assertEquals(UNAUTHORIZED, response.extract().statusCode());
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
    void loginWithoutPassword() {
        given().when()
                .param(PARAM_NAME, NAME)
                .param(PARAM_EMAIL, EMAIL)
                .param(PARAM_PASSWORD, PASSWORD)
                .post("/users/create");

        ValidatableResponse response = given().when()
                .param(PARAM_PASSWORD, PASSWORD)
                .post("/users/login")
                .then()
                .statusCode(BAD_REQUEST);

        assertEquals(BAD_REQUEST, response.extract().statusCode());
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
    void loginWithNonexistentUser() {
        ValidatableResponse response = given().when()
                .param(EMAIL, "nonexistent@orion-services.dev")
                .param(PARAM_PASSWORD, PASSWORD)
                .post("/users/login")
                .then()
                .statusCode(BAD_REQUEST);

        assertEquals(BAD_REQUEST, response.extract().statusCode());
    }

}