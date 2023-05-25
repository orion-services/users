package dev.orion.users.data.usecases;

import dev.orion.users.data.handlers.TwoFactorAuthHandler;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.usecases.CreateUser;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;

@ApplicationScoped
public class CreateUserImpl implements CreateUser {

    /** The minimum size of the password required. */
    private static final int SIZE_PASSWORD = 8;

    @Inject
    protected TwoFactorAuthHandler twoFactorAuthHandler;

    @Inject
    protected UserRepository repository;

    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return An Uni<User> object
     */
    @Override
    public Uni<User> createUser(final String name, final String email,
            final String password) {
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email)
                || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Blank arguments or invalid e-mail");
        } else {
            if (password.length() < SIZE_PASSWORD) {
                throw new IllegalArgumentException(
                        "Password less than eight characters");
            } else {
                User user = new User();
                String secretKey = twoFactorAuthHandler.generateSecretKey();
                user.setName(name);
                user.setEmail(email);
                user.setPassword(DigestUtils.sha256Hex(password));
                user.setEmailValid(false);
                user.setSecret2FA(secretKey);
                return repository.createUser(user);
            }
        }
    }

    /**
     * Creates a user in the service (UC: Authenticate With Google).
     *
     * @param name         : The name of the user
     * @param email        : The e-mail of the user
     * @param isEmailValid : Informs if the e-mail is valid
     * @return An Uni<User> object
     */
    @Override
    public Uni<User> createUser(final String name, final String email,
            final Boolean isEmailValid) {
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(
                    "Blank arguments or invalid e-mail");
        } else {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setEmailValid(isEmailValid);
            return repository.createUser(user);
        }
    }

}
