package dev.orion.users.unitTests.users;

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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.data.handlers.TwoFactorAuthHandler;
import dev.orion.users.data.interfaces.Repository;
import dev.orion.users.data.usecases.CreateUserImpl;
import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.UserRepository;
import io.quarkus.test.Mock;
import io.smallrye.mutiny.Uni;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class CreateUserTest {

    @Mock
    private TwoFactorAuthHandler twoFactorAuthHandler;

    @Mock
    private Repository repository;

    @InjectMocks
    private CreateUserImpl createUserUseCase;

    @BeforeAll
    void setUp() {
        MockitoAnnotations.openMocks(this);
        twoFactorAuthHandler = mock(TwoFactorAuthHandler.class);
        repository = mock(UserRepository.class);
    }

    @Test
    @DisplayName("Create a user")
    @Order(1)
    void testCreateUser_WithValidArguments_ShouldReturnUserObject() {
        String name = "Orion";
        String email = "orion@test.com";
        String password = "12345678";
        User expectedUser = new User();

        Mockito.when(twoFactorAuthHandler.generateSecretKey()).thenReturn("secretKey");
        Mockito.when(repository.createUser(Mockito.any(User.class))).thenReturn(Uni.createFrom().item(expectedUser));

        Uni<User> result = createUserUseCase.createUser(name, email, password);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedUser, result.await().indefinitely());
        Mockito.verify(repository).createUser(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Create a user with a blank name")
    @Order(2)
    void createUserWithBlankName() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("", "orion@test.com", "12345678");
                });
    }

    @Test
    @DisplayName("Create a user with a blank email")
    @Order(3)
    void createUserWithBlankEmail() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("Orion", "", "12345678");
                });
    }

    @Test
    @DisplayName("Create a user with a blank password")
    @Order(4)
    void createUserWithBlankPassword() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("Orion", "orion@test.com", "");
                });
    }

    @Test
    @DisplayName("Create a user with an invalid e-mail")
    @Order(5)
    void createUserWithInvalidEmail() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("Orion", "orion#test.com", "12345678");
                });
    }

    @Test
    @DisplayName("Create a user with invalid password")
    @Order(6)
    void createUserWithInvalidPasswordTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("Orion", "orion@test.com", "12345");
                });
    }

    @Test
    @DisplayName("Create a user with a null name")
    @Order(7)
    void createUserWithNullName() {
        Assertions.assertThrows(NullPointerException.class,
                () -> {
                    createUserUseCase.createUser(null, "orion#test.com", "12345678");
                });
    }
}
