package dev.orion.users.ws;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import dev.orion.users.model.User;
import dev.orion.users.ws.expections.UserWSException;
import io.smallrye.jwt.build.Jwt;

import javax.ws.rs.core.Response;

public class BaseWS {

    /* Configure the issuer for JWT generation. */
    @ConfigProperty(name = "user.issuer")
    protected Optional<String> issuer;

    /**
     * Creates a JWT (JSON Web Token) to a user.
     *
     * @param user : The user object
     *
     * @return Returns the JWT
     */
    protected String generateJWT(final User user) {
        return Jwt.issuer(issuer.orElse("http://localhost:8080"))
                .upn(user.getEmail())
                .groups(new HashSet<>(Arrays.asList("user")))
                .claim(Claims.c_hash, user.getHash())
                .claim(Claims.email, user.getEmail())
                .sign();
    }

    protected boolean checkTokenEmail(String email, String jwtEmail){
        if (!email.equals(jwtEmail)) {
            throw new UserWSException("JWT outdated", Response.Status.BAD_REQUEST);
        }
        return true;
    }

}
