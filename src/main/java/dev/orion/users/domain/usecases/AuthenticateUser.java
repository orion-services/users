package dev.orion.users.domain.usecases;

import dev.orion.users.domain.model.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface AuthenticateUser {
    /**
     * Authenticates the user in the service (UC: Authenticate).
     *
     * @param email    : The email of the user
     * @param password : The password of the user
     * @return An Uni<User> object
     */
    Uni<User> authenticate(String email, String password);

    /**
     * Generates a new password of a user.
     *
     * @param email : The e-mail of the user
     * @return A new password
     * @throws IllegalArgumentException if the user informs a blank e-mail
     */
    Uni<String> recoverPassword(String email);
}
