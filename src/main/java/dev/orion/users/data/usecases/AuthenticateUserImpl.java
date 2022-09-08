package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.AuthenticateUserDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.AuthenticateUser;
import dev.orion.users.infra.repositories.PanacheUserRepository;


import org.apache.commons.codec.digest.DigestUtils;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AuthenticateUserImpl implements AuthenticateUser {

    /** User repository. */
    protected UserRepository repository;

    public AuthenticateUserImpl(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Authenticates the user in the service (UC: Authenticate).
     *
     * @return A Uni<User> object
     */
    @Override
    public User authenticate(AuthenticateUserDto userDto) {
        User user = null;
        if ((userDto.email != null) && (userDto.password != null)) {
            userDto.password = DigestUtils.sha256Hex(userDto.password);
            user = repository.authenticate(userDto);
        } else {
            throw new IllegalArgumentException("All arguments are required");
        }
        return user;
    }

}
