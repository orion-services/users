package dev.orion.users.domain.usecases;

import dev.orion.users.domain.dto.UpdateClientDto;
import dev.orion.users.domain.models.Client;

public interface UpdateClient {
    Client update (UpdateClientDto updateClientDto);
}
