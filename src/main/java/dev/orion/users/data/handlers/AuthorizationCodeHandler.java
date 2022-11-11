package dev.orion.users.data.handlers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import dev.orion.users.domain.models.User;
import io.smallrye.jwt.build.Jwt;

@ApplicationScoped
public class AuthorizationCodeHandler {

    @ConfigProperty(name = "AuthCodeHandler.issuer")
    public Optional<String> issuer;

    /**
     * Creates a JWT (JSON Web Token) to a user.
     *
     * @param user : The user object
     *
     * @return Returns the JWT
     */
    protected String generateJWT(final User user) {
        return Jwt.issuer(issuer.orElse("http://localhost:8080"))
                .upn(user.getEmail().getAddress())
                .groups(new HashSet<>(Arrays.asList("user")))
                .claim(Claims.c_hash, user.getHash())
                .sign();
    }
}
