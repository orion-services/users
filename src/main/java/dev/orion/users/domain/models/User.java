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
package dev.orion.users.domain.models;

import java.util.UUID;

import dev.orion.users.domain.vo.Email;

public class User {

    /** The name of the user. */
    private String name;

    /** The e-mail of the user. */
    private Email email;

    private String userId;

    private String password;

    private String status;

    private String roles;

    private String scopes;

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public void setEmail(String address) {
        this.email.setAddress(address);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * User constructor.
     */
    public User() {
    }

    public User(String name, String email, String password) {
        this();
        this.name = this.validateName(name);
        this.userId = UUID.randomUUID().toString();
        this.email = new Email(email);
        this.password = this.validatePassword(password);
        this.status = StatusEnum.ACTIVATED.name();
    }

    @Override
    public String toString() {
        return "User{" +
                "name:'" + name + '\'' +
                ", email:'" + email + '\'' +
                ", hash:'" + userId + '\'' +
                ", password:'" + password + '\'' +
                ", status:'" + status + '\'' +
                '}';
    }

    private String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is invalid!");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("Password is lass than eigth caracters!");
        }
        return password;
    }

    private String validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is invalid!");
        }
        return name;
    }
}
