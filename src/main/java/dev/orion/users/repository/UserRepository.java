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
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import dev.orion.users.model.User;
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

  /**
   * Changes User password.
   *
   * @param password    : Actual password
   * @param newPassword : New Password
   * @param email       : User's email
   *
   * @return Returns a user asynchronously
   */
  @Override
  public Uni<User> changePassword(
    final String password,
    final String newPassword,
    final String email) {
      return checkEmail(email)
      .onItem().ifNull()
      .failWith(new IllegalArgumentException("User not found"))
      .onItem().ifNotNull()
      .transformToUni(user -> {
        user.setPassword(password == user.getPassword()
        ? newPassword : user.getPassword());
        return Panache.<User>withTransaction(user::persist);
      });
  }


  @Override
  public Uni<String> recoverPassword(String email) {
    String password = generateSecurePassword();
    return checkEmail(email)
    .onItem().ifNull()
    .failWith(new IllegalArgumentException("Email not found"))
    .onItem().ifNotNull()
    .transformToUni(user -> changePassword(user.getPassword(), DigestUtils.sha256Hex(password), email)
    .onItem().transform(item -> {return password;}));
  }

  private static String generateSecurePassword() {

    /** Character rule for lower case characters. */
    CharacterRule LCR = new CharacterRule(EnglishCharacterData.LowerCase);
    /** Set the number of lower case characters. */
    LCR.setNumberOfCharacters(2);

    /** Character rule for uppercase characters. */
    CharacterRule UCR = new CharacterRule(EnglishCharacterData.UpperCase);
    /** Set the number of upper case characters. */
    UCR.setNumberOfCharacters(2);

    /** Character rule for digit characters. */
    CharacterRule DR = new CharacterRule(EnglishCharacterData.Digit);
    /** Set the number of digit characters. */
    DR.setNumberOfCharacters(2);

    /** Character rule for special characters. */
    CharacterData special = new CharacterData() {

      @Override
      public String getErrorCode() {
        return "Error";
      }

      @Override
      public String getCharacters() {
        return "!@#$%^&*()_+";
      }
    };
    CharacterRule SR = new CharacterRule(special);
    /** Set the number of special characters. */
    SR.setNumberOfCharacters(2);

    PasswordGenerator passGen = new PasswordGenerator();
    String password = passGen.generatePassword(8, SR, LCR, UCR, DR);

    return password;
  }

}
