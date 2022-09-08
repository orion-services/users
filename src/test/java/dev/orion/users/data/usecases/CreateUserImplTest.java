package dev.orion.users.data.usecases;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateUserImplTest {
//
//    @Mock
//    UserPanacheRepository repository;
//
//    @InjectMocks
//    UseCase uc = new CreateUserImpl();
//
//    @Test
//    @DisplayName("Create a user")
//    @Order(1)
//    void createUserTest() {
//        CreateUserDto createUserDto = new CreateUserDto("Orion", "orion@test.com", DigestUtils.sha256Hex("12345678"));
//        Mockito.when(repository.createUser(User.createUser(createUserDto)))
//                .thenReturn(Uni.createFrom().item(new UserPanacheEntity()));
//        Uni<UserPanacheEntity> uni = uc.create(new CreateUserDto("Orion", "orion@test.com", "12345678"));
//        assertNotNull(uni);
//    }
//
//    @Test
//    @DisplayName("Create a user with a blank name")
//    @Order(2)
//    void createUserWithBlankName() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            uc.create(new CreateUserDto("", "orion@test.com", "12345678"));
//        });
//    }
//
//    @Test
//    @DisplayName("Create a user with a blank name")
//    @Order(3)
//    void createUserWithBlankEmail() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            uc.create(new CreateUserDto("Orion", "", "12345678"));
//        });
//    }
//
//    @Test
//    @DisplayName("Create a user with a blank password")
//    @Order(4)
//    void createUserWithBlankPassword() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            uc.create(new CreateUserDto("Orion", "orion@test.com", ""));
//        });
//    }
//
//    @Test
//    @DisplayName("Create a user with an invalid e-mail")
//    @Order(5)
//    void createUserWithInvalidEmail() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            uc.create(new CreateUserDto("Orion", "orion#test.com", "12345678"));
//        });
//    }
//
//    @Test
//    @DisplayName("Create a user with invalid password")
//    @Order(6)
//    void createUserWithInvalidPasswordTest() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            uc.create(new CreateUserDto("Orion", "orion@test.com", "12345"));
//        });
//    }
//
//    @Test
//    @DisplayName("Create a user with a null name")
//    @Order(7)
//    void createUserWithNullName() {
//        Assertions.assertThrows(NullPointerException.class, () -> {
//            uc.create(new CreateUserDto(null, "orion#test.com", "12345678"));
//        });
//    }

}
