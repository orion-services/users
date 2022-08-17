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
package dev.orion.users.infra.repository;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import dev.orion.users.domain.model.User;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;

/**
 * Implements the repository pattern for the user entity.
 */
@ApplicationScoped
public class UserRepository implements Repository {

  /**
   * Creates a user in the service.
   *
   * @param name     : A name of the user
   * @param email    : A valid e-mail
   * @param password : A password of the user
   *
   * @return Returns a user asynchronously
   */
  @Override
  public Uni<User> createUser(final String name, final String email,
      final String password) {

      return checkEmail(email)
        .onItem()
        .ifNotNull()
        .failWith(new IllegalArgumentException("The e-mail already exists"))
        .onItem()
        .ifNull()
        .switchTo(() -> persistUser(name, email, password));
  }

  /**
   * Verifies if the e-mail already exists in the database.
   *
   * @param email : An e-mail address
   *
   * @return Returns true if the e-mail already exists
   */
  private Uni<User> checkEmail(final String email) {
    return find("email", email).firstResult();
  }

  /**
   * Persists a user in the service.
   *
   * @param name  : The name of the user
   * @param email : An e-mail address of the a user
   * @param password : The password of the user
   *
   * @return Returns Uni<User> object
   */
  private Uni<User> persistUser(final String name, final String email,
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
  @Override
  public Uni<User> authenticate(final String email, final String password) {
      Map<String, Object> params = Parameters.with("email", email)
        .and("password", password).map();
    return find("email = :email and password = :password", params)
        .firstResult();
  }

}
