package dev.orion.users.unitTests.users;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

import dev.orion.users.data.usecases.UpdateUserImpl;
import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class UpdateUserTest {

    @InjectMocks
    private UserRepositoryImpl repository;

    @InjectMocks
    private UpdateUserImpl updateUserUseCase;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepositoryImpl.class);
        updateUserUseCase = mock(UpdateUserImpl.class);
    }

    @Test
    @DisplayName("Change email")
    @Order(1)
    void changeEmail() {
        Mockito.when(repository.updateEmail("orion@test.com",
                "newOrion@test.com"))
                .thenReturn(Uni.createFrom().item(new User()));

        Mockito.when(updateUserUseCase.updateEmail("orion@test.com",
                "newOrion@test.com")).thenReturn(Uni.createFrom().item(new User()));

        Uni<User> user = updateUserUseCase.updateEmail("orion@test.com",
                "newOrion@test.com");
        assertNotNull(user);
    }

    @Test
    @DisplayName("Change email")
    @Order(2)
    void changeEmailWithBlankArguments() {
        Mockito.when(updateUserUseCase.updateEmail("", "orion@test.com")).thenCallRealMethod();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    updateUserUseCase.updateEmail("", "orion@test.com");
                });
    }

    @Test
    @DisplayName("Change password with blank arguments")
    @Order(3)
    void changePasswordWithBlankArguments() {
        Mockito.when(updateUserUseCase.updatePassword("", "1234", "12345678")).thenCallRealMethod();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    updateUserUseCase.updatePassword("", "1234", "12345678");
                });
    }
}
