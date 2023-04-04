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

import javax.inject.Inject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import dev.orion.users.model.User;
import dev.orion.users.ws.utils.GoogleUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TwoFactorAuthIntegrationTest {
    public static User user;

    public static final String USER_NAME = "Orion2";
    public static final String USER_EMAIL = "orion2@test.com";
    public static final String USER_PASS = "orion123";

    @Inject
    protected GoogleUtils googleUtils;

    @Test
    @Order(1)
    void createUser() {
        user = given()
            .when()
            .param("name", USER_NAME)
            .param("email", USER_EMAIL)
            .param("password", USER_PASS)
            .post("/api/users/create")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(User.class);
    }

    @Test
    @Order(2)
    void validateWithoutLink2FAuth() {
        given()
            .when()
            .contentType(ContentType.URLENC)
            .formParam("email", USER_EMAIL)
            .formParam("code", "12345678")
            .post("/api/users/google/2FAuth/validate")
            .then()
            .assertThat()
            .statusCode(401);
    }

    @Test
    @Order(3)
    void createQrCode2FAuth() {
        given()
            .when()
            .contentType(ContentType.URLENC)
            .formParam("email", USER_EMAIL)
            .formParam("password", USER_PASS)
            .post("/api/users/google/2FAuth/qrCode")
            .then()
            .assertThat()
            .statusCode(200);
    }

    @Test
    @Order(4)
    void validateCode2FAuth() {
        String userCode = googleUtils.getTOTPCode(user.getSecret2FA());
        given()
            .when()
            .contentType(ContentType.URLENC)
            .formParam("email", USER_EMAIL)
            .formParam("code", userCode)
            .post("/api/users/google/2FAuth/validate")
            .then()
            .assertThat()
            .statusCode(200);
    }

    @Test
    @Order(5)
    void validateWithWrongCode2FAuth() {
        given()
            .when()
            .contentType(ContentType.URLENC)
            .formParam("email", USER_EMAIL)
            .formParam("code", "12345678")
            .post("/api/users/google/2FAuth/validate")
            .then()
            .assertThat()
            .statusCode(401);
    }
}