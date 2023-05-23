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

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import dev.orion.users.data.handlers.TwoFactorAuthHandler;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.model.User;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@Transactional
public class TwoFactorAuthIntegrationTest {

    static User user;

    public static final String USER_NAME = "Orion2";
    public static final String USER_EMAIL = "orion2@test.com";
    public static final String USER_PASS = "orion123";
    public static final String TWOFACTOR_VALIDATECODE_URL = "/api/users/twoFactorAuth/validate";
    public static final String TWOFACTOR_QRCODE_URL = "/api/users/twoFactorAuth/qrCode";

    @Inject
    protected TwoFactorAuthHandler googleUtils;

    @Inject
    protected UserRepository useCase;

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
    void createQrCode2FAuth() {
        given()
                .when()
                .contentType(ContentType.URLENC)
                .formParam("email", USER_EMAIL)
                .formParam("password", USER_PASS)
                .post(TWOFACTOR_QRCODE_URL)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @Order(3)
    void validateWithWrongCode2FAuth() {
        given()
                .when()
                .contentType(ContentType.URLENC)
                .formParam("email", USER_EMAIL)
                .formParam("password", USER_PASS)
                .formParam("code", "12345678")
                .post(TWOFACTOR_VALIDATECODE_URL)
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    @Order(4)
    void validateWithWrongPass2FAuth() {
        String userCode = googleUtils.getTOTPCode(user.getSecret2FA());
        given()
                .when()
                .contentType(ContentType.URLENC)
                .formParam("email", USER_EMAIL)
                .formParam("password", "123")
                .formParam("code", userCode)
                .post(TWOFACTOR_VALIDATECODE_URL)
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    @Order(5)
    void validateWithWrongPassAndCode2FAuth() {
        given()
                .when()
                .contentType(ContentType.URLENC)
                .formParam("email", USER_EMAIL)
                .formParam("password", "12345678")
                .formParam("code", "12345678")
                .post(TWOFACTOR_VALIDATECODE_URL)
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    @Order(6)
    void validateWithoutLink2FAuth() {
        given()
                .when()
                .contentType(ContentType.URLENC)
                .formParam("email", USER_EMAIL)
                .formParam("password", USER_PASS)
                .formParam("code", "12345678")
                .post(TWOFACTOR_VALIDATECODE_URL)
                .then()
                .assertThat()
                .statusCode(401);
    }

    @Test
    @Order(7)
    void validateCode2FAuth() {
        String code = googleUtils.getTOTPCode(user.getSecret2FA());

        given()
                .when()
                .contentType(ContentType.URLENC)
                .formParam("email", USER_EMAIL)
                .formParam("password", USER_PASS)
                .formParam("code", code)
                .post("/api/users/twoFactorAuth/validate")
                .then()
                .assertThat()
                .statusCode(200);

    }
}
