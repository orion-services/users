package dev.orion.users.usecase;

import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.UserRepository;
import dev.orion.users.validation.dto.UserQuery;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.smallrye.mutiny.Multi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ListUserTest {

    @InjectSpy
    @Mock
    UserRepository repository;

    @InjectSpy
    @InjectMocks
    UseCase useCase = new ListUser();

    @Test
    @DisplayName("List All Users")
    @Order(1)
    void listAllUsersTest() {
        UserQuery query = new UserQuery();

        Multi<User> userList = Multi.createFrom().item(createUserMock("123", "Orion", "Orion@email.com"));
        Mockito.when(repository.listByQuery(query)).thenReturn(userList);

        Multi<User> users = useCase.listUser(query);

        Mockito.verify(repository, Mockito.times(1)).listByQuery(query);

        assertNotNull(query);
        assertNotNull(users);
        assertEquals(users, userList);
    }

    @Test
    @DisplayName("List Users by name")
    @Order(2)
    void listUsersByNameTest() {
        UserQuery query = new UserQuery();
        query.setName("Orion");

        Multi<User> userList = Multi.createFrom().item(createUserMock("123", "Orion", "Orion@email.com"));
        Mockito.when(repository.listByQuery(query)).thenReturn(userList);

        Multi<User> users = useCase.listUser(query);

        Mockito.verify(repository, Mockito.times(1)).listByQuery(query);
        assertNotNull(userList);
        assertNotNull(users);
        assertEquals(users, userList);
    }

    @Test
    @DisplayName("List users by id")
    @Order(3)
    void listUsersByIdTest() {
        UserQuery query = new UserQuery();
        query.setHash("123");

        Multi<User> userList = Multi.createFrom().item(createUserMock("123", "Orion", "Orion@email.com"));
        Mockito.when(repository.listByQuery(query)).thenReturn(userList);

        Multi<User> users = useCase.listUser(query);

        Mockito.verify(repository, Mockito.times(1)).listByQuery(query);

        assertNotNull(userList);
        assertNotNull(users);
        assertEquals(userList, users);
    }

    private User createUserMock(String hash, String name, String email) {
        User user = new User();
        user.setHash(hash);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
