package dev.orion.users.presentation.services.authentication;

import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.jboss.resteasy.reactive.RestForm;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.model.WebAuthnCredential;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.security.webauthn.WebAuthnLoginResponse;
import io.quarkus.security.webauthn.WebAuthnRegisterResponse;
import io.quarkus.security.webauthn.WebAuthnSecurity;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.auth.webauthn.Authenticator;
import io.vertx.ext.web.RoutingContext;

@Path("api/users")
public class WebAuthnWS {

    @Inject
    WebAuthnSecurity webAuthnSecurity;

    @Inject
    UserRepository userRepository;

    @Path("webAuthn/login")
    @POST
    @WithSession
    public Uni<Response> login(@RestForm String userName,
            @BeanParam WebAuthnLoginResponse webAuthnResponse,
            RoutingContext ctx) {
        // Input validation
        if (userName == null || userName.isEmpty()
                || !webAuthnResponse.isSet()
                || !webAuthnResponse.isValid()) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        Uni<User> userUni = userRepository.findUserByEmail(userName);
        return userUni.flatMap(user -> {
            if (user == null) {
                // Invalid user
                return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
            }
            Uni<Authenticator> authenticator = this.webAuthnSecurity.login(webAuthnResponse, ctx);

            return authenticator
                    // bump the auth counter
                    .invoke(auth -> user.webAuthnCredential.counter = auth.getCounter())
                    .map(auth -> {
                        // make a login cookie
                        this.webAuthnSecurity.rememberUser(auth.getUserName(), ctx);
                        return Response.ok().build();
                    })
                    // handle login failure
                    .onFailure().recoverWithItem(x -> {
                        // make a proper error response
                        return Response.status(Status.BAD_REQUEST).build();
                    });

        });
    }

    @Path("webAuthn/register")
    @POST
    @WithSession
    public Uni<Response> register(@RestForm String userName,

            @BeanParam WebAuthnRegisterResponse webAuthnResponse,
            RoutingContext ctx) {
        // Input validation
        if (userName == null || userName.isEmpty()
                || !webAuthnResponse.isSet()
                || !webAuthnResponse.isValid()) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        Uni<User> userUni = userRepository.findUserByEmail(userName);
        return userUni.flatMap(user -> {
            if (user != null) {
                // Duplicate user
                return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
            }
            Uni<Authenticator> authenticator = this.webAuthnSecurity.register(webAuthnResponse, ctx);

            return authenticator
                    // store the user
                    .flatMap(auth -> {
                        User newUser = new User();
                        newUser.setEmail(auth.getUserName());
                        WebAuthnCredential credential = new WebAuthnCredential(auth, newUser);
                        return credential.persist()
                                .flatMap(c -> newUser.<User>persist());

                    })
                    .map(newUser -> {
                        // make a login cookie
                        this.webAuthnSecurity.rememberUser(newUser.getEmail(), ctx);
                        return Response.ok().build();
                    })
                    // handle login failure
                    .onFailure().recoverWithItem(x -> {
                        // make a proper error response
                        return Response.status(Status.BAD_REQUEST).build();
                    });

        });
    }
}