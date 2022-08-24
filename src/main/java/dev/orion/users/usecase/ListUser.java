package dev.orion.users.usecase;

import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.Repository;
import dev.orion.users.infra.repository.UserRepository;
import dev.orion.users.validation.dto.UserQuery;
import io.smallrye.mutiny.Uni;

import java.util.List;

public class ListUser implements UseCase {

    private Repository repository = new UserRepository();

    /**
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return
     */
    @Override
    public Uni<User> createUser(String name, String email, String password) {
        return null;
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
     * @return
     */
    @Override
    public Uni<List<User>> listUser(UserQuery query) {

        return repository.listByQuery(query);
    }

    /**
     * @return
     */
    @Override
    public Uni<User> removeUser() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public Uni<User> deleteUser() {
        return null;
    }
}
