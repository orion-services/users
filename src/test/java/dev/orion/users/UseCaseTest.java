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

import org.apache.commons.codec.digest.DigestUtils;
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
class UseCaseTest {

  @Mock
  Repository repository;

  @InjectMocks
  UserUC uc;

  @Test
  @DisplayName("Create a user")
  @Order(1)
  public void createUserTest() {
    Mockito.when(repository.createUser("Orion", "orion@teste.com", DigestUtils.sha256Hex("12345678")))
      .thenReturn(Uni.createFrom().item(new User()));
    Uni<User> uni = uc.createUser("Orion", "orion@teste.com", "12345678");
    assertNotNull(uni);
  }

  @Test
  @DisplayName("Create a user with a null name")
  @Order(2)
  public void createUserWithInvalidNameTest() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.createUser(null, "email", "password");
    });
  }

  @Test
  @DisplayName("Create a user with invalid password")
  @Order(3)
  public void createUserWithInvalidPasswordTest() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      uc.createUser("Orion", "orion@test.com", "12345");
    });
  }

}