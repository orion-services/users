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
package dev.orion.users.enterprise.model;

/**
 * Represents a role in the system.
 */
public class Role {

    /** The name of the role. */
    private String name;

    /**
     * Constructs a new Role object.
     */
    public Role() { }

    /**
     * Constructs a new Role object with the specified name.
     *
     * @param name the name of the role
     */
    public Role(final String name) {
        this();
        this.name = name;
    }

    /**
     * Returns the name of the role.
     *
     * @return the name of the role
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the role.
     *
     * @param name the name of the role
     */
    public void setName(final String name) {
        this.name = name;
    }

}
