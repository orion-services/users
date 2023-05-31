package dev.orion.users.infra.repository;

import java.util.List;

import dev.orion.users.data.interfaces.WebAuthnCredentialRepository;
import dev.orion.users.domain.model.WebAuthnCredential;
import io.smallrye.mutiny.Uni;

public class WebAuthCredentialRepositoryImpl implements WebAuthnCredentialRepository {

    @Override
    public Uni<List<WebAuthnCredential>> findByUserName(String userName) {
        return list("userName", userName);
    }

    @Override
    public Uni<List<WebAuthnCredential>> findByCredID(String credID) {
        return list("credID", credID);
    }

}
