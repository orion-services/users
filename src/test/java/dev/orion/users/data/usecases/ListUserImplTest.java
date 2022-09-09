package dev.orion.users.data.usecases;

import dev.orion.users.domain.models.User;
import dev.orion.users.domain.vo.Email;
import io.quarkus.test.junit.mockito.InjectSpy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ListUserImplTest {

    // @InjectSpy
    // @Mock
    // UserPanacheRepository repository;

    // @InjectSpy
    // @InjectMocks
    // UseCase useCase = new ListUserImpl();

    // @Test
    // @DisplayName("List All Users")
    // @Order(1)
    // void listAllUsersTest() {
    // UserQuery query = new UserQuery();
    //
    // Multi<User> userList = Multi.createFrom().item(createUserMock("123", "Orion",
    // "Orion@email.com"));
    // Mockito.when(repository.listByQuery(query)).thenReturn(userList);
    //
    // List<User> users = useCase.listUser(query);
    //
    // Mockito.verify(repository, Mockito.times(1)).listByQuery(query);
    //
    // assertNotNull(query);
    // assertNotNull(users);
    // assertEquals(users, userList);
    // }

    // @Test
    // @DisplayName("List Users by name")
    // @Order(2)
    // void listUsersByNameTest() {
    // UserQuery query = new UserQuery();
    // query.setName("Orion");
    //
    // Multi<User> userList = Multi.createFrom().item(createUserMock("123", "Orion",
    // "Orion@email.com"));
    // Mockito.when(repository.listByQuery(query)).thenReturn(userList);
    //
    // Multi<User> users = useCase.listUser(query);
    //
    // Mockito.verify(repository, Mockito.times(1)).listByQuery(query);
    //
    // assertNotNull(userList);
    // assertNotNull(users);
    //
    // assertTrue((BooleanSupplier) Stream.of(users));
    //
    // assertEquals(users, userList);
    // }
    //
    // @Test
    // @DisplayName("List users by id")
    // @Order(3)
    // void listUsersByIdTest() {
    // UserQuery query = new UserQuery();
    // query.setHash("123");
    //
    // Multi<User> userList = Multi.createFrom().item(createUserMock("123", "Orion",
    // "Orion@email.com"));
    // Mockito.when(repository.listByQuery(query)).thenReturn(userList);
    //
    // Multi<User> users = useCase.listUser(query);
    //
    // Mockito.verify(repository, Mockito.times(1)).listByQuery(query);
    //
    // assertNotNull(userList);
    // assertNotNull(users);
    // assertEquals(userList, users);
    // }

    private User createUserMock(String hash, String name, String email) {
        User user = new User();
        // user.setHash(hash);
        user.setName(name);
        user.setEmail(new Email(email));
        return user;
    }
}
