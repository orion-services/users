package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.usecases.DeleteUser;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DeleteUserImpl implements DeleteUser {

    @Inject
    protected UserRepository repository;

    /**
     * Deletes a User from the service.
     *
     * @param email : User email
     *
     * @return Return 1 if user was deleted
     */
    @Override
    public Uni<Void> deleteUser(final String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email can not be blank");
        } else {
            return repository.deleteUser(email);
        }
    }

}
