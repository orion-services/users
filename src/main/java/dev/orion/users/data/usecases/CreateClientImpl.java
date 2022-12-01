package dev.orion.users.data.usecases;

import dev.orion.users.domain.dto.CreateClientDto;
import dev.orion.users.domain.models.Client;
import dev.orion.users.domain.usecases.CreateClient;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateClientImpl implements CreateClient {
    @Override
    public Client create(CreateClientDto clientDto) {
        return null;
    }
}
