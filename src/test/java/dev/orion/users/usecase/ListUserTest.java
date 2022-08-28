package dev.orion.users.usecase;

import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.Repository;
import dev.orion.users.validation.dto.UserQuery;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.smallrye.mutiny.Uni;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.spy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ListUserTest {

    @InjectSpy
    @Mock
    Repository repository;

    @InjectSpy
    @InjectMocks
    ListUser listUsersUC = new ListUser();

    @Test
    @DisplayName("List All Users")
    @Order(1)
    void listAllUsersTest() {
        UserQuery query = new UserQuery();
        query.setEmail(null);
        query.setHash(null);
        query.setName(null);
        Uni<User> user = Uni.createFrom().item(new User());
        Uni<List<User>> userList = Uni.join().all(user).andCollectFailures();

        Mockito.when(repository.listByQuery(query)).thenReturn(userList);

        Uni<List<User>> users = listUsersUC.listUser(query);
        Mockito.verify(repository, Mockito.times(1)).listByQuery(query);

        assertEquals(userList, users);
    }

    @Test
    @DisplayName("List Users by name")
    @Order(2)
    void listUsersByNameTest() {
        UserQuery query = new UserQuery();
        query.setName("Test");

        Uni<User> user = Uni.createFrom().item(this.createUserMock("Teste", "Teste", "teste@email.com"));

        Uni<List<User>> userList = Uni.join().all(user).andCollectFailures();

        Mockito.when(repository.listByQuery(query)).thenReturn(userList);

        Uni<List<User>> users = listUsersUC.listUser(query);

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
        query.setHash("Teste");

        Uni<User> user = Uni.createFrom().item(this.createUserMock("Teste", "Teste", "teste@email.com"));

        Uni<List<User>> userList = Uni.join().all(user).andCollectFailures();

        Mockito.when(repository.listByQuery(query)).thenReturn(userList);

        Uni<List<User>> users = listUsersUC.listUser(query);

        Mockito.verify(repository, Mockito.times(1)).listByQuery(query);

        assertNotNull(userList);
        assertNotNull(users);
        assertEquals(users, userList);
    }

    private User createUserMock(String hash, String name, String email) {
        User user = new User();
        user.setHash(hash);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
