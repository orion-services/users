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
package dev.orion.users.adapters.gateways.repository

import dev.orion.users.adapters.gateways.entities.WebAuthnCredentialEntity
import io.quarkus.hibernate.reactive.panache.Panache
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

/**
 * Implementation of WebAuthnCredentialRepository.
 */
@ApplicationScoped
class WebAuthnCredentialRepositoryImpl : WebAuthnCredentialRepository {

    /**
     * Finds a credential by credential ID.
     *
     * @param credentialId The credential ID
     * @return A Uni<WebAuthnCredentialEntity> object
     */
    override fun findByCredentialId(credentialId: String): Uni<WebAuthnCredentialEntity> {
        return (this as io.quarkus.hibernate.reactive.panache.PanacheRepository<WebAuthnCredentialEntity>)
            .find("credentialId", credentialId)
            .firstResult<WebAuthnCredentialEntity>()
    }

    /**
     * Finds all credentials for a user.
     *
     * @param userEmail The user's email
     * @return A Uni<List<WebAuthnCredentialEntity>> object
     */
    override fun findByUserEmail(userEmail: String): Uni<List<WebAuthnCredentialEntity>> {
        return (this as io.quarkus.hibernate.reactive.panache.PanacheRepository<WebAuthnCredentialEntity>)
            .find("userEmail", userEmail)
            .list<WebAuthnCredentialEntity>()
    }

    /**
     * Saves or updates a credential.
     *
     * @param credential The credential entity
     * @return A Uni<WebAuthnCredentialEntity> object
     */
    override fun saveCredential(credential: WebAuthnCredentialEntity): Uni<WebAuthnCredentialEntity> {
        return Panache.withTransaction { credential.persist() }
            .onItem().transform { credential }
    }

    /**
     * Deletes a credential.
     *
     * @param credentialId The credential ID
     * @return A Uni<Void> object
     */
    override fun deleteCredential(credentialId: String): Uni<Void> {
        return findByCredentialId(credentialId)
            .onItem().ifNull()
            .failWith(IllegalArgumentException("Credential not found"))
            .onItem().ifNotNull()
            .transformToUni { credential ->
                Panache.withTransaction<Void> { credential.delete() }
            }
    }
}

