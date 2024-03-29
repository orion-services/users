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
package dev.orion.users.service;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.resteasy.reactive.RestForm;

import dev.orion.users.model.User;
import dev.orion.users.repository.UserRepository;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;

/**
 * User API
 */
@Path("/api/user")
public class Service {

    @Inject
    UserRepository repo;

    /**
     * Creates a user in the service.
     *
     * @param String name : The name of the user
     * @param String email : The email of the user
     * @param String password : The password of the user
     *
     * @return The user object in JSON format
     * @throws ServiceException Returns a HTTP 409 if the e-mail already exists
     * in the database
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    public Uni<User> create(@FormParam("name") @NotEmpty String name,
            @FormParam("email") @NotEmpty @Email String email,
            @FormParam("password") @NotEmpty String password) throws ServiceException {

        return repo.checkEmail(email)
                .onItem().ifNotNull()
                    .failWith(new ServiceException("The e-mail already exists", Response.Status.CONFLICT))
                .onItem().ifNull().switchTo(() ->  repo.createUser(name, email, password));
    }

    /**
     * Authenticates the user.
     *
     * @param email : The e-mail of the user
     * @param password : The password of the user
     *
     * @return A JWT (JSON Web Token)
     * @throws ServiceException Returns a HTTP 401 if the services is not able
     * to find the user in the database
     */
    @POST
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Retry(maxRetries = 1, delay = 2000)
    public Uni<String> login(@RestForm @NotEmpty @Email String email,
            @RestForm @NotEmpty String password) {

        return repo.login(email, password)
            .onItem().ifNotNull().transform(this::generateJWT)
            .onItem().ifNull().failWith(new ServiceException("User not found", Response.Status.UNAUTHORIZED));
    }

    /**
     * Creates a JWT (JSON Web Token) to a user.
     *
     * @param user : The user object
     *
     * @return Returns the JWT
     */
    private String generateJWT(User user) {
        return Jwt.issuer("http://localhost:8080")
                .upn(user.getEmail())
                .groups(new HashSet<>(Arrays.asList("User")))
                .claim(Claims.full_name, user.getEmail())
                .sign();
    }

}
