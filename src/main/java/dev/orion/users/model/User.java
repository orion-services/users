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
package dev.orion.users.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.Setter;
/**
 * User Entity.
 */
@Entity
@Getter @Setter
public class User extends PanacheEntityBase {

    /** Primary key. */
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    /** The hash used to identify the user.  */
    private String hash;

    /** The name of the user. */
    @NotNull(message = "The name can't be null")
    private String name;

    /** The e-mail of the user. */
    @NotNull(message = "The e-mail can't be null")
    @Email(message = "The e-mail format is necessary")
    private String email;

     /** The password of the user. */
    @JsonIgnore
    @Column(length = 256)
    @NotNull(message = "The password can't be null")
    private String password;

    /**
     * User constructor.
     */
    public User() {
        this.hash = UUID.randomUUID().toString();
    }

}
