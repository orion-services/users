package dev.orion.users.ws;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import dev.orion.users.model.User;
import dev.orion.users.ws.exceptions.UserWSException;
import io.smallrye.jwt.build.Jwt;
/**
 * Common Web Service code.
 */
public class BaseWS {

    /** Configure the issuer for JWT generation. */
    @ConfigProperty(name = "user.issuer")
    private Optional<String> issuer;

    /**
     * Creates a JWT (JSON Web Token) to a user.
     *
     * @param user : The user object
     *
     * @return Returns the JWT
     */
    public String generateJWT(final User user) {
        return Jwt.issuer(issuer.orElse("http://localhost:8080"))
            .upn(user.getEmail())
            .groups(new HashSet<>(Arrays.asList("user")))
            .claim(Claims.c_hash, user.getHash())
            .claim(Claims.email, user.getEmail())
            .sign();
    }

    /**
     * Verifies if the e-mail from the jwt is the same from request.
     *
     * @param email     : Request e-mail
     * @param jwtEmail  : JWT e-mail
     * @return true if the e-mails are the same
     * @throws UserWSException Throw an exception (HTTP 400) if the e-mails are
     * different, indicating that possibly the JWT is outdated.
     */
    protected boolean checkTokenEmail(final String email,
        final String jwtEmail) {

        if (!email.equals(jwtEmail)) {
            throw new UserWSException("JWT outdated",
                Response.Status.BAD_REQUEST);
        }
        return true;
    }

}
