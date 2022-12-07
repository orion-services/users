package dev.orion.users.data.mappers;

import dev.orion.users.domain.dto.client.CreateClientDto;
import dev.orion.users.domain.models.Client;

public class ClientMapper {
    public static Client toEntity(CreateClientDto createClient){
        return  new Client(
                createClient.name,
                createClient.uri
        );
    }
}
