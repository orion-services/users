package dev.orion.users.mocks;

import jakarta.enterprise.context.ApplicationScoped;
import dev.orion.users.data.handlers.WebAuthnSetup;
import io.quarkus.test.Mock;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.auth.webauthn.Authenticator;

@Mock
@ApplicationScoped
public class TestUserProvider extends WebAuthnSetup {
    @Override
    public Uni<Void> updateOrStoreWebAuthnCredentials(Authenticator authenticator) {
        // delegate the scooby user to the manual endpoint, because if we do it here it
        // will be
        // created/updated twice
        if (authenticator.getUserName().equals("scooby"))
            return Uni.createFrom().nullItem();
        return super.updateOrStoreWebAuthnCredentials(authenticator);
    }
}