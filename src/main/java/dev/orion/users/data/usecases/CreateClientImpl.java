package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.ClientRepository;
import dev.orion.users.data.mappers.ClientMapper;
import dev.orion.users.domain.dto.CreateClientDto;
import dev.orion.users.domain.models.Client;
import dev.orion.users.domain.usecases.CreateClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class CreateClientImpl implements CreateClient {
    @Inject
    protected ClientRepository repository;

    @Override
    @Transactional
    public Client create(CreateClientDto clientDto) {
        Client clientToBeCreated = ClientMapper.toEntity(clientDto);

        return this.repository.create(clientToBeCreated);
    }
}
