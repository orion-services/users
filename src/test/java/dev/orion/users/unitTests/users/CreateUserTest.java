package dev.orion.users.unitTests.users;

import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import dev.orion.users.data.handlers.TwoFactorAuthHandler;
import dev.orion.users.data.usecases.CreateUserImpl;
import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class CreateUserTest {

    @InjectMocks
    private TwoFactorAuthHandler twoFactorAuthHandler;

    @InjectMocks
    private UserRepositoryImpl repository;

    @InjectMocks
    private CreateUserImpl createUserUseCase;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepositoryImpl.class);
        twoFactorAuthHandler = mock(TwoFactorAuthHandler.class);
        createUserUseCase = mock(CreateUserImpl.class);
    }

    @Test
    @DisplayName("Create a user")
    @Order(1)
    void createUserWithValidArguments() {
        String name = "Orion";
        String email = "orion@test.com";
        String password = "12345678";
        User expectedUser = new User();

        Mockito.when(repository.createUser(Mockito.any(User.class))).thenReturn(Uni.createFrom().item(expectedUser));
        Mockito.when(twoFactorAuthHandler.generateSecretKey()).thenReturn("secretKey");
        Mockito.when(createUserUseCase.createUser(name, email, password))
                .thenReturn(Uni.createFrom().item(expectedUser));

        Uni<User> result = createUserUseCase.createUser(name, email, password);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedUser, result.await().indefinitely());
        // Mockito.verify(repository).createUser(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Create a user with a blank name")
    @Order(2)
    void createUserWithBlankName() {
        String name = "";
        String email = "orion@test.com";
        String password = "12345678";
        Mockito.when(createUserUseCase.createUser(name, email, password)).thenCallRealMethod();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("", "orion@test.com", "12345678");
                });
    }

    @Test
    @DisplayName("Create a user with a blank email")
    @Order(3)
    void createUserWithBlankEmail() {
        String name = "Orion";
        String email = "";
        String password = "12345678";
        Mockito.when(createUserUseCase.createUser(name, email, password)).thenCallRealMethod();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("Orion", "", "12345678");
                });
    }

    @Test
    @DisplayName("Create a user with a blank password")
    @Order(4)
    void createUserWithBlankPassword() {
        String name = "Orion";
        String email = "orion@test.com";
        String password = "";
        Mockito.when(createUserUseCase.createUser(name, email, password)).thenCallRealMethod();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("Orion", "orion@test.com", "");
                });
    }

    @Test
    @DisplayName("Create a user with an invalid e-mail")
    @Order(5)
    void createUserWithInvalidEmail() {
        String name = "Orion";
        String email = "orion#test.com";
        String password = "12345678";
        Mockito.when(createUserUseCase.createUser(name, email, password)).thenCallRealMethod();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("Orion", "orion#test.com", "12345678");
                });
    }

    @Test
    @DisplayName("Create a user with invalid password")
    @Order(6)
    void createUserWithInvalidPasswordTest() {
        String name = "Orion";
        String email = "orion@test.com";
        String password = "12345";
        Mockito.when(createUserUseCase.createUser(name, email, password)).thenCallRealMethod();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    createUserUseCase.createUser("Orion", "orion@test.com", "12345");
                });
    }

}
