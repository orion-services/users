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
     * @return
     */
    @Override
    public Uni<List<User>> listUser(UserQuery query) {
        return repository.listByQuery(query);
    }
}
