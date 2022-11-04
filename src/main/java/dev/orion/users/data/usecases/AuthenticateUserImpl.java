package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.Encrypter;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.AuthenticateUserDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.AuthenticateUser;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class AuthenticateUserImpl implements AuthenticateUser {

    @Inject
    protected UserRepository repository;

    @Inject
    protected Encrypter encrypter;

    /**
     * Authenticates the user in the service (UC: Authenticate).
     *
     */
    @Override
    @Transactional
    public User authenticate(AuthenticateUserDto userDto) {
        User user = repository.findByEmail(userDto.email);
        boolean userValidated = this.encrypter.validate(userDto.password, user.getPassword());
        return userValidated ? user : null;
    }

}
