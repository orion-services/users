/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
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
package dev.orion.users.adapters.gateways.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import java.util.UUID

/**
 * User Entity.
 */
@Entity
@Table(name = "User")
class UserEntity : PanacheEntityBase() {

    /** Default size for column. */
    companion object {
        private const val COLUMN_LENGTH = 256
    }

    /** Primary key. */
    @Id
    @GeneratedValue
    @JsonIgnore
    var id: Long? = null

    /** The hash used to identify the user. */
    var hash: String = UUID.randomUUID().toString()

    /** The name of the user. */
    @NotNull(message = "The name can't be null")
    var name: String? = null

    /** The e-mail of the user. */
    @NotNull(message = "The e-mail can't be null")
    @Email(message = "The e-mail format is necessary")
    var email: String? = null

    /** The password of the user. */
    @JsonIgnore
    @Column(length = COLUMN_LENGTH)
    @NotNull(message = "The password can't be null")
    var password: String? = null

    /** Role list. */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    var roles: MutableList<RoleEntity> = mutableListOf()

    /** Stores if the e-mail was validated. */
    var emailValid: Boolean = false

    /** The hash used to identify the user. */
    @JsonIgnore
    var emailValidationCode: String = UUID.randomUUID().toString()

    /** Stores if is using 2FA. */
    var isUsing2FA: Boolean = false

    /** Secret code to be used at 2FA validation. */
    var secret2FA: String? = null

    /**
     * User constructor.
     */
    init {
        this.hash = UUID.randomUUID().toString()
        this.roles = mutableListOf()
        this.emailValidationCode = UUID.randomUUID().toString()
    }

    /**
     * Add a role in a user.
     *
     * @param role A role object.
     */
    fun addRole(role: RoleEntity) {
        roles.add(role)
    }

    /**
     * Transform the a list of object role to a list of String. The role "user"
     * is the default role of the server
     *
     * @return A list of roles in String format
     */
    @JsonIgnore
    fun getRoleList(): List<String> {
        val strRoles = mutableListOf<String>()
        if (this.roles.isEmpty()) {
            strRoles.add("user")
        } else {
            for (role in roles) {
                role.name?.let { strRoles.add(it) }
            }
        }
        return strRoles
    }

    /**
     * Generates a e-mail validation code to the user.
     */
    fun setEmailValidationCode() {
        this.emailValidationCode = UUID.randomUUID().toString()
    }

    /**
     * Removes all roles of the object.
     */
    fun removeRoles() {
        this.roles.clear()
    }
}

