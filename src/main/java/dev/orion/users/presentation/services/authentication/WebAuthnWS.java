package dev.orion.users.presentation.services.authentication;

import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.net.URI;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.logging.Logger;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.data.interfaces.WebAuthnCredentialRepository;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.model.WebAuthnCredential;
import dev.orion.users.domain.usecases.UpdateUser;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.security.webauthn.WebAuthnAuthenticationMechanism;
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

    @Inject
    WebAuthnAuthenticationMechanism authMech;

    @Inject
    WebAuthnCredentialRepository webAuthnCredentialRepository;
    @Inject
    protected UpdateUser updateUserUseCase;

    private static final Logger LOG = Logger.getLogger(WebAuthnWS.class);

    @Path("/webauthn/login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
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

        return userRepository.findUserByEmail(userName)
                .flatMap(user -> {
                    if (user == null) {
                        // Invalid user
                        return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
                    }
                    LOG.info(user.getEmail());
                    Uni<Authenticator> authenticator = this.webAuthnSecurity.login(webAuthnResponse, ctx);
                    return authenticator
                            // bump the auth counter
                            .invoke(auth -> user.webAuthnCredential.counter = auth.getCounter())
                            .map(auth -> {
                                // make a login cookie
                                this.webAuthnSecurity.rememberUser(auth.getUserName(), ctx);
                                return Response.ok().entity(user).build();
                                // NewCookie cookie = null;
                                // return Response.seeOther(URI.create("/")).cookie(cookie).build();
                            })
                            // handle login failure
                            .onFailure().recoverWithItem(x -> Response.status(Status.BAD_REQUEST).build());

                });
    }

    @Path("/webauthn/register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @WithSession
    public Uni<Response> register(@RestForm String userName,
            @BeanParam WebAuthnRegisterResponse webAuthnResponse,
            RoutingContext ctx) {

        // // Input validation
        if (userName == null || userName.isEmpty()
                || !webAuthnResponse.isSet()
                || !webAuthnResponse.isValid()) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        return userRepository.findUserByEmail(userName)
                .flatMap((user) -> {
                    if (user == null) {
                        return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
                    }

                    Uni<Authenticator> authenticator = this.webAuthnSecurity.register(webAuthnResponse, ctx);

                    return authenticator
                            // store the user
                            .flatMap(auth -> {
                                WebAuthnCredential credential = new WebAuthnCredential(auth, user);
                                return credential.persist().flatMap(c -> updateUserUseCase.updateUser(user));
                            })
                            .map(newUser -> {
                                // make a login cookie
                                // NewCookie cookie = null;
                                // return Response.seeOther(URI.create("/")).cookie(cookie).build();
                                this.webAuthnSecurity.rememberUser(newUser.getEmail(), ctx);
                                return Response.ok().build();
                            });
                    // handle login failure
                    // .onFailure().recoverWithItem(x -> {
                    // // make a proper error response
                    // return Response.status(Status.BAD_REQUEST).build();
                    // });
                });
    }
}