package dev.orion.users.data.handlers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
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

    protected Long expiresInMin = 30L;

    /**
     * Creates a JWT (JSON Web Token) to a user.
     *
     * @param user : The user object
     *
     * @return Returns the JWT
     */
    public String getAccessToken(User user) {
        return Jwt.issuer(issuer.orElse("http://localhost:8080"))
                .upn(user.getEmail().getAddress())
                .claim("userId", user.getUserId())
                .expiresIn(expiresInMin) // expires in 30 minutes
                .groups(new HashSet<>(Arrays.asList("user")))
                .claim(Claims.c_hash, user.getUserId())
                .sign();
    }

    public String getRefreshToken(User user) {
        Instant now = Instant.now();
        return Jwt.issuer(issuer.orElse(
                "http://localhost:8080"))
                .claim("userId", user.getUserId())
                .expiresIn(Date.from(now.plus(1, ChronoUnit.DAYS)).getTime())// refresh token for 1 day.
                .sign();
    }
}
