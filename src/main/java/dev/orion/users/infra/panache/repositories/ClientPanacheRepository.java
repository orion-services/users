package dev.orion.users.infra.panache.repositories;

import dev.orion.users.data.interfaces.ClientRepository;
import dev.orion.users.domain.models.Client;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class ClientPanacheRepository implements ClientRepository {

    @Override
    @Transactional
    public Client create(Client client) {
        return null;
    }

    @Override
    @Transactional
    public Client findById(String id) {
        return null;
    }
}
