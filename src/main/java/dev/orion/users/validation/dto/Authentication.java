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
package dev.orion.users.validation.dto;

import dev.orion.users.domain.models.User;
import lombok.Getter;
import lombok.Setter;

/**
 * Authentication DTO.
 */
@Getter @Setter
public class Authentication {

    /** The user object. */
    private User user;

    /** The authentication token (jwt). */
    private String token;

}
