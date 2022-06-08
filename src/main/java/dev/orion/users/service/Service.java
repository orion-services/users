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

import javax.inject.Inject;
import javax.transaction.Transactional;
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

import dev.orion.users.model.User;
import dev.orion.users.repository.UserRepository;

/**
 * User API
 */
@Path("/api/user")
@Transactional
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
     * in the data base
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    public User create(@FormParam("name") @NotEmpty String name,
                       @FormParam("email") @NotEmpty @Email String email,
                       @FormParam("password") @NotEmpty String password) throws ServiceException{

        User user = new User();
        if (!repo.checkEmail(email)){
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            repo.persist(user);
        }
        else{
            throw new ServiceException("The e-mail already exists", Response.Status.CONFLICT);
        }
        return user;
    }

}
