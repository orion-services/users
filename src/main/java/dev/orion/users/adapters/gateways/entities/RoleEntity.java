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
package dev.orion.users.adapters.gateways.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.Setter;

/**
 * Role Entity.
 */
@Entity
@Getter
@Setter
@Table(name = "Role")
public class RoleEntity extends PanacheEntityBase {

    /** Primary key. */
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    /** The name of the role. */
    @NotNull(message = "The name of the role can't be null")
    private String name;

    /**
     * Default constructor for RoleEntity.
     */
    public RoleEntity() {
    }

    /**
     * Constructor for RoleEntity with name parameter.
     *
     * @param name The name of the role.
     */
    public RoleEntity(final String name) {
        this();
        this.name = name;
    }
}
