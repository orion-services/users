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
package dev.orion.users.integrationTests;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;

import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@TestSecurity(authorizationEnabled = false)
class IntegrationIT {

        @Test
        @Order(1)
        void createUser() {
                given()
                                .when()
                                .param("name", "Orion")
                                .param("email", "orion@test.com")
                                .param("password", "12345678")
                                .post("/api/users/create")
                                .then()
                                .statusCode(200)
                                .body("name", is("Orion"),
                                                "email", is("orion@test.com"));
        }

        @Test
        @Order(2)
        void createUserWithEmptyName() {
                given()
                                .when()
                                .param("name", "")
                                .param("email", "orion@test.com")
                                .param("password", "12345678")
                                .post("/api/users/create")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(3)
        void createUserWithWrongEmail() {
                given()
                                .when()
                                .param("name", "Orion")
                                .param("email", "orionteste.com")
                                .param("password", "12345678")
                                .post("/api/users/create")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(4)
        void createUserWithEmptyEmail() {
                given()
                                .when()
                                .param("name", "Orion")
                                .param("email", "")
                                .param("password", "12345678")
                                .post("/api/users/create")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(5)
        void createUserWithEmptyPassword() {
                given()
                                .when()
                                .param("name", "Orion")
                                .param("email", "orion@test.com")
                                .param("password", "")
                                .post("/api/users/create")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(6)
        void authenticate() {
                given()
                                .when()
                                .param("email", "orion@test.com")
                                .param("password", "12345678")
                                .post("/api/users/authenticate")
                                .then()
                                .statusCode(200);
        }

        @Test
        @Order(7)
        void authenticateWithWrongEmail() {
                given()
                                .when()
                                .param("email", "orion@test")
                                .param("password", "1234")
                                .post("/api/users/authenticate")
                                .then()
                                .statusCode(401);
        }

        @Test
        @Order(8)
        void authenticateWithInvalidEmail() {
                given()
                                .when()
                                .param("email", "orion#test.com")
                                .param("password", "1234")
                                .post("/api/users/authenticate")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(9)
        void authenticateWrongPassword() {
                given()
                                .when()
                                .param("email", "orion@test")
                                .param("password", "123456789")
                                .post("/api/users/authenticate")
                                .then()
                                .statusCode(401);
        }

        @Test
        @Order(10)
        void authenticateEmptyName() {
                given()
                                .when()
                                .param("password", "1234")
                                .post("/api/users/authenticate")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(11)
        void authenticateEmptyPassword() {
                given()
                                .when()
                                .param("email", "orion@test.com")
                                .post("/api/users/authenticate")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(12)
        void createAuthenticate() {
                given()
                                .when()
                                .param("name", "OrionOrion")
                                .param("email", "orionOrion@test.com")
                                .param("password", "12345678")
                                .post("/api/users/createAuthenticate")
                                .then()
                                .statusCode(200);
        }

        @Test
        @Order(13)
        void changeEmail() {

                // Getting a token
                Response response = given()
                                .when()
                                .param("email", "orion@test.com")
                                .param("password", "12345678")
                                .post("/api/users/authenticate");

                String jwt = response.getBody().asString();

                given()
                                .headers("Authorization", "Bearer " + jwt)
                                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                                .formParam("email", "orion@test.com")
                                .formParam("newEmail", "newOrion@test.com")
                                .when()
                                .put("/api/users/update/email")
                                .then()
                                .statusCode(200);
        }

        @Test
        @Order(14)
        void changeEmailFromNonExistingUser() {
                given()
                                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                                .formParam("email", "orionnnn@test.com")
                                .formParam("newEmail", "newOrion@test.com")
                                .when()
                                .put("/api/users/update/email")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(15)
        void changePassword() {
                // Getting a token
                Response response = given()
                                .when()
                                .param("email", "orionOrion@test.com")
                                .param("password", "12345678")
                                .post("/api/users/authenticate");
                String jwt = response.getBody().asString();

                given()
                                .headers("Authorization", "Bearer " + jwt)
                                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                                .formParam("email", "orionOrion@test.com")
                                .formParam("password", "12345678")
                                .formParam("newPassword", "87654321")
                                .when()
                                .put("/api/users/update/password")
                                .then()
                                .statusCode(200);
        }

        @Test
        @Order(16)
        void changePasswordWithWrongPassword() {
                given()
                                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                                .formParam("email", "orionOrion@test.com")
                                .formParam("password", "12345678")
                                .formParam("newPassword", "87654321")
                                .when()
                                .put("/api/users/update/password")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(17)
        void recoverPassword() {
                given()
                                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                                .formParam("email", "orionOrion@test.com")
                                .when()
                                .post("/api/users/recoverPassword")
                                .then()
                                .statusCode(204);
        }

        @Test
        @Order(18)
        void recoverPasswordFromNonExistingUser() {
                given()
                                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                                .formParam("email", "notExist@test.com")
                                .when()
                                .post("/api/users/recoverPassword")
                                .then()
                                .statusCode(400);
        }

        @Test
        @Order(19)
        void deleteUser() {
                given()
                                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                                .formParam("email", "orionOrion@test.com")
                                .when()
                                .delete("/api/users/delete")
                                .then()
                                .statusCode(204);
        }

}