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
package dev.orion.users.ws.authentication;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;

import dev.orion.users.dto.AuthenticationDTO;
import dev.orion.users.usecase.UseCase;
import dev.orion.users.usecase.UserUC;
import dev.orion.users.ws.BaseWS;
import dev.orion.users.ws.exceptions.UserWSException;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;

/**
 * Social Authenticate.
 */
@Path("/api/users")
public class SocialAuthenticationWS extends BaseWS {

    /** Business logic. */
    private UseCase uc = new UserUC();

     /**
     * ID Token issued by the OpenID Connect Provider
     */
    @Inject
    @IdToken
    JsonWebToken idToken;

    /**
     * Authenticate and creates a user using google.
     *
     * @return The Authentication DTO in json format
     * @throws UserWSException Returns a HTTP 409 if the name already exists
     * in the database
     */
    @GET
    @Path("/google")
    @Authenticated
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<AuthenticationDTO> google() {

        // Getting information from id token
        Object gName = this.idToken.getClaim("given_name");
        String fname = this.idToken.getClaim("family_name");
        String email = this.idToken.getClaim("email");

        StringBuilder name = new StringBuilder();
        name.append(gName); name.append(" "); name.append(fname);

        try {
            return uc.createUser(name.toString(), email, true)
                .onItem().ifNotNull()
                    .transform(user -> {
                        AuthenticationDTO auth = new AuthenticationDTO();
                        auth.setToken(generateJWT(user));
                        auth.setUser(user);
                        return auth;
                    })
                .onFailure()
                    .transform(e -> {
                        throw new UserWSException(e.getMessage(),
                            Response.Status.BAD_REQUEST);
                    })
                .log();
        } catch (Exception e) {
            throw (UserWSException) new UserWSException(e.getMessage(),
                Response.Status.BAD_REQUEST);
        }
    }
}
