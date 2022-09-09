package dev.orion.users.data.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.domain.vo.Email;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmailTest {

    @Test
    @DisplayName("Should create a email")
    @Order(1)
    void shouldCreateEmail() {
        Email email = new Email("orion@email.com");
        assertNotNull(email);
    }

    @Test
    @DisplayName("Should not create email with empty value")
    @Order(2)
    void shouldNotCreateEmailWithEmptyValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Email("");
        });
    }

    @Test
    @DisplayName("Should not create email with null value")
    @Order(3)
    void shouldNotCreateEmailWithNullValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Email(null);
        });
    }

    @Test
    @DisplayName("Should not create email with invalid value")
    @Order(4)
    void shouldNotCreateEmailWithInvalidValue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Email("invalidEmail");
        });
    }
}
