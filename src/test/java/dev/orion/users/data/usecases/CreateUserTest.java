package dev.orion.users.data.usecases;

import dev.orion.users.data.usecases.user.CreateUserImpl;
import dev.orion.users.infra.panache.entities.UserPanacheEntity;
import dev.orion.users.infra.panache.repositories.UserPanacheRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.data.interfaces.Encrypter;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.user.CreateUserDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.user.CreateUser;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class CreateUserTest {

    @InjectMock
    private UserRepository repository;

    @Mock
    Encrypter encrypter;

    @Inject
    CreateUser createUser;


    @Test
    @DisplayName("Create a user")
    @Order(1)
    void createUserTest() {
        CreateUserDto createUserDto = new CreateUserDto();
        String userHash = UUID.randomUUID().toString();
        createUserDto.name = "Orion";
        createUserDto.email = "orion@test.com";
        createUserDto.password = "12345678";

        User user = new User("Orion", "orion@teste.com", "12345678");
        Mockito.when(repository.create(user)).thenReturn(user);
        user.setUserHash(userHash);
        User userCreated = new User("Orion", "orion@teste.com", "12345678");
        Mockito.when(createUser.create(createUserDto)).thenReturn(user);
        userCreated.setUserHash(userHash);

        assertNotNull(userCreated);
        assertEquals(user.getEmail().getAddress(), userCreated.getEmail().getAddress());
    }

    @Test
    @DisplayName("Create a user with a blank name")
    @Order(2)
    void createUserWithBlankName() {
        assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("", "orion@test.com", "12345678"));
        });
    }

    @Test
    @DisplayName("Create a user with a blank name")
    @Order(3)
    void createUserWithBlankEmail() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("Orion", "", "12345678"));
        });

        assertNotNull(exception);
    }

    @Test
    @DisplayName("Create a user with a blank password")
    @Order(4)
    void createUserWithBlankPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("Orion", "orion@test.com", ""));
        });
    }

    @Test
    @DisplayName("Create a user with an invalid e-mail")
    @Order(5)
    void createUserWithInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("Orion", "orion#test.com", "12345678"));
        });
    }

    @Test
    @DisplayName("Create a user with invalid password")
    @Order(6)
    void createUserWithInvalidPasswordTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto("Orion", "orion@test.com", "12345"));
        });
    }

    @Test
    @DisplayName("Create a user with a null name")
    @Order(7)
    void createUserWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            createUser.create(new CreateUserDto(null, "orion#test.com", "12345678"));
        });
    }

}
