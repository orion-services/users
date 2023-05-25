package dev.orion.users.domain.usecases;

import dev.orion.users.domain.model.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface CreateUser {
    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return A Uni<User> object
     */
    Uni<User> createUser(String name, String email, String password);

    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name         : The name of the user
     * @param email        : The e-mail of the user
     * @param isEmailValid : Confirm if the e-mail is valid or not
     * @return A Uni<User> object
     */
    Uni<User> createUser(String name, String email, Boolean isEmailValid);
}
