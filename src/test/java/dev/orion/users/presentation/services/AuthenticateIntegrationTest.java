package dev.orion.users.presentation.services;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class AuthenticateIntegrationTest {
    @Test
    @Order(1)
    void createUser() {
        given()
                .body("{\"name\":\"Orion2\",\"email\":\"Orion2@email.com\",\"password\":\"Orion123\"}")
                .header("Content-Type", "application/json")
                .post("/api/user/create")
                .then()
                .statusCode(201);
    }
    @Test
    @Order(2)
    void authenticate() {
        given()
                .body("{\"email\":\"Orion2@email.com\",\"password\":\"Orion123\"}")
                .header("Content-Type", "application/json")
                .post("/api/auth/authenticate")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(3)
    void authenticateWithWrongEmail() {
        given()
                .body("{\"email\":\"Orion3@email.com\",\"password\":\"Orion123\"}")
                .header("Content-Type", "application/json")
                .post("/api/auth/authenticate")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(4)
    void authenticateWithInvalidEmail() {
        given()
                .body("{\"email\":\"Orion#email.com\",\"password\":\"Orion123\"}")
                .header("Content-Type", "application/json")
                .post("/api/auth/authenticate")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(5)
    void authenticateWrongPassword() {
        given()
                .body("{\"email\":\"Orion2@email.com\",\"password\":\"Orion1234\"}")
                .header("Content-Type", "application/json")
                .post("/api/auth/authenticate")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(6)
    void authenticateEmptyEmail() {
        given()
                .body("{\"email\":\"\",\"password\":\"Orion123\"}")
                .header("Content-Type", "application/json")
                .post("/api/auth/authenticate")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    void authenticateEmptyPassword() {
        given()
                .body("{\"email\":\"Orion2@email.com\",\"password\":\"\"}")
                .header("Content-Type", "application/json")
                .post("/api/auth/authenticate")
                .then()
                .statusCode(400);
    }
}
