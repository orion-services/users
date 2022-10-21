package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.Encrypter;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.AuthenticateUserDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.AuthenticateUser;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class AuthenticateUserImpl implements AuthenticateUser {

    private UserRepository repository;
    private Encrypter encrypter;

    public AuthenticateUserImpl(UserRepository repository, Encrypter encrypter) {
        this.repository = repository;
        this.encrypter = encrypter;
    }

    /**
     * Authenticates the user in the service (UC: Authenticate).
     *
     */
    @Override
    @Transactional
    public User authenticate(AuthenticateUserDto userDto) {
        User user = null;
        if ((userDto.email != null) && (userDto.password != null)) {
            user = repository.findByEmail(userDto.email);
            boolean userValidated = this.encrypter.validate(user.getPassword());
            return userValidated ? user : null;
        } else {
            throw new IllegalArgumentException("All arguments are required");
        }
    }

}
