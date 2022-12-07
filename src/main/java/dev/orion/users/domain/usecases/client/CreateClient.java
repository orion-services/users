package dev.orion.users.domain.usecases.client;

import dev.orion.users.domain.dto.client.CreateClientDto;
import dev.orion.users.domain.models.Client;

public interface CreateClient {
    Client create (CreateClientDto clientDto);
}
