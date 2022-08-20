package dev.orion.users.usecase;

import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.Repository;
import io.smallrye.mutiny.Uni;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateUserTest {

    @Mock
    Repository repository;

    @InjectMocks
    UseCase uc = new CreateUser();

    @Test
    @DisplayName("Create a user")
    @Order(1)
    void createUserTest() {
        Mockito.when(repository.createUser("Orion", "orion@test.com", DigestUtils.sha256Hex("12345678")))
                .thenReturn(Uni.createFrom().item(new User()));
        Uni<User> uni = uc.createUser("Orion", "orion@test.com", "12345678");
        assertNotNull(uni);
    }

    @Test
    @DisplayName("Create a user with a blank name")
    @Order(2)
    void createUserWithBlankName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            uc.createUser("", "orion@test.com", "12345678");
        });
    }

    @Test
    @DisplayName("Create a user with a blank name")
    @Order(3)
    void createUserWithBlankEmail() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            uc.createUser("Orion", "", "12345678");
        });
    }

    @Test
    @DisplayName("Create a user with a blank password")
    @Order(4)
    void createUserWithBlankPassword() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            uc.createUser("Orion", "orion@test.com", "");
        });
    }

    @Test
    @DisplayName("Create a user with an invalid e-mail")
    @Order(5)
    void createUserWithInvalidEmail() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            uc.createUser("Orion", "orion#test.com", "12345678");
        });
    }

    @Test
    @DisplayName("Create a user with invalid password")
    @Order(6)
    void createUserWithInvalidPasswordTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            uc.createUser("Orion", "orion@test.com", "12345");
        });
    }

    @Test
    @DisplayName("Create a user with a null name")
    @Order(7)
    void createUserWithNullName() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            uc.createUser(null, "orion#test.com", "12345678");
        });
    }

}
