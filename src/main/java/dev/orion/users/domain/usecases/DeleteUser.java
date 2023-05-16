package dev.orion.users.domain.usecases;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface DeleteUser {
    /**
     * Deletes a User from the service.
     *
     * @param email : User email
     *
     * @return Return 1 if user was deleted
     */
    Uni<Void> deleteUser(String email);

}
