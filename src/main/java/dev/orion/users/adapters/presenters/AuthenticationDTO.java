/**
 * @License
 * Copyright 2024 Orion Services @ https://orion-services.dev
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
package dev.orion.users.adapters.presenters;


import dev.orion.users.adapters.gateways.entities.UserEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Authentication DTO.
 */
@Getter
@Setter
public class AuthenticationDTO {

    /** The user object. */
    private UserEntity user;

    /** The authentication token (jwt). */
    private String token;

}
