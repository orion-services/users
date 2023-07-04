package dev.orion.users.integrationTests;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dev.orion.users.mocks.RenardeCookieFilter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.webauthn.WebAuthnEndpointHelper;
import io.quarkus.test.security.webauthn.WebAuthnHardware;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;

import io.restassured.specification.RequestSpecification;
import io.vertx.core.json.JsonObject;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
@TestMethodOrder(OrderAnnotation.class)
class WebAuthnResourceTest {

        @Test
        @Order(1)
        void testWebAuthn() {
                Filter cookieFilter = new RenardeCookieFilter();
                WebAuthnHardware token = new WebAuthnHardware();

                String userEmail = "orion@test.com";
                String name = "Orion";

                // config register
                String challenge = WebAuthnEndpointHelper.invokeRegistration(userEmail, cookieFilter);
                JsonObject registrationJson = token.makeRegistrationJson(challenge);

                // test register
                RequestSpecification request = given()
                                .when()
                                .contentType(ContentType.URLENC)
                                .filter(cookieFilter)
                                .redirects().follow(false)
                                .log().ifValidationFails()
                                .formParam("userEmail", userEmail)
                                .formParam("name", name);

                WebAuthnEndpointHelper.addWebAuthnRegistrationFormParameters(request, registrationJson);

                request
                                .post("/api/users/webauthn/register")
                                .then()
                                .statusCode(200);

                // config login
                challenge = WebAuthnEndpointHelper.invokeLogin(userEmail, cookieFilter);
                JsonObject loginJson = token.makeLoginJson(challenge);

                // test login
                RequestSpecification requestLogin = given()
                                .when()
                                .contentType(ContentType.URLENC)
                                .filter(cookieFilter)
                                .redirects().follow(false)
                                .log().ifValidationFails()
                                .formParam("userEmail", userEmail);

                WebAuthnEndpointHelper.addWebAuthnLoginFormParameters(requestLogin,
                                loginJson);

                requestLogin
                                .post("/api/users/webauthn/login")
                                .then()
                                .statusCode(200);

        }

        @Test
        @Order(2)
        void testWebAuthnWithNullArguments() {
                Filter cookieFilter = new RenardeCookieFilter();
                WebAuthnHardware token = new WebAuthnHardware();

                String userEmail = "orion@test.com";
                String name = "Orion";

                // config register
                String challenge = WebAuthnEndpointHelper.invokeRegistration(userEmail, cookieFilter);
                JsonObject registrationJson = token.makeRegistrationJson(challenge);

                // test register
                RequestSpecification request = given()
                                .when()
                                .contentType(ContentType.URLENC)
                                .filter(cookieFilter)
                                .redirects().follow(false)
                                .log().ifValidationFails();
                // .formParam("userEmail", null)
                // .formParam("name", null);

                WebAuthnEndpointHelper.addWebAuthnRegistrationFormParameters(request, registrationJson);

                request
                                .post("/api/users/webauthn/register")
                                .then()
                                .statusCode(400);

                // config login
                challenge = WebAuthnEndpointHelper.invokeLogin(userEmail, cookieFilter);
                JsonObject loginJson = token.makeLoginJson(challenge);

                // test login
                RequestSpecification requestLogin = given()
                                .when()
                                .contentType(ContentType.URLENC)
                                .filter(cookieFilter)
                                .redirects().follow(false)
                                .log().ifValidationFails();

                WebAuthnEndpointHelper.addWebAuthnLoginFormParameters(requestLogin,
                                loginJson);

                requestLogin
                                .post("/api/users/webauthn/login")
                                .then()
                                .statusCode(400);

        }

        @Test
        @Order(3)
        void testWebAuthnWithEmptyArguments() {
                Filter cookieFilter = new RenardeCookieFilter();
                WebAuthnHardware token = new WebAuthnHardware();

                String userEmail = "orion@test.com";
                String name = "Orion";

                // config register
                String challenge = WebAuthnEndpointHelper.invokeRegistration(userEmail, cookieFilter);
                JsonObject registrationJson = token.makeRegistrationJson(challenge);

                // test register
                RequestSpecification request = given()
                                .when()
                                .contentType(ContentType.URLENC)
                                .filter(cookieFilter)
                                .redirects().follow(false)
                                .log().ifValidationFails()
                                .formParam("userEmail", "")
                                .formParam("name", "");

                WebAuthnEndpointHelper.addWebAuthnRegistrationFormParameters(request, registrationJson);

                request
                                .post("/api/users/webauthn/register")
                                .then()
                                .statusCode(400);

                // config login
                challenge = WebAuthnEndpointHelper.invokeLogin(userEmail, cookieFilter);
                JsonObject loginJson = token.makeLoginJson(challenge);

                // test login
                RequestSpecification requestLogin = given()
                                .when()
                                .contentType(ContentType.URLENC)
                                .filter(cookieFilter)
                                .redirects().follow(false)
                                .log().ifValidationFails()
                                .formParam("userEmail", "");

                WebAuthnEndpointHelper.addWebAuthnLoginFormParameters(requestLogin,
                                loginJson);

                requestLogin
                                .post("/api/users/webauthn/login")
                                .then()
                                .statusCode(400);

        }

        @Test
        @Order(4)
        void testWebAuthnWithNullUser() {
                Filter cookieFilter = new RenardeCookieFilter();
                WebAuthnHardware token = new WebAuthnHardware();

                String userEmail = "orion@test4.com";
                String wrongUserEmail = "orion@wrong.com";
                String name = "Orion4";

                // config register
                String challenge = WebAuthnEndpointHelper.invokeRegistration(userEmail, cookieFilter);
                JsonObject registrationJson = token.makeRegistrationJson(challenge);

                // test register
                RequestSpecification request = given()
                                .when()
                                .contentType(ContentType.URLENC)
                                .filter(cookieFilter)
                                .redirects().follow(false)
                                .log().ifValidationFails()
                                .formParam("userEmail", userEmail)
                                .formParam("name", name);

                WebAuthnEndpointHelper.addWebAuthnRegistrationFormParameters(request, registrationJson);

                request
                                .post("/api/users/webauthn/register")
                                .then()
                                .statusCode(200);

                // config login
                challenge = WebAuthnEndpointHelper.invokeLogin(userEmail, cookieFilter);
                JsonObject loginJson = token.makeLoginJson(challenge);

                // test login
                RequestSpecification requestLogin = given()
                                .when()
                                .contentType(ContentType.URLENC)
                                .filter(cookieFilter)
                                .redirects().follow(false)
                                .log().ifValidationFails()
                                .formParam("userEmail", wrongUserEmail);

                WebAuthnEndpointHelper.addWebAuthnLoginFormParameters(requestLogin,
                                loginJson);

                requestLogin
                                .post("/api/users/webauthn/login")
                                .then()
                                .statusCode(400);
        }

}