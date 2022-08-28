package dev.orion.users.usecase;

import dev.orion.users.infra.repository.UserRepository;

import org.apache.commons.codec.digest.DigestUtils;
import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthenticateUser implements UseCase {

    /** User repository. */
    protected UserRepository repository = new UserRepositoryImpl();

    /**
     * Authenticates the user in the service (UC: Authenticate).
     *
     * @param email    : The email of the user
     * @param password : The password of the user
     * @return A Uni<User> object
     */
    @Override
    public Uni<User> authenticate(final String email, final String password) {
        Uni<User> user = null;
        if ((email != null) && (password != null)) {
            user = repository.authenticate(email,
                    DigestUtils.sha256Hex(password));
        } else {
            throw new IllegalArgumentException("All arguments are required");
        }
        return user;
    }
}
