package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.Encrypter;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.AuthenticateUserDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.AuthenticateUser;
import dev.orion.users.presentation.services.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import lombok.val;

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
        val user = repository.findByEmail(userDto.email);

        if(user == null){
            throw new ServiceException("User not found",
                    Response.Status.UNAUTHORIZED);
        }

        boolean userValidated = this.encrypter.validate(userDto.password, user.getPassword());
        return userValidated ? user : null;
    }

}
