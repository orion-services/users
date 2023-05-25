package dev.orion.users.domain.usecases;

import dev.orion.users.domain.model.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface UpdateUser {
    /**
     * Updates the e-mail of the user.
     *
     * @param email    : Current user's e-mail
     * @param newEmail : New e-mail
     *
     * @return An Uni<User> object
     */
    Uni<User> updateEmail(String email, String newEmail);

    /**
     * Updates the user's password.
     *
     * @param email       : User's email
     * @param password    : Current password
     * @param newPassword : New Password
     *
     * @return An Uni<User> object
     */
    Uni<User> updatePassword(String email, String password, String newPassword);

    /**
     * Updates a user.
     *
     * @param user A user object
     * @return An Uni<User> object
     */
    Uni<User> updateUser(User user);
}
