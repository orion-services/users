package dev.orion.users.data.usecases;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled
public class RemoveUserTest {

    // @Mock
    // UserRepository repository;

    // @InjectMocks
    // RemoveUser removeUser = new RemoveUserImpl(repository);

    @Test
    @DisplayName("Should remove a user")
    @Order(1)
    void shouldRemoveAUser() {

    }

    @Test
    @DisplayName("Should not remove a user already")
    @Order(1)
    void shouldNotRemoveAUserAlreadyRemoved() {

    }
}
