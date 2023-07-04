package dev.orion.users.unitTests.authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import dev.orion.users.data.usecases.AuthenticateUserImpl;
import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class AuthenticateUserTest {

    @InjectMocks
    private UserRepositoryImpl repository;

    @InjectMocks
    private AuthenticateUserImpl authenticateUserUseCase;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepositoryImpl.class);
        authenticateUserUseCase = mock(AuthenticateUserImpl.class);
        authenticateUserUseCase.repository = repository;
    }

    @Test
    @DisplayName("Authenticate User")
    @Order(1)
    void authenticateUser() {
        User user = new User();
        user.setEmail("orion@test.com");
        user.setPassword(DigestUtils.sha256Hex("password"));
        Mockito.when(repository.authenticate(user)).thenCallRealMethod();
        Mockito.when(authenticateUserUseCase.authenticate("orion@test.com", "password")).thenCallRealMethod();
        Uni<User> uni = authenticateUserUseCase.authenticate("orion@test.com", "password");
        assertNotNull(uni);
    }

    @Test
    @DisplayName("Recover password")
    @Order(2)
    void recoverPassword() {
        User user = new User();
        user.setEmail("orion@test.com");
        user.setPassword(DigestUtils.sha256Hex("password"));

        Mockito.when(repository.recoverPassword("orion@test.com"))
                .thenReturn(Uni.createFrom().item(Mockito.anyString()));
        Mockito.when(authenticateUserUseCase.recoverPassword("orion@test.com")).thenCallRealMethod();

        Uni<String> uni = authenticateUserUseCase.recoverPassword("orion@test.com");

        assertNotNull(uni);
    }

    @Test
    @DisplayName("Recover password with blank arguments")
    @Order(3)
    void recoverPasswordWithBlankArguments() {
        Mockito.when(authenticateUserUseCase.recoverPassword("")).thenCallRealMethod();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    authenticateUserUseCase.recoverPassword("");
                });
    }

    @Test
    @DisplayName("Validate email")
    @Order(4)
    void validateEmail() {
        String email = "orion@test.com";
        String validationToken = UUID.randomUUID().toString();
        UserRepositoryImpl repositoryTest = mock(UserRepositoryImpl.class);
        Mockito.when(repositoryTest.validateEmail(email, validationToken)).thenCallRealMethod();
        Mockito.when(authenticateUserUseCase.validateEmail(email, validationToken)).thenCallRealMethod();

        Uni<User> user = this.authenticateUserUseCase.validateEmail(email, validationToken);

        assertNotNull(user);

    }

    @Test
    @DisplayName("Validate email with blank Arguments")
    @Order(5)
    void validateEmailWithBlankArguments() {
        Mockito.when(authenticateUserUseCase.validateEmail("", "")).thenCallRealMethod();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    authenticateUserUseCase.validateEmail("", "");
                });
    }

    @Test
    @DisplayName("Validate authenticate with null Arguments")
    @Order(6)
    void validateAuthenticateWithBlankArguments() {
        Mockito.when(authenticateUserUseCase.authenticate(null, null)).thenCallRealMethod();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    authenticateUserUseCase.authenticate(null, null);
                });
    }

}
