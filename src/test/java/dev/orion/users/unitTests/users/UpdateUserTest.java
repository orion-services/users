package dev.orion.users.unitTests.users;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import static org.mockito.Mockito.mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.data.usecases.UpdateUserImpl;
import dev.orion.users.domain.model.User;
import dev.orion.users.data.interfaces.Repository;
import dev.orion.users.infra.repository.UserRepository;
import io.quarkus.test.Mock;
import io.smallrye.mutiny.Uni;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class UpdateUserTest {

    @Mock
    private Repository repository;

    @InjectMocks
    private UpdateUserImpl updateUserUseCase;

    @BeforeAll
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = mock(UserRepository.class);
    }

    @Test
    @DisplayName("Change email")
    @Order(1)
    void changeEmail() {
        Mockito.when(repository.updateEmail("orion@test.com",
                "newOrion@test.com"))
                .thenReturn(Uni.createFrom().item(new User()));
        Uni<User> user = updateUserUseCase.updateEmail("orion@test.com",
                "newOrion@test.com");
        assertNotNull(user);
    }

    @Test
    @DisplayName("Change email")
    @Order(2)
    void changeEmailWithBlankArguments() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    updateUserUseCase.updateEmail("", "orion@test.com");
                });
    }

    @Test
    @DisplayName("Change password with blank arguments")
    @Order(3)
    void changePasswordWithBlankArguments() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    updateUserUseCase.updatePassword("", "1234", "12345678");
                });
    }
}
