/**
 * @License
 * Copyright 2022 Orion Services @ https://github.com/orion-services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.orion.users.ws;

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
    @ConfigProperty(name = "users.issuer")
    Optional<String> issuer;

    /**
     * Creates a JWT (JSON Web Token) to a user.
     *
     * @param user : The user object
     *
     * @return Returns the JWT
     */
    public String generateJWT(final User user) {
        return Jwt.issuer(issuer.orElse("orion-users"))
            .upn(user.getEmail())
            .groups(new HashSet<>(user.getRoleList()))
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
