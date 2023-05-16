package dev.orion.users.data.usecases;

import org.apache.commons.codec.digest.DigestUtils;

import dev.orion.users.data.interfaces.Repository;
import dev.orion.users.domain.model.User;
import dev.orion.users.domain.usecases.UpdateUser;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class UpdateUserImpl implements UpdateUser {
    /** Default blanck arguments message. */
    private static final String BLANK = "Blank Arguments";

    @Inject
    private Repository repository;

    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     * @return An Uni<User> object
     */
    @Override
    public Uni<User> updateEmail(final String email, final String newEmail) {
        Uni<User> user = null;
        if (email.isBlank() || newEmail.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            user = repository.updateEmail(email, newEmail);
        }
        return user;
    }

    /**
     * Changes User password.
     *
     * @param password    : Actual password
     * @param newPassword : New Password
     * @param email       : User's email
     * @return Returns a user asynchronously
     */
    @Override
    public Uni<User> updatePassword(final String email, final String password,
            final String newPassword) {
        if (password.isBlank() || newPassword.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException(BLANK);
        } else {
            return repository.changePassword(DigestUtils.sha256Hex(password),
                    DigestUtils.sha256Hex(newPassword), email);
        }
    }

    @Override
    public Uni<User> updateUser(User user) {
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return repository.updateUser(user);
    }

}
