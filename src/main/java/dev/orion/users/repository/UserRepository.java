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

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.codec.digest.DigestUtils;

import dev.orion.users.model.User;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;

/**
 * Implements the repository pattern for the user entity.
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

  /**
   * Verifies if the e-mail already exists in the database.
   *
   * @param email : An e-mail address
   *
   * @return Returns true if the e-mail already exists
   */
  public Uni<User> checkEmail(final String email) {
    return find("email", email).firstResult();
  }

  /**
   * Creates a user in the database.
   *
   * @param name     : A name of the user
   * @param email    : A valid e-mail
   * @param password : A password of the user
   *
   * @return Returns a user asynchronously
   */
  public Uni<User> createUser(final String name, final String email,
                              final String password) {

    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(password);
    return Panache.<User>withTransaction(user::persist);
  }

  /**
   * Returns a user looking for email and password.
   *
   * @param email    : An e-mail of the user
   * @param password : A password
   *
   * @return Returns a user asynchronously
   */
  public Uni<User> login(final String email, final String password) {
    String shaPassword = DigestUtils.sha256Hex(password);
    Map<String, Object> params = Parameters.with("email", email)
      .and("password", shaPassword).map();

    return find("email = :email and password = :password", params)
      .firstResult();
  }

}
