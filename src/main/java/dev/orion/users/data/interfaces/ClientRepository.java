package dev.orion.users.data.interfaces;

import dev.orion.users.domain.models.Client;

public interface ClientRepository {

    Client create (Client client);

    Client findById (String id);

}
