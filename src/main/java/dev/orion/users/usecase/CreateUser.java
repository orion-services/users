package dev.orion.users.usecase;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;

import dev.orion.users.domain.model.User;
import dev.orion.users.domain.model.UserData;
import dev.orion.users.infra.repository.UserRepository;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class CreateUser implements UseCase {
    private static final int SIZE_PASSWORD = 8;

    /** User repository. */
    private UserRepository repository = new UserRepositoryImpl();

    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return A Uni<User> object
     */
    @Override
    public Uni<User> createUser(UserData userData) {
        Uni<User> user = null;
        if (userData.getEmail().isBlank() || !EmailValidator.getInstance().isValid(userData.getEmail())
                || userData.getPassword().isBlank()) {
            throw new IllegalArgumentException("Blank arguments or invalid e-mail");
        } else {
            if (userData.getPassword().length() < SIZE_PASSWORD) {
                throw new IllegalArgumentException(
                        "Password less than eight characters");
            } else {
                userData.setPassword(DigestUtils.sha256Hex(userData.getPassword()));
                user = repository.createUser(userData);
            }
        }
        return user;
    }
}