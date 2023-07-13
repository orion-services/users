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
package dev.orion.users.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UsersTest {

    @Test
    @Order(1)
    void createUser() {
        given().when()
            .param("name", "Orion")
            .param("email", "orion@test.com")
            .param("password", "12345678")
            .post("/users/create")
        .then()
            .statusCode(200)
            .body("name", is("Orion"),
            "email", is("orion@test.com"));
    }

    @Test
    @Order(2)
    void createUserWithInvalidPassword() {
        given().when()
            .param("name", "Orion")
            .param("email", "orion@test.com")
            .param("password", "123")
            .post("/users/create")
        .then()
            .statusCode(400);
    }

}