package dev.orion.users.usecase;

import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.UserRepository;
import dev.orion.users.infra.repository.UserRepositoryImpl;
import dev.orion.users.validation.dto.UserQuery;
import io.smallrye.mutiny.Multi;

public class ListUser implements UseCase {

    private UserRepository repository = new UserRepositoryImpl();

    /**
     * @return
     */
    @Override
    public Multi<User> listUser(UserQuery query) {
        return repository.listByQuery(query);
    }
}
