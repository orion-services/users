package dev.orion.users.domain.usecases;

import dev.orion.users.domain.dto.CreateClientDto;
import dev.orion.users.domain.models.Client;

public interface CreateClient {
    Client create (CreateClientDto clientDto);
}
