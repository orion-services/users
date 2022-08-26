package dev.orion.users.usecase;

import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.Repository;
import dev.orion.users.validation.dto.UserQuery;
import io.smallrye.mutiny.Uni;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
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

    @Mock
    Repository repository;

    @InjectMocks
    ListUser listUsersUC;

    @Test
    @DisplayName("List All Users")
    @Order(1)
    void listAllUsersTest() {
        UserQuery query = new UserQuery();
        Mockito.when(repository.listByQuery(query)).thenReturn(Uni.createFrom().item(new ArrayList<User>()));
        Assertions.assertEquals(1, repository.count());
        Uni<List<User>> users = listUsersUC.listUser(query);

        assertNotNull(users);
    }

    @Test
    @DisplayName("List Users by name")
    @Order(2)
    void listUsersByNameTest() {
        UserQuery query = new UserQuery();
        query.setUserId("");
        query.setUserName("Teste");

        Mockito.when(repository.listByQuery(query)).thenReturn(Uni.createFrom().item(new ArrayList<User>()));
        Assertions.assertEquals(1, repository.count());

        Uni<List<User>> users = listUsersUC.listUser(query);
        assertNotNull(users);
    }

    @Test
    @DisplayName("List users by id")
    @Order(3)
    void listUsersByIdTest() {
        UserQuery query = new UserQuery();
        query.setUserId("Teste");
        query.setUserName("");

        Mockito.when(repository.listByQuery(query)).thenReturn(Uni.createFrom().item(new ArrayList<User>()));

        Uni<List<User>> users = listUsersUC.listUser(query);
        assertNotNull(users);
    }
}
