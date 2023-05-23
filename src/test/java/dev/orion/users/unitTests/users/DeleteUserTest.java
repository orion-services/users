package dev.orion.users.unitTests.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.data.usecases.DeleteUserImpl;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import io.smallrye.mutiny.Uni;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class DeleteUserTest {

    @InjectMocks
    private UserRepository repository;

    @InjectMocks
    private DeleteUserImpl deleteUserUseCase;

    @BeforeAll
    void setUp() {
        repository = mock(UserRepositoryImpl.class);
    }

    @Test
    @Order(1)
    void testDeleteUser() {
        String email = "user@example.com";
        Uni<Void> expectedUni = Uni.createFrom().voidItem();

        when(repository.deleteUser(email)).thenReturn(expectedUni);

        Uni<Void> resultUni = deleteUserUseCase.deleteUser(email);

        assertEquals(expectedUni, resultUni);
    }

    @Test
    @Order(2)
    void testDeleteUserWithBlankEmail() {
        String email = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            deleteUserUseCase.deleteUser(email);
        });

        assertEquals("Email can not be blank", exception.getMessage());
    }
}
