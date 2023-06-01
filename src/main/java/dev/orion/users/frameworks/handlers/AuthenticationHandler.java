/**
 * @License
 * Copyright 2023 Orion Services @ https://github.com/orion-services
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
package dev.orion.users.frameworks.handlers;

import java.util.HashSet;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import dev.orion.users.adapters.gateways.entities.UserEntity;
import dev.orion.users.frameworks.mail.MailTemplate;
import dev.orion.users.frameworks.rest.ServiceException;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AuthenticationHandler {
    /** Fault tolerance default delay. */
    protected static final long DELAY = 2000;

    /** Configure the issuer for JWT generation. */
    @ConfigProperty(name = "users.issuer")
    Optional<String> issuer;

    /** Set the validation url. */
    @ConfigProperty(name = "users.email.validation.url",
        defaultValue = "http://localhost:8080/api/users/validateEmail")
    String validateURL;

    /**
     * Creates a JWT (JSON Web Token) to a user.
     *
     * @param user : The user object
     *
     * @return Returns the JWT
     */
    public String generateJWT(final UserEntity user) {
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
     * @param email    : Request e-mail
     * @param jwtEmail : JWT e-mail
     * @return true if the e-mails are the same
     * @throws ServiceException Throw an exception (HTTP 400) if the e-mails are
     * different, indicating that possibly the JWT is outdated.
     */
    public boolean checkTokenEmail(final String email,
            final String jwtEmail) {
        if (!email.equals(jwtEmail)) {
            throw new ServiceException("JWT outdated",
                Response.Status.BAD_REQUEST);
        }
        return true;
    }

    /**
     * Send a message to the user validates the e-mail.
     *
     * @param user : A user object
     * @return Return a Uni<UserEntity> after to send an e-mail.
     */
    public Uni<UserEntity> sendValidationEmail(final UserEntity user) {
        StringBuilder url = new StringBuilder();
        url.append(validateURL);
        url.append("?code=" + user.getEmailValidationCode());
        url.append("&email=" + user.getEmail());

        return MailTemplate.validateEmail(url.toString())
                .to(user.getEmail())
                .subject("E-mail confirmation")
                .send()
                .onItem().ifNotNull()
                .transform(item -> user);
    }

}
