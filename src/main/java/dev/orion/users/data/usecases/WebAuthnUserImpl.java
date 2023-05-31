package dev.orion.users.data.usecases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.data.interfaces.WebAuthnCredentialRepository;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.model.WebAuthnCertificate;
import dev.orion.users.domain.model.WebAuthnCredential;
import io.quarkus.security.webauthn.WebAuthnUserProvider;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.auth.webauthn.AttestationCertificates;
import io.vertx.ext.auth.webauthn.Authenticator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class WebAuthnUserImpl implements WebAuthnUserProvider {

    @Inject
    WebAuthnCredentialRepository webAuthnCredentialRepository;

    @Inject
    UserRepository userRepository;

    @Override
    public Uni<List<Authenticator>> findWebAuthnCredentialsByUserName(String userName) {
        return webAuthnCredentialRepository.findByUserName(userName)
                .flatMap(WebAuthnUserImpl::toAuthenticators);
    }

    @Override
    public Uni<List<Authenticator>> findWebAuthnCredentialsByCredID(String credID) {
        return webAuthnCredentialRepository.findByCredID(credID)
                .flatMap(WebAuthnUserImpl::toAuthenticators);
    }

    @Override
    public Uni<Void> updateOrStoreWebAuthnCredentials(Authenticator authenticator) {
        // leave the scooby user to the manual endpoint, because if we do it here it
        // will be
        // created/udated twice
        if (authenticator.getUserName().equals("scooby"))
            return Uni.createFrom().nullItem();
        return userRepository.findUserByEmail(authenticator.getUserName())
                .flatMap(user -> {
                    // new user
                    if (user == null) {
                        User newUser = new User();
                        newUser.setEmail(authenticator.getUserName());
                        WebAuthnCredential credential = new WebAuthnCredential(authenticator, newUser);
                        return credential.persist()
                                .flatMap(c -> newUser.persist())
                                .onItem().ignore().andContinueWithNull();
                    } else {
                        // existing user
                        user.webAuthnCredential.counter = authenticator.getCounter();
                        return Uni.createFrom().nullItem();
                    }
                });
    }

    private static Uni<List<Authenticator>> toAuthenticators(List<WebAuthnCredential> dbs) {
        // can't call combine/uni on empty list
        if (dbs.isEmpty())
            return Uni.createFrom().item(Collections.emptyList());
        List<Uni<Authenticator>> ret = new ArrayList<>(dbs.size());
        for (WebAuthnCredential db : dbs) {
            ret.add(toAuthenticator(db));
        }
        return Uni.combine().all().unis(ret).combinedWith(f -> (List) f);
    }

    private static Uni<Authenticator> toAuthenticator(WebAuthnCredential credential) {
        return credential.fetch(credential.x5c)
                .map(x5c -> {
                    Authenticator ret = new Authenticator();
                    ret.setAaguid(credential.aaguid);
                    AttestationCertificates attestationCertificates = new AttestationCertificates();
                    attestationCertificates.setAlg(credential.alg);
                    List<String> x5cs = new ArrayList<>(x5c.size());
                    for (WebAuthnCertificate webAuthnCertificate : x5c) {
                        x5cs.add(webAuthnCertificate.x5c);
                    }
                    ret.setAttestationCertificates(attestationCertificates);
                    ret.setCounter(credential.counter);
                    ret.setCredID(credential.credID);
                    ret.setFmt(credential.fmt);
                    ret.setPublicKey(credential.publicKey);
                    ret.setType(credential.type);
                    ret.setUserName(credential.userName);
                    return ret;
                });
    }

    @Override
    public Set<String> getRoles(String userId) {
        if (userId.equals("admin")) {
            Set<String> ret = new HashSet<>();
            ret.add("user");
            ret.add("admin");
            return ret;
        }
        return Collections.singleton("user");
    }
}
