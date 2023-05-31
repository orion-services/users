package dev.orion.users.data.interfaces;

import dev.orion.users.domain.model.WebAuthnCertificate;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;

public interface WebAuthnCertificateRepository extends PanacheRepository<WebAuthnCertificate> {

}
