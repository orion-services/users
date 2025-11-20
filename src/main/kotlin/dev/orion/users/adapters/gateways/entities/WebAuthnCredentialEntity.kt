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
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

/**
 * WebAuthn Credential Entity.
 * Stores WebAuthn credentials (passkeys) for users.
 */
@Entity
@Table(name = "WebAuthnCredential")
class WebAuthnCredentialEntity : PanacheEntityBase() {

    /** Primary key. */
    @Id
    @GeneratedValue
    @JsonIgnore
    var id: Long? = null

    /** The user's email (foreign key reference). */
    @NotNull(message = "The user email can't be null")
    @Column(name = "user_email")
    var userEmail: String? = null

    /** The credential ID (base64 encoded). */
    @NotNull(message = "The credential ID can't be null")
    @Column(name = "credential_id", length = 512)
    var credentialId: String? = null

    /** The public key (JSON string). */
    @NotNull(message = "The public key can't be null")
    @Column(name = "public_key", columnDefinition = "TEXT")
    var publicKey: String? = null

    /** The signature counter. */
    @NotNull(message = "The counter can't be null")
    @Column(name = "counter")
    var counter: Long = 0

    /** The name/description of the device. */
    @Column(name = "device_name", length = 256)
    var deviceName: String? = null
}

