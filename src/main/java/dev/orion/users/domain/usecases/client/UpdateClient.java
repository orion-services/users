package dev.orion.users.domain.usecases.client;

import dev.orion.users.domain.dto.client.UpdateClientDto;
import dev.orion.users.domain.models.Client;

public interface UpdateClient {
    Client update (UpdateClientDto updateClientDto);
}
