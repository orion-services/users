package dev.orion.users.data.usecases;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.CreateUserDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.CreateUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import org.apache.commons.codec.digest.DigestUtils;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateUserTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    CreateUser createUser = new CreateUserImpl(repository);

    @Test
    @DisplayName("Create a user")
    @Order(1)
    void createUserTest() {
        CreateUserDto createUserDto = new CreateUserDto();

        createUserDto.name = "Orion";
        createUserDto.email = "orion@test.com";
        createUserDto.password = "12345678";

        User user = new User("Orion", "orion@teste.com", "12345678");
        Mockito.when(repository.create(any(User.class))).thenReturn(user);

        User userCreated = createUser.create(createUserDto);

        assertNotNull(userCreated);
        assertEquals(user, userCreated);
    }

    @Test
    @DisplayName("Create a user with a blank name")
    @Order(2)
    void createUserWithBlankName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("", "orion@test.com", "12345678"));
        });
    }

    @Test
    @DisplayName("Create a user with a blank name")
    @Order(3)
    void createUserWithBlankEmail() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("Orion", "", "12345678"));
        });
    }

    @Test
    @DisplayName("Create a user with a blank password")
    @Order(4)
    void createUserWithBlankPassword() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("Orion", "orion@test.com", ""));
        });
    }

    @Test
    @DisplayName("Create a user with an invalid e-mail")
    @Order(5)
    void createUserWithInvalidEmail() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("Orion", "orion#test.com", "12345678"));
        });
    }

    @Test
    @DisplayName("Create a user with invalid password")
    @Order(6)
    void createUserWithInvalidPasswordTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("Orion", "orion@test.com", "12345"));
        });
    }

    @Test
    @DisplayName("Create a user with a null name")
    @Order(7)
    void createUserWithNullName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto(null, "orion#test.com", "12345678"));
        });
    }

}
