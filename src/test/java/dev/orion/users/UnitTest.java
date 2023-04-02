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
package dev.orion.users;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.orion.users.model.User;
import dev.orion.users.repository.Repository;
import dev.orion.users.usecase.UserUC;
import io.smallrye.mutiny.Uni;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class UnitTest {

  @Mock
  Repository repository;

  @InjectMocks
  UserUC uc;

  @Test
  @DisplayName("Create a user")
  @Order(1)
  void createUserTest() {
    // Mockito.when(repository.createUser("Orion", "orion@test.com", DigestUtils.sha256Hex("12345678")))
    //   .thenReturn(Uni.createFrom().item(new User()));
    // Uni<User> uni = uc.createUser("Orion", "orion@test.com", "12345678");
    // assertNotNull(uni);
  }

  @Test
  @DisplayName("Create a user with a blank name")
  @Order(2)
  void createUserWithBlankName() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.createUser("", "orion@test.com", "12345678");
    });
  }

  @Test
  @DisplayName("Create a user with a blank name")
  @Order(3)
  void createUserWithBlankEmail() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.createUser("Orion", "", "12345678");
    });
  }

  @Test
  @DisplayName("Create a user with a blank password")
  @Order(4)
  void createUserWithBlankPassword() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.createUser("Orion", "orion@test.com", "");
    });
  }

  @Test
  @DisplayName("Create a user with an invalid e-mail")
  @Order(5)
  void createUserWithInvalidEmail() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.createUser("Orion", "orion#test.com", "12345678");
    });
  }

  @Test
  @DisplayName("Create a user with invalid password")
  @Order(6)
  void createUserWithInvalidPasswordTest() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.createUser("Orion", "orion@test.com", "12345");
    });
  }

  @Test
  @DisplayName("Create a user with a null name")
  @Order(7)
  void createUserWithNullName() {
    Assertions.assertThrows(NullPointerException.class, () -> {
      uc.createUser(null, "orion#test.com", "12345678");
    });
  }

  @Test
  @DisplayName("Change email")
  @Order(8)
  void changeEmail() {
    Mockito.when(repository.updateEmail("orion@test.com", "newOrion@test.com"))
      .thenReturn(Uni.createFrom().item(new User()));
    Uni<User> uni = uc.updateEmail("orion@test.com", "newOrion@test.com");
    assertNotNull(uni);
  }

  @Test
  @DisplayName("Change email")
  @Order(9)
  void changeEmailWithBlankArguments() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.updateEmail("", "orion@test.com");
    });
  }

  @Test
  @DisplayName("Change password with blank arguments")
  @Order(10)
  void changePasswordWithBlankArguments() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.updatePassword("", "1234", "12345678");
    });
  }

  @Test
  @DisplayName("Recover password")
  @Order(11)
  void recoverPassword() {
    Mockito.when(repository.recoverPassword("orion@test.com"))
      .thenReturn(Uni.createFrom().item("ok"));
    Uni<String> uni = uc.recoverPassword("orion@test.com");
    assertNotNull(uni);
  }

  @Test
  @DisplayName("Recover password with blank arguments")
  @Order(12)
  void recoverPasswordWithBlankArguments() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.recoverPassword("");
    });
  }

}