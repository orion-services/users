package dev.orion.users.data.usecases;

import org.apache.commons.codec.digest.DigestUtils;

import dev.orion.users.data.interfaces.Repository;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.usecases.AuthenticateUser;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

public class AuthenticateUserImpl implements AuthenticateUser {
    /** Default blanck arguments message. */
    private static final String BLANK = "Blank Arguments";

    @Inject
    private Repository repository;

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
