package dev.orion.users.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.ManyToOne;

public class WebAuthnCertificate extends PanacheEntity {

    @ManyToOne
    public WebAuthnCredential webAuthnCredential;

    /**
     * The list of X509 certificates encoded as base64url.
     */
    public String x5c;
}