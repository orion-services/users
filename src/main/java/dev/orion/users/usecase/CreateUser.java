package dev.orion.users.usecase;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;

import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.Repository;
import dev.orion.users.infra.repository.UserRepository;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.common.NotImplementedYet;

@ApplicationScoped
public class CreateUser implements UseCase{
    private static final int SIZE_PASSWORD = 8;

    /** User repository. */
    private Repository repository = new UserRepository();

    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return A Uni<User> object
     */
    @Override
    public Uni<User> createUser(final String name, final String email,
            final String password) {
        Uni<User> user = null;
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email)
            || password.isBlank()) {
            throw new IllegalArgumentException("Blank arguments or invalid e-mail");
        } else {
            if (password.length() < SIZE_PASSWORD) {
                throw new IllegalArgumentException(
                        "Password less than eight characters");
            } else {
                user = repository.createUser(name, email,
                        DigestUtils.sha256Hex(password));
            }
        }
        return user;
    }

    /**
     * @param email    : The email of the user 
     * @param password : The password of the user
     * @return
     */
    @Override
    public Uni<User> authenticate(String email, String password) {
        return null;
    }

    /**
     * @param email    : The email of the user 
     * @param password : The password of the user
     * @return
     */

}