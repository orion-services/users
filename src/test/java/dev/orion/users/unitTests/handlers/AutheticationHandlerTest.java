package dev.orion.users.unitTests.handlers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.data.exceptions.UserWSException;
import dev.orion.users.data.handlers.AuthenticationHandler;
import dev.orion.users.data.mail.MailTemplate;
import dev.orion.users.domain.model.User;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Optional;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class AutheticationHandlerTest {

    @Mock
    private UserWSException userWSExceptionMock;

    @Mock
    MailTemplate mailTemplate;

    @Mock
    Optional<String> issuer;

    @InjectMocks
    private AuthenticationHandler authenticationHandler;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mailTemplate = mock(MailTemplate.class);
    }

    @Test
    void testGenerateJWT() {
        User user = new User();
        user.setEmail("orion@test.com");
        user.getRoleList().add("ROLE_USER");

        String jwt = authenticationHandler.generateJWT(user);

        assertNotNull(jwt);
        assertTrue(jwt.startsWith("eyJ"));
    }

    @Test
    void testCheckTokenEmail_WithMatchingEmails() {
        String email = "orion@test.com";
        String jwtEmail = "orion@test.com";

        boolean result = authenticationHandler.checkTokenEmail(email, jwtEmail);

        assertTrue(result);
    }

    @Test
    void testCheckTokenEmail_WithDifferentEmails() {
        String email = "orion@test.com";
        String jwtEmail = "other@test.com";

        assertThrows(UserWSException.class, () -> authenticationHandler.checkTokenEmail(email, jwtEmail));
    }

    // @Test
    // public void testSendValidationEmail() {
    // User user = new User();
    // user.setEmail("orion@test.com");
    // user.setEmailValidationCode("ABC123");

    // Uni<User> uni = authenticationHandler.sendValidationEmail(user);

    // assertNotNull(uni);
    // assertEquals(user, uni.await().indefinitely());
    // }
}
