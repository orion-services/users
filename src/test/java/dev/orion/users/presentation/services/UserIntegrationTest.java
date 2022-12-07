package dev.orion.users.presentation.services;

import io.quarkus.test.junit.QuarkusTest;
import lombok.val;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class UserIntegrationTest {
    val userToQuery;
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
                .body("{\"name\":\"Orion\",\"email\":\"Orion@email.com\",\"password\":\"Orion123\"}")
                .header("Content-Type", "application/json")
                .post("/api/user/create")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(5)
    void createUserWithPasswordLessThanEigthCaracters() {
        given()
                .body("{\"name\":\"Orion\",\"email\":\"Orion@email.com\",\"password\":\"123\"}")
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
    void listAllUsers(){
        val body = given()
                .get("/api/user")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        assertNotNull(body);
    }

    @Test
    @Order(7)
    void listUsersByName(){
        val body = given()
                .get("/api/user")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        assertNotNull(body);
    }

    @Test
    @Order(7)
    void listUsersByEmail(){
        val body = given()
                .get("/api/user")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        assertNotNull(body);
    }
}
