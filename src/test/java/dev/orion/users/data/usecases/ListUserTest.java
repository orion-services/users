package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.UserQueryDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.ListUser;
import dev.orion.users.domain.vo.Email;
import io.quarkus.test.junit.mockito.InjectSpy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ListUserTest {
    @Mock
    UserRepository repository;

    @Spy
    @InjectMocks
    ListUser listUser = new ListUserImpl(repository);

    public void setup() {
        // if we don't call below, we will get NullPointerException
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("List All Users")
    @Order(1)
    void listAllUsersTest() {
        UserQueryDto query = new UserQueryDto();

        List<User> userList = List.of(
                createUserMock("123", "Orion",
                        "Orion@email.com"),
                createUserMock("123", "Orion2",
                        "Orion2@email.com"));

        Mockito.when(repository.findByQuery(any(UserQueryDto.class))).thenReturn(userList);

        List<User> users = listUser.list(query);

        Mockito.verify(repository, Mockito.times(1)).findByQuery(query);
        Mockito.verify(listUser, Mockito.atLeast(1)).list(query);

        assertNotNull(users);
        assertEquals(users.size() == 2, true);
    }

    @Test
    @DisplayName("List Users by name")
    @Order(2)
    void listUsersByNameTest() {
        UserQueryDto query = new UserQueryDto();
        query.name = "Orion";

        List<User> userList = List.of(createUserMock("123", "Orion",
                "Orion@email.com"));
        Mockito.when(repository.findByQuery(query)).thenReturn(userList);
        List<User> users = listUser.list(query);

        assertNotNull(users);
        assertEquals(users.size() == 1, true);
    }

    @Test
    @DisplayName("List users by id")
    @Order(3)
    void listUsersByHashTest() {
        UserQueryDto query = new UserQueryDto();
        query.hash = "123";

        List<User> userList = List.of(createUserMock("123", "Orion",
                "Orion@email.com"));
        Mockito.when(repository.findByQuery(query)).thenReturn(userList);

        List<User> users = listUser.list(query);

        Mockito.verify(repository, Mockito.times(1)).findByQuery(query);

        assertEquals(users.get(0).getHash(), query.hash);
    }

    @Test
    @DisplayName("List users by id")
    @Order(4)
    void listUsersByEmailTest() {
        UserQueryDto query = new UserQueryDto();
        query.email = "Orion@email.com";

        List<User> userList = List.of(createUserMock("123", "Orion",
                "Orion@email.com"));

        Mockito.when(repository.findByQuery(query)).thenReturn(userList);

        List<User> users = listUser.list(query);

        Mockito.verify(listUser, Mockito.times(1)).list(query);
        Mockito.verify(repository, Mockito.times(1)).findByQuery(query);

        assertEquals(users.get(0).getEmail().getAddress(), query.email);
    }

    private User createUserMock(String hash, String name, String email) {
        User user = new User();
        user.setHash(hash);
        user.setName(name);
        user.setEmail(new Email(email));
        return user;
    }
}
