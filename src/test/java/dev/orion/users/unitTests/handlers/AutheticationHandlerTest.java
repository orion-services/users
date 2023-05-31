package dev.orion.users.unitTests.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.InjectMocks;

import org.mockito.Mockito;
import dev.orion.users.data.exceptions.UserWSException;
import dev.orion.users.data.handlers.AuthenticationHandler;
import dev.orion.users.data.mail.MailTemplate;
import dev.orion.users.domain.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Optional;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class AutheticationHandlerTest {

    @InjectMocks
    MailTemplate mailTemplate;

    @InjectMocks
    Optional<String> issuer;

    @InjectMocks
    private AuthenticationHandler authenticationHandler;

    @BeforeEach
    public void setup() {
        mailTemplate = mock(MailTemplate.class);
        authenticationHandler = mock(AuthenticationHandler.class);
    }

    @Test
    void testGenerateJWT() {
        User user = new User();
        user.setEmail("orion@test.com");
        user.getRoleList().add("ROLE_USER");

        Mockito.when(authenticationHandler.generateJWT(user)).thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");

        String jwt = authenticationHandler.generateJWT(user);

        assertNotNull(jwt);
        assertTrue(jwt.startsWith("eyJ"));
    }

    @Test
    void testCheckTokenEmail_WithMatchingEmails() {
        String email = "orion@test.com";
        String jwtEmail = "orion@test.com";

        Mockito.when(authenticationHandler.checkTokenEmail(email, jwtEmail)).thenCallRealMethod();

        boolean result = authenticationHandler.checkTokenEmail(email, jwtEmail);

        assertTrue(result);
    }

    @Test
    void testCheckTokenEmail_WithDifferentEmails() {
        String email = "orion@test.com";
        String jwtEmail = "other@test.com";

        Mockito.when(authenticationHandler.checkTokenEmail(email, jwtEmail)).thenCallRealMethod();

        assertThrows(UserWSException.class, () -> authenticationHandler.checkTokenEmail(email, jwtEmail));
    }

    @Test
    void testSendValidationEmail() {
        User user = new User();
        user.setEmail("orion@test.com");
        user.setEmailValidationCode("ABC123");

        Mockito.when(authenticationHandler.sendValidationEmail(user)).thenCallRealMethod();
        // Mockito.when(MailTemplate.validateEmail(anyString())).thenCallRealMethod();

        Uni<User> uni = authenticationHandler.sendValidationEmail(user);

        assertNotNull(uni);
        // assertEquals(user, uni);
    }
}
