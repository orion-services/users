package dev.orion.users.usecase;

import dev.orion.users.infra.repository.Repository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ListUserTest {

    @Mock
    Repository repository;

    @InjectMocks
    UseCase uc = new ListUser();

}
