package dev.orion.users.unitTests.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

import dev.orion.users.data.usecases.DeleteUserImpl;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class DeleteUserTest {

    @InjectMocks
    private UserRepositoryImpl repository;

    @InjectMocks
    private DeleteUserImpl deleteUserUseCase;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepositoryImpl.class);
        deleteUserUseCase = mock(DeleteUserImpl.class);
        deleteUserUseCase = mock(DeleteUserImpl.class);
    }

    @Test
    @Order(1)
    void testDeleteUser() {
        String email = "user@example.com";

        Mockito.when(deleteUserUseCase.deleteUser(anyString())).thenReturn(Uni.createFrom().voidItem());
        Mockito.when(repository.deleteUser(anyString())).thenReturn(Uni.createFrom().voidItem());

        Uni<Void> expectedUni = Uni.createFrom().voidItem();

        Uni<Void> resultUni = deleteUserUseCase.deleteUser(email);

        assertEquals(expectedUni, resultUni);
    }

    @Test
    @Order(2)
    void testDeleteUserWithBlankEmail() {
        String email = "";
        Mockito.when(deleteUserUseCase.deleteUser(email)).thenCallRealMethod();

        assertThrows(IllegalArgumentException.class, () -> {
            deleteUserUseCase.deleteUser(email);
        });

    }
}
