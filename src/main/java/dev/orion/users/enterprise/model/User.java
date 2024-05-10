/**
 * @License
 * Copyright 2024 Orion Services @ https://github.com/orion-services
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
 * Represents a user in the system.
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

    /** Stores if is using 2FA. */
    private boolean isUsing2FA;

    /** Secret code to be used at 2FA validation. */
    private String secret2FA;

    /**
     * User constructor. Initializes the user with a unique hash, an empty role
     * list, and a random email validation code.
     */
    public User() {
        this.hash = UUID.randomUUID().toString();
        this.roles = new ArrayList<>();
        this.emailValidationCode = UUID.randomUUID().toString();
    }

    /**
     * Add a role to the user.
     *
     * @param role The role to be added.
     */
    public void addRole(final Role role) {
        roles.add(role);
    }

    /**
     * Get the list of roles assigned to the user.
     *
     * @return A list of roles in String format.
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
     * Generates a new email validation code for the user.
     */
    public void setEmailValidationCode() {
        this.emailValidationCode = UUID.randomUUID().toString();
    }

    /**
     * Removes all roles assigned to the user.
     */
    public void removeRoles() {
        this.roles.clear();
    }

    /**
     * Get the hash of the user.
     *
     * @return The hash of the user.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Set the hash of the user.
     *
     * @param hash The hash of the user.
     */
    public void setHash(final String hash) {
        this.hash = hash;
    }

    /**
     * Get the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the user.
     *
     * @param name The name of the user.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the email of the user.
     *
     * @return The email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email of the user.
     *
     * @param email The email of the user.
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Get the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password of the user.
     *
     * @param password The password of the user.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Get the list of roles assigned to the user.
     *
     * @return The list of roles assigned to the user.
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Set the list of roles assigned to the user.
     *
     * @param roles The list of roles assigned to the user.
     */
    public void setRoles(final List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Check if the user's email is validated.
     *
     * @return True if the email is validated, false otherwise.
     */
    public boolean isEmailValid() {
        return emailValid;
    }

    /**
     * Set the email validation status of the user.
     *
     * @param emailValid True if the email is validated, false otherwise.
     */
    public void setEmailValid(final boolean emailValid) {
        this.emailValid = emailValid;
    }

    /**
     * Get the email validation code of the user.
     *
     * @return The email validation code of the user.
     */
    public String getEmailValidationCode() {
        return emailValidationCode;
    }

    /**
     * Set the email validation code of the user.
     *
     * @param emailValidationCode The email validation code of the user.
     */
    public void setEmailValidationCode(final String emailValidationCode) {
        this.emailValidationCode = emailValidationCode;
    }

    /**
     * Check if the user is using 2FA (Two-Factor Authentication).
     *
     * @return True if the user is using 2FA, false otherwise.
     */
    public boolean isUsing2FA() {
        return isUsing2FA;
    }

    /**
     * Set the 2FA status of the user.
     *
     * @param isUsing2FA True if the user is using 2FA, false otherwise.
     */
    public void setUsing2FA(final boolean isUsing2FA) {
        this.isUsing2FA = isUsing2FA;
    }

    /**
     * Get the secret code used for 2FA validation.
     *
     * @return The secret code used for 2FA validation.
     */
    public String getSecret2FA() {
        return secret2FA;
    }

    /**
     * Set the secret code used for 2FA validation.
     *
     * @param secret2fa The secret code used for 2FA validation.
     */
    public void setSecret2FA(final String secret2fa) {
        secret2FA = secret2fa;
    }

}
