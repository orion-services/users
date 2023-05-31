package dev.orion.users.data.interfaces;

import java.util.List;

import dev.orion.users.domain.model.WebAuthnCredential;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface WebAuthnCredentialRepository extends PanacheRepository<WebAuthnCredential> {
    Uni<List<WebAuthnCredential>> findByUserName(String userName);

    Uni<List<WebAuthnCredential>> findByCredID(String credID);
}
