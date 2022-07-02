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
package dev.orion.users;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class ServiceIT {

  /**
   * Tests if the service throws an HTTP 409 when the e-mail already exists in
   * the data base.
   */
  @Test
  @Order(1)
  void createUser() {
    given()
        .when()
        .param("name", "Orion")
        .param("email", "orion@test.com")
        .param("password", "12345678")
        .post("/api/user/create")
        .then()
        .statusCode(200);
  }

  /**
   * Tests a user creation with a name parameter empty.
   */
  @Test
  @Order(2)
  void createUserNameEmpty() {
    given()
        .when()
        .param("name", "")
        .param("email", "orion@test.com")
        .param("password", "1234")
        .post("/api/user/create")
        .then()
        .statusCode(400);
  }

  /**
   * Tests a user creation with a problem in the e-mail parameter.
   */
  @Test
  @Order(3)
  void createUserEmailProblem() {
    given()
        .when()
        .param("name", "Orion")
        .param("email", "orionteste.com")
        .param("password", "1234")
        .post("/api/user/create")
        .then()
        .statusCode(400);
  }

  /**
   * Tests a user creation with a password empty.
   */
  @Test
  @Order(4)
  void createUserPasswordEmpty() {
    given()
        .when()
        .param("name", "Orion")
        .param("email", "orion@test.com")
        .param("password", "")
        .post("/api/user/create")
        .then()
        .statusCode(400);
  }

  /**
   * Tests if the server returns a HTTP 409 when receive an duplicated user
   * (same e-mail).
   */
  @Test
  @Order(5)
  void createDuplicateUser() {
    given()
        .when()
        .param("name", "Orion")
        .param("email", "orion@test.com")
        .param("password", "1234")
        .post("/api/user/create")
        .then()
        .statusCode(409);
  }

  /**
   * Tests if the user is able to generates a JWT.
   */
  @Test
  @Order(6)
  void login() {
    given()
        .when()
        .param("email", "orion@test.com")
        .param("password", "12345678")
        .post("/api/user/login")
        .then()
        .statusCode(200);
  }

  /**
   * Sends a wrong e-mail to check if the server return a 401 error.
   */
  @Test
  @Order(7)
  void loginWrongEmail() {
    given()
        .when()
        .param("email", "orion@test")
        .param("password", "1234")
        .post("/api/user/login")
        .then()
        .statusCode(401);
  }

  /**
   * Sends an invalid e-mail format to check if the server return a 400 error.
   */
  @Test
  @Order(8)
  void loginWrongInvalidEmail() {
    given()
        .when()
        .param("email", "orion#test.com")
        .param("password", "1234")
        .post("/api/user/login")
        .then()
        .statusCode(400);
  }

  /**
   * Sends a wrong password to check if the server return a 401 error.
   */
  @Test
  @Order(9)
  void loginWrongPassword() {
    given()
        .when()
        .param("email", "orion@test")
        .param("password", "12345678")
        .post("/api/user/login")
        .then()
        .statusCode(401);
  }

  /**
   * Sends a empty e-mail to check if the server return a 400 error.
   */
  @Test
  @Order(10)
  void loginEmptyName() {
    given()
        .when()
        .param("password", "1234")
        .post("/api/user/login")
        .then()
        .statusCode(400);
  }

  /**
   * Sends a empty password to check if the server return a 400 error.
   */
  @Test
  @Order(11)
  void loginEmptyPassword() {
    given()
        .when()
        .param("email", "orion@test.com")
        .post("/api/user/login")
        .then()
        .statusCode(400);
  }

}