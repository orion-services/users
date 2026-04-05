/*
 * Copyright 2026 Orion Services.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.orion.users.domain.model

import java.util.UUID

/**
 * Represents a user in the system (domain — no framework dependencies).
 */
class User {
    /** Database id when loaded from persistence. */
    var id: Long? = null

    /** The hash used to identify the user. */
    var hash: String = UUID.randomUUID().toString()

    /** The name of the user. */
    var name: String? = null

    /** The e-mail of the user. */
    var email: String? = null

    /** The password of the user (hash). */
    var password: String? = null

    /** Role list. */
    var roles: MutableList<Role> = mutableListOf()

    /** Stores if the e-mail was validated. */
    var emailValid: Boolean = false

    /** The hash used to identify the user. */
    var emailValidationCode: String = UUID.randomUUID().toString()

    /** Stores if is using 2FA. */
    var using2FA: Boolean = false

    /** Secret code to be used at 2FA validation. */
    var secret2FA: String? = null

    /** Controls if 2FA is required for basic login (email/password). */
    var require2FAForBasicLogin: Boolean = false

    /** Controls if 2FA is required for social login (Google OAuth). */
    var require2FAForSocialLogin: Boolean = false

    init {
        this.hash = UUID.randomUUID().toString()
        this.roles = mutableListOf()
        this.emailValidationCode = UUID.randomUUID().toString()
    }

    /** Add a role to the user. */
    fun addRole(role: Role) {
        roles.add(role)
    }

    /**
     * Role names for JWT / authorization (default "user" when empty).
     */
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

    /** Generates a new email validation code for the user. */
    fun setEmailValidationCode() {
        this.emailValidationCode = UUID.randomUUID().toString()
    }

    /** Removes all roles assigned to the user. */
    fun removeRoles() {
        this.roles.clear()
    }
}
