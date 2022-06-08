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
package dev.orion.users.repository;

import javax.enterprise.context.ApplicationScoped;

import dev.orion.users.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

/**
 * Implements the repository pattern for the user entity
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    /**
     * Verifies if the e-mail already exists in the database
     *
     * @param email : An e-mail address
     *
     * @return Returns true if the e-mail already exists
     */
    public boolean checkEmail(String email){
        boolean exists = true;
        User user = find("email", email).firstResult();
        if(user == null)
            exists = false;
        return exists;
    }

}
