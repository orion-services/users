package dev.orion.users.infra.panache.repositories;

import dev.orion.users.data.interfaces.ClientRepository;
import dev.orion.users.domain.models.Client;
import dev.orion.users.infra.panache.entities.ClientPanacheEntity;
import org.apache.commons.codec.digest.DigestUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class ClientPanacheRepository implements ClientRepository {

    @Override
    @Transactional
    public Client create(Client client) {
        ClientPanacheEntity clientPanache = new ClientPanacheEntity();
        clientPanache.setName(client.getName());
        clientPanache.setRedirectUri(client.getUri());
        clientPanache.setClientSecret(DigestUtils.sha256Hex(client.getName()));
        return clientPanache.toClient();
    }

    @Override
    @Transactional
    public Client findById(String id) {
        ClientPanacheEntity clientPanache = ClientPanacheEntity.find("clientId",id).firstResult();
        return clientPanache == null ? null : clientPanache.toClient();
    }

    @Override
    public Client update(Client client) {
        ClientPanacheEntity clientPanache = ClientPanacheEntity.find("clientId",client.getClientId()).firstResult();
        if (clientPanache == null) {
            throw new NotFoundException("Client not found");
        }
        clientPanache.scope = client.getScope() == null ? clientPanache.scope : client.getScope();
        clientPanache.redirectUri = client.getUri() == null ? clientPanache.redirectUri : client.getUri();
        clientPanache.authorizedGrantTypes = client.getAuthorizedGrantTypes() == null ? clientPanache.authorizedGrantTypes : client.getAuthorizedGrantTypes();
        return clientPanache.toClient();
    }


}
