package dev.orion.users.presentation.services.authentication;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Email;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.logging.Logger;

import dev.orion.users.data.handlers.AuthenticationHandler;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.AuthenticationDTO;
import dev.orion.users.domain.model.WebAuthnCredential;
import dev.orion.users.domain.usecases.CreateUser;
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
@RequestScoped
public class WebAuthnWS {

    @Inject
    WebAuthnSecurity webAuthnSecurity;

    @Inject
    UserRepository userRepository;

    @Inject
    WebAuthnAuthenticationMechanism authMech;

    @Inject
    private AuthenticationHandler authHandler;

    @Inject
    protected UpdateUser updateUserUseCase;

    @Inject
    protected CreateUser createUserUseCase;

    private static final Logger LOG = Logger.getLogger(WebAuthnWS.class);

    @Path("/webauthn/login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<Response> login(@RestForm @Email String userEmail,
            @BeanParam WebAuthnLoginResponse webAuthnResponse,
            RoutingContext ctx) {

        // Input validation
        if (userEmail == null || userEmail.isEmpty()
                || !webAuthnResponse.isSet()
                || !webAuthnResponse.isValid()) {

            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        return userRepository.findUserByEmail(userEmail)
                .flatMap(user -> {
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
                                String token = authHandler.generateJWT(user);
                                AuthenticationDTO authDto = new AuthenticationDTO();
                                authDto.setToken(token);
                                authDto.setUser(user);
                                LOG.info(authDto);
                                return Response.ok().entity(authDto).build();
                            })
                            // handle login failure
                            .onFailure().recoverWithItem(x -> {
                                LOG.error(x.getMessage());
                                return Response.status(Status.BAD_REQUEST).build();
                            });

                });
    }

    @Path("/webauthn/activate")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @WithSession
    // @RolesAllowed("user")
    public Uni<Response> activate(
            @RestForm @Email String userEmail,
            @BeanParam WebAuthnRegisterResponse webAuthnResponse,
            RoutingContext ctx) {

        if (userEmail == null || userEmail.isEmpty()
                || !webAuthnResponse.isSet()
                || !webAuthnResponse.isValid()) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        return userRepository.findUserByEmail(userEmail)
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
                                this.webAuthnSecurity.rememberUser(newUser.getEmail(), ctx);
                                return Response.ok().entity(newUser).build();
                            })
                            // handle login failure
                            .onFailure().recoverWithItem(x -> {
                                // make a proper error response
                                LOG.error(x.getMessage());
                                return Response.status(Status.BAD_REQUEST).build();
                            });
                });
    }

    @Path("/webauthn/register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @WithSession
    public Uni<Response> register(
            @RestForm String userEmail,
            @RestForm String name,
            @BeanParam WebAuthnRegisterResponse webAuthnResponse,
            RoutingContext ctx) {

        // // Input validation
        if (userEmail == null || userEmail.isEmpty()
                || !webAuthnResponse.isSet()
                || !webAuthnResponse.isValid()) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        return userRepository.findUserByEmail(userEmail)
                .onItem()
                .ifNull()
                .switchTo(createUserUseCase.createUser(name, userEmail, userEmail))
                .onItem()
                .ifNotNull()
                .call(user -> this.authHandler.sendValidationEmail(user))
                .onItem()
                .transformToUni(user -> {
                    Uni<Authenticator> authenticator = this.webAuthnSecurity.register(webAuthnResponse, ctx);

                    return authenticator
                            // store the user
                            .flatMap(auth -> {
                                WebAuthnCredential credential = new WebAuthnCredential(auth, user);
                                return credential.persist().flatMap(c -> updateUserUseCase.updateUser(user));
                            })
                            .map(newUser -> {
                                // make a login cookie
                                this.webAuthnSecurity.rememberUser(newUser.getEmail(), ctx);
                                return Response.ok().entity(newUser).build();
                            })
                            // handle login failure
                            .onFailure().recoverWithItem(x -> {
                                // make a proper error response
                                LOG.error(x.getMessage());
                                return Response.status(Status.BAD_REQUEST).build();
                            });
                });
    }
}