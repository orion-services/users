package dev.orion.users.usecase;

import dev.orion.users.domain.model.User;
import dev.orion.users.infra.repository.Repository;
import dev.orion.users.infra.repository.UserRepository;
import io.smallrye.mutiny.Uni;

public class RemoveUser implements UseCase {

    private Repository repository = new UserRepository();

    @Override
    public Uni<Long> removeUser(String hash) {
        return repository.removeUser(hash);
    }
}
