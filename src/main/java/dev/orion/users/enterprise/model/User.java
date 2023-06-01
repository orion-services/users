/**
 * @License
 * Copyright 2023 Orion Services @ https://github.com/orion-services
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
package dev.orion.users.enterprise.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User.
 */
public class User {

    /** The hash used to identify the user. */
    private String hash;

    /** The name of the user. */
    private String name;

    /** The e-mail of the user. */
    private String email;

    /** The password of the user. */
    private String password;

    /** Role list. */
    private List<Role> roles;

    /** Stores if the e-mail was validated. */
    private boolean emailValid;

    /** The hash used to identify the user. */
    private String emailValidationCode;

    /** Stores if is using 2FA */
    private boolean isUsing2FA;

    /** Secret code to be used at 2FA validation */
    private String secret2FA;

    /**
     * User constructor.
     */
    public User() {
        this.hash = UUID.randomUUID().toString();
        this.roles = new ArrayList<>();
        this.emailValidationCode = UUID.randomUUID().toString();
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean isEmailValid() {
        return emailValid;
    }

    public void setEmailValid(boolean emailValid) {
        this.emailValid = emailValid;
    }

    public String getEmailValidationCode() {
        return emailValidationCode;
    }

    public void setEmailValidationCode(String emailValidationCode) {
        this.emailValidationCode = emailValidationCode;
    }

    public boolean isUsing2FA() {
        return isUsing2FA;
    }

    public void setUsing2FA(boolean isUsing2FA) {
        this.isUsing2FA = isUsing2FA;
    }

    public String getSecret2FA() {
        return secret2FA;
    }

    public void setSecret2FA(String secret2fa) {
        secret2FA = secret2fa;
    }

}
