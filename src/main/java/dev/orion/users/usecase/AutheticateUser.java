package dev.orion.users.usecase;
import dev.orion.users.infra.repository.Repository;

import org.apache.commons.codec.digest.DigestUtils;
import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.UserRepository;
import io.smallrye.mutiny.Uni;
public class AutheticateUser implements UseCase{
     /** The minimum size of the password required. */
     private static final int SIZE_PASSWORD = 8;

     /** User repository. */
     protected Repository repository = new UserRepository();

    @Override
    public Uni<User> createUser(String name, String email, String password) {
        return null;
    }

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
