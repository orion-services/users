/**
 * @License
 * Copyright 2025 Orion Services @ https://orion-services.dev
 */
package dev.orion.users.adapters.out.persistence

import dev.orion.users.adapters.gateways.entities.UserEntity
import dev.orion.users.domain.model.Role
import dev.orion.users.domain.model.User
import jakarta.enterprise.context.ApplicationScoped

/**
 * Maps between JPA [UserEntity] and domain [User].
 */
@ApplicationScoped
class UserEntityMapper {
    fun toDomain(entity: UserEntity): User {
        val u = User()
        u.id = entity.id
        u.hash = entity.hash
        u.name = entity.name
        u.email = entity.email
        u.password = entity.password
        u.emailValid = entity.emailValid
        u.emailValidationCode = entity.emailValidationCode
        u.using2FA = entity.isUsing2FA
        u.secret2FA = entity.secret2FA
        u.require2FAForBasicLogin = entity.require2FAForBasicLogin
        u.require2FAForSocialLogin = entity.require2FAForSocialLogin
        u.roles.clear()
        entity.roles.forEach { re -> u.addRole(Role(name = re.name)) }
        return u
    }

    /**
     * Maps domain user to a new entity for create/update flows.
     * Role assignment for new users is handled by persistence (default role).
     */
    fun toEntity(domain: User): UserEntity {
        val e = UserEntity()
        e.id = domain.id
        e.hash = domain.hash
        e.name = domain.name
        e.email = domain.email
        e.password = domain.password
        e.emailValid = domain.emailValid
        e.emailValidationCode = domain.emailValidationCode
        e.isUsing2FA = domain.using2FA
        e.secret2FA = domain.secret2FA
        e.require2FAForBasicLogin = domain.require2FAForBasicLogin
        e.require2FAForSocialLogin = domain.require2FAForSocialLogin
        e.roles = mutableListOf()
        return e
    }
}
