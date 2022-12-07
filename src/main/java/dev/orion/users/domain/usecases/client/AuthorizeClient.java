package dev.orion.users.domain.usecases.client;

import dev.orion.users.domain.dto.client.AuthorizeClientDto;
import dev.orion.users.domain.models.AuthorizationCode;

public interface AuthorizeClient {
    AuthorizationCode authorize(AuthorizeClientDto authorizeClientDto);
}
