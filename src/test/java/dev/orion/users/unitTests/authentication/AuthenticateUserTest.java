package dev.orion.users.unitTests.authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.UUID;

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
    }

    @Test
    @DisplayName("Recover password")
    @Order(1)
    void recoverPassword() {
        Mockito.when(repository.recoverPassword("orion@test.com"))
                .thenReturn(Uni.createFrom().item("ok"));
        Uni<String> uni = authenticateUserUseCase.recoverPassword("orion@test.com");
        assertNotNull(uni);
    }

    @Test
    @DisplayName("Recover password with blank arguments")
    @Order(2)
    void recoverPasswordWithBlankArguments() {

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    authenticateUserUseCase.recoverPassword("");
                });
    }

    @Test
    @DisplayName("Recover password with blank arguments")
    @Order(2)
    void validateEmailWithBlankArguments() {

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    authenticateUserUseCase.validateEmail("", "");
                });
    }

    @Test
    @DisplayName("Recover password with blank arguments")
    @Order(2)
    void validateEmail() {
        String email = "orion@test.com";
        String validationToken = UUID.randomUUID().toString();
        Mockito.when(authenticateUserUseCase.validateEmail(email, validationToken)).thenCallRealMethod();
        Mockito.when(repository.validateEmail(email, validationToken)).thenCallRealMethod();

        Uni<User> user = authenticateUserUseCase.validateEmail(email, validationToken);

        assertNotNull(user);

    }
}
