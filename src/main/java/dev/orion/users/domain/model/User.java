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
package dev.orion.users.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.Setter;

/**
 * User Entity.
 */
@Entity
@Getter
@Setter
public class User extends PanacheEntityBase {

    /** Default size for column. */
    private static final int COLUMN_LENGTH = 256;

    /** Primary key. */
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    /** The hash used to identify the user. */
    private String hash;

    /** The name of the user. */
    @NotNull(message = "The name can't be null")
    private String name;

    /** The e-mail of the user. */
    @NotNull(message = "The e-mail can't be null")
    @Email(message = "The e-mail format is necessary")
    @Column(unique = true)
    private String email;

    /** The password of the user. */
    @JsonIgnore
    @Column(length = COLUMN_LENGTH)
    @NotNull(message = "The password can't be null")
    private String password;

    /** Role list. */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    /** Stores if the e-mail was validated. */
    private boolean emailValid;

    /** The hash used to identify the user. */
    @JsonIgnore
    private String emailValidationCode;

    /** Stores if is using 2FA */
    private boolean isUsing2FA;

    /** Secret code to be used at 2FA validation */
    @JsonIgnore
    private String secret2FA;

    // non-owning side, so we can add more credentials later
    @JsonIgnore
    @OneToOne(mappedBy = "user")
    public WebAuthnCredential webAuthnCredential;

    /**
     * User constructor.
     */
    public User() {
        this.hash = UUID.randomUUID().toString();
        this.roles = new ArrayList<>();
        this.emailValidationCode = UUID.randomUUID().toString();
        // webAuthnCredential = new WebAuthnCredential();
        // webAuthnCredential.persist();
    }

    /**
     * Add a role in a user.
     *
     * @param role A role object.
     */
    public void addRole(final Role role) {
        roles.add(role);
    }

    /**
     * Transform the a list of object role to a list of String. The role "user"
     * is the default role of the server
     *
     * @return A list of roles in String format
     */
    @JsonIgnore
    public List<String> getRoleList() {
        List<String> strRoles = new ArrayList<>();
        if (this.roles.isEmpty()) {
            strRoles.add("user");
        } else {
            for (Role role : roles) {
                strRoles.add(role.getName());
            }
        }
        return strRoles;
    }

    /**
     * Generates a e-mail validation code to the user.
     */
    public void setEmailValidationCode() {
        this.emailValidationCode = UUID.randomUUID().toString();
    }

    /**
     * Removes all roles of the object.
     */
    public void removeRoles() {
        this.roles.clear();
    }

    // public long getWebAuthnCredentialCounter() {
    // return this.webAuthnCredential.counter;
    // }

    // public void setWebAuthnCredentialCounter(long counter) {
    // this.webAuthnCredential.counter = counter;
    // }
}
