package dev.orion.users.data.usecases;

import org.apache.commons.codec.digest.DigestUtils;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.usecases.AuthenticateUser;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthenticateUserImpl implements AuthenticateUser {
    /** Default blanck arguments message. */
    private static final String BLANK = "Blank Arguments";

    @Inject
    protected UserRepository repository;

    /**
     * Authenticates the user in the service (UC: Authenticate).
     *
     * @param email    : The email of the user
     * @param password : The password of the user
     * @return An Uni<User> object
     */
    @Override
    public Uni<User> authenticate(final String email, final String password) {
        if (email != null && password != null) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(DigestUtils.sha256Hex(password));
            return repository.authenticate(user);
        } else {
            throw new IllegalArgumentException("All arguments are required");
        }
    }

    /**
     * Validates an e-mail of a user.
     *
     * @param email : The e-mail of a user
     * @param code  : The validation code
     * @return true if the validation code is correct for the respective e-mail
     */
    public Uni<User> validateEmail(final String email, final String code) {
        if (email.isBlank() || code.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            return repository.validateEmail(email, code);
        }
    }

    /**
     * Generates a new password of a user.
     *
     * @param email : The e-mail of the user
     * @return A new password
     * @throws IllegalArgumentException if the user informs a blank e-mail
     */
    @Override
    public Uni<String> recoverPassword(final String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            return repository.recoverPassword(email);
        }
    }

}
