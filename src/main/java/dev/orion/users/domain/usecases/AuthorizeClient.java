package dev.orion.users.domain.usecases;

import dev.orion.users.domain.dto.AuthorizeClientDto;
import dev.orion.users.domain.models.AuthorizationCode;

public interface AuthorizeClient {
    AuthorizationCode authorize(AuthorizeClientDto authorizeClientDto);
}
