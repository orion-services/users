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

import java.util.Map;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.orion.users.domain.dto.CreateUserDto;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class IntegrationIT {

  @Test
  @Order(1)
  void createUser() {
    given()
        .body("{\"name\":\"Orion\",\"email\":\"Orion@email.com\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/create")
        .then()
        .statusCode(201);
  }

  @Test
  @Order(2)
  void createUserWithEmptyName() {
    given()
        .body("{\"name\":\"\",\"email\":\"Orion@email.com\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/create")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(3)
  void createUserWithWrongEmail() {
    given()
        .body("{\"name\":\"Orion\",\"email\":\"Orionemail.com\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/create")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(4)
  void createUserWithEmptyEmail() {
    given()
        .body("{\"name\":\"Orion\",\"email\":\"\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/create")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(5)
  void createUserWithEmptyPassword() {
    given()
        .body("{\"name\":\"Orion\",\"email\":\"Orion@email.com\",\"password\":\"\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/create")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(6)
  void createDuplicateUser() {
    given()
        .body("{\"name\":\"Orion\",\"email\":\"Orion@email.com\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/create")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(7)
  void authenticate() {
    given()
        .body("{\"email\":\"Orion@email.com\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/authenticate")
        .then()
        .statusCode(200);
  }

  @Test
  @Order(8)
  void authenticateWithWrongEmail() {
    given()
        .body("{\"email\":\"Orion2@email.com\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/authenticate")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(9)
  void authenticateWithInvalidEmail() {
    given()
        .body("{\"email\":\"Orion#email.com\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/authenticate")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(10)
  void authenticateWrongPassword() {
    given()
        .body("{\"email\":\"Orion@email.com\",\"password\":\"Orion1234\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/authenticate")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(11)
  void authenticateEmptyName() {
    given()
        .body("{\"email\":\"\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/authenticate")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(12)
  void authenticateEmptyPassword() {
    given()
        .body("{\"email\":\"Orion@email.com\",\"password\":\"\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/authenticate")
        .then()
        .statusCode(400);
  }

  @Test
  @Order(13)
  void createAuthenticate() {
    given()
        .body("{\"name\":\"OrionTest\",\"email\":\"OrionTest@email.com\",\"password\":\"Orion123\"}")
        .header("Content-Type", "application/json")
        .post("/api/user/createAuthenticate")
        .then()
        .statusCode(200);
  }

}