package dev.orion.users.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.domain.models.User;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest {
    @Test
    @DisplayName("Should create a user")
    @Order(1)
    void shouldCreateUser() {
        User user = new User("Orion", "orion@email.com", "orion123");
        assertNotNull(user);
    }

    @Test
    @DisplayName("Should not create a user with empty name")
    @Order(2)
    void shouldNotCreateUserWithEmptyName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new User("", "orion@email.com", "orion123");
        });
    }

    @Test
    @DisplayName("Should not create a user with null name")
    @Order(2)
    void shouldNotCreateUserWithNullName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new User(null, "orion@email.com", "orion123");
        });
    }

    @Test
    @DisplayName("Should not create a user with empty password")
    @Order(3)
    void shouldNotCreateUserWithEmptyPassword() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new User("Orion", "orion@email.com", "");
        });
    }

    @Test
    @DisplayName("Should not create a user with null password")
    @Order(4)
    void shouldNotCreateUserWithNullPassword() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new User("Orion", "orion@email.com", null);
        });
    }

    @Test
    @DisplayName("Should not create a user with password less eigth caracters")
    @Order(5)
    void shouldNotCreateUserWithPasswordLessThanEigthCaracters() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new User("Orion", "orion@email.com", "orion");
        });
    }
}
