package dev.orion.users.unitTests.authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.data.usecases.AuthenticateUserImpl;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import io.smallrye.mutiny.Uni;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class AuthenticateUserTest {

    @InjectMocks
    private UserRepository repository;

    @InjectMocks
    private AuthenticateUserImpl authenticateUserUseCase;

    @BeforeAll
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
}
