package dev.orion.users.data.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.usecases.BlockUser;
import io.quarkus.test.Mock;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
