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
public class UserTest {

  /**
   * Tests if the service throws an HTTP 409 when the e-mail already exists in
   * the data base
   */
  @Test
  @Order(1)
  public void createUser() {
    given()
        .when()
        .param("name", "Orion")
        .param("email", "orion@test.com")
        .param("password", "1234")
        .post("/api/user/create")
        .then()
        .statusCode(200);
  }

  /**
   * Tests a user creation with a name parameter empty
   */
  @Test
  @Order(2)
  public void createUserNameEmpty() {
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
   * Tests a user creation with a problem in the e-mail parameter
   */
  @Test
  @Order(3)
  public void createUserEmailProblem() {
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
   * Tests a user creation with a password empty
   */
  @Test
  @Order(4)
  public void createUserPasswordEmpty() {
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
   * (same e-mail)
   */
  @Test
  @Order(5)
  public void createDuplicateUser() {
    given()
        .when()
        .param("name", "Orion")
        .param("email", "orion@test.com")
        .param("password", "1234")
        .post("/api/user/create")
        .then()
        .statusCode(409);
  }

}