package dev.orion.users.data.usecases;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled
public class BlockUserTest {

    // @Mock
    // UserRepository repository;

    // @InjectMocks
    // BlockUser blockUser = new BlockUserImpl(repository);

    @Test
    @DisplayName("Should block a user")
    @Order(1)
    void shouldBlockAUser() {

    }
}
